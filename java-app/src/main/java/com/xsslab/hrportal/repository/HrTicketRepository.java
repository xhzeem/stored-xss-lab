package com.xsslab.hrportal.repository;

import com.xsslab.hrportal.model.HrTicket;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class HrTicketRepository {
    private final JdbcTemplate jdbcTemplate;

    public HrTicketRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<HrTicket> rowMapper = new RowMapper<HrTicket>() {
        @Override
        public HrTicket mapRow(ResultSet rs, int rowNum) throws SQLException {
            HrTicket t = new HrTicket();
            t.setId(rs.getInt("id"));
            t.setEmployeeName(rs.getString("employee_name"));
            t.setSubject(rs.getString("subject"));
            t.setDescription(rs.getString("description"));
            t.setStatus(rs.getString("status"));
            t.setCreatedAt(rs.getTimestamp("created_at"));
            return t;
        }
    };

    public List<HrTicket> findAll() {
        return jdbcTemplate.query("SELECT * FROM hr_tickets ORDER BY created_at DESC", rowMapper);
    }

    public void save(HrTicket t) {
        jdbcTemplate.update("INSERT INTO hr_tickets (employee_name, subject, description, status) VALUES (?, ?, ?, ?)",
                t.getEmployeeName(), t.getSubject(), t.getDescription(), t.getStatus());
    }
}
