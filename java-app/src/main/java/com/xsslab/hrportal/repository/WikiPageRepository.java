package com.xsslab.hrportal.repository;

import com.xsslab.hrportal.model.WikiPage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WikiPageRepository {
    private final JdbcTemplate jdbcTemplate;

    public WikiPageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<WikiPage> rowMapper = new RowMapper<WikiPage>() {
        @Override
        public WikiPage mapRow(ResultSet rs, int rowNum) throws SQLException {
            WikiPage w = new WikiPage();
            w.setId(rs.getInt("id"));
            w.setTitle(rs.getString("title"));
            w.setAuthor(rs.getString("author"));
            w.setContent(rs.getString("content"));
            w.setCreatedAt(rs.getTimestamp("created_at"));
            return w;
        }
    };

    public List<WikiPage> findAll() {
        return jdbcTemplate.query("SELECT * FROM wiki_pages ORDER BY created_at DESC", rowMapper);
    }

    public void save(WikiPage w) {
        jdbcTemplate.update("INSERT INTO wiki_pages (title, author, content) VALUES (?, ?, ?)",
                w.getTitle(), w.getAuthor(), w.getContent());
    }
}
