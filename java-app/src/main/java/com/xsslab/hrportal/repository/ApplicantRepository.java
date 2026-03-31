package com.xsslab.hrportal.repository;

import com.xsslab.hrportal.model.Applicant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ApplicantRepository {
    private final JdbcTemplate jdbcTemplate;

    public ApplicantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Applicant> rowMapper = new RowMapper<Applicant>() {
        @Override
        public Applicant mapRow(ResultSet rs, int rowNum) throws SQLException {
            Applicant a = new Applicant();
            a.setId(rs.getInt("id"));
            a.setName(rs.getString("name"));
            a.setEmail(rs.getString("email"));
            a.setPosition(rs.getString("position"));
            a.setSummary(rs.getString("summary"));
            a.setStatus(rs.getString("status"));
            a.setCreatedAt(rs.getTimestamp("created_at"));
            return a;
        }
    };

    public List<Applicant> findAll() {
        return jdbcTemplate.query("SELECT * FROM applicants ORDER BY created_at DESC", rowMapper);
    }

    public Applicant findById(int id) {
        List<Applicant> list = jdbcTemplate.query("SELECT * FROM applicants WHERE id = ?", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void save(Applicant a) {
        jdbcTemplate.update("INSERT INTO applicants (name, email, position, summary, status) VALUES (?, ?, ?, ?, ?)",
                a.getName(), a.getEmail(), a.getPosition(), a.getSummary(), a.getStatus());
    }
}
