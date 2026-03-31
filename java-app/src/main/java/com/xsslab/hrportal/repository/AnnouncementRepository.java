package com.xsslab.hrportal.repository;

import com.xsslab.hrportal.model.Announcement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AnnouncementRepository {
    private final JdbcTemplate jdbcTemplate;

    public AnnouncementRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Announcement> rowMapper = new RowMapper<Announcement>() {
        @Override
        public Announcement mapRow(ResultSet rs, int rowNum) throws SQLException {
            Announcement a = new Announcement();
            a.setId(rs.getInt("id"));
            a.setAuthor(rs.getString("author"));
            a.setTitle(rs.getString("title"));
            a.setContent(rs.getString("content"));
            a.setCreatedAt(rs.getTimestamp("created_at"));
            return a;
        }
    };

    public List<Announcement> findAll() {
        return jdbcTemplate.query("SELECT * FROM announcements ORDER BY created_at DESC", rowMapper);
    }

    public void save(Announcement a) {
        jdbcTemplate.update("INSERT INTO announcements (author, title, content) VALUES (?, ?, ?)",
                a.getAuthor(), a.getTitle(), a.getContent());
    }
}
