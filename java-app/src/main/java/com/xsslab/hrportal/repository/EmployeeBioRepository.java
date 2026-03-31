package com.xsslab.hrportal.repository;

import com.xsslab.hrportal.model.EmployeeBio;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EmployeeBioRepository {
    private final JdbcTemplate jdbcTemplate;

    public EmployeeBioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<EmployeeBio> rowMapper = new RowMapper<EmployeeBio>() {
        @Override
        public EmployeeBio mapRow(ResultSet rs, int rowNum) throws SQLException {
            EmployeeBio b = new EmployeeBio();
            b.setId(rs.getInt("id"));
            b.setEmployeeName(rs.getString("employee_name"));
            b.setDepartment(rs.getString("department"));
            b.setBio(rs.getString("bio"));
            b.setHobbies(rs.getString("hobbies"));
            b.setCreatedAt(rs.getTimestamp("created_at"));
            return b;
        }
    };

    public List<EmployeeBio> findAll() {
        return jdbcTemplate.query("SELECT * FROM employee_bios ORDER BY created_at DESC", rowMapper);
    }

    public void save(EmployeeBio b) {
        jdbcTemplate.update("INSERT INTO employee_bios (employee_name, department, bio, hobbies) VALUES (?, ?, ?, ?)",
                b.getEmployeeName(), b.getDepartment(), b.getBio(), b.getHobbies());
    }
}
