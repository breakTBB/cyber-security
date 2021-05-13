package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private JdbcTemplate template;

    public List<User> getUserByUserName(String username) {
        String sql = "select username, hash, salt from user where username = ?";
        return template.query(sql, new Object[]{username}, new BeanPropertyRowMapper(User.class));
    }

    public boolean register(String username, String pwd) {
        // hash = md5(md5(pwd) + salt)
        Random ran = new SecureRandom();
        byte[] salt = new byte[64];
        ran.nextBytes(salt);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] b = md.digest(pwd.getBytes(StandardCharsets.UTF_8));
            String hash = DatatypeConverter.printHexBinary(b);
            String sql = "insert into user values(?, ?, ?)";
            template.update(sql, username, hash, DatatypeConverter.printHexBinary(salt));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean login(String username, String pwd) {
        List<User> userList = getUserByUserName(username);
        if (userList.isEmpty()) {
            return false;
        }
        User user = userList.get(0);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(DatatypeConverter.parseHexBinary(user.getSalt()));
            byte[] b = md.digest(pwd.getBytes(StandardCharsets.UTF_8));
            String hash = DatatypeConverter.printHexBinary(b);
            System.out.println("hash: " + hash);
            System.out.println("hash in database: " + user.getHash());
            return hash.equals(user.getHash());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }
}
