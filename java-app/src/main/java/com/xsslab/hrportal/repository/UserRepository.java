package com.xsslab.hrportal.repository;

import com.xsslab.hrportal.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> rowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            return user;
        }
    };

    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", rowMapper);
    }

    public User findByUsername(String username) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE username = ?", rowMapper, username);
        return users.isEmpty() ? null : users.get(0);
    }

    public void save(User user) {
        jdbcTemplate.update("INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                user.getUsername(), user.getPassword(), user.getRole());
    }
}
