package com.xsslab.hrportal.repository;

import com.xsslab.hrportal.model.AuditLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AuditLogRepository {
    private final JdbcTemplate jdbcTemplate;

    public AuditLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AuditLog> rowMapper = new RowMapper<AuditLog>() {
        @Override
        public AuditLog mapRow(ResultSet rs, int rowNum) throws SQLException {
            AuditLog l = new AuditLog();
            l.setId(rs.getInt("id"));
            l.setActor(rs.getString("actor"));
            l.setAction(rs.getString("action"));
            l.setDetails(rs.getString("details"));
            l.setCreatedAt(rs.getTimestamp("created_at"));
            return l;
        }
    };

    public List<AuditLog> findAll() {
        return jdbcTemplate.query("SELECT * FROM audit_logs ORDER BY created_at DESC", rowMapper);
    }

    public void save(AuditLog l) {
        jdbcTemplate.update("INSERT INTO audit_logs (actor, action, details) VALUES (?, ?, ?)",
                l.getActor(), l.getAction(), l.getDetails());
    }
}
