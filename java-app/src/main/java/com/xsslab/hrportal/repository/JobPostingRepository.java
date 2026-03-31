package com.xsslab.hrportal.repository;

import com.xsslab.hrportal.model.JobPosting;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JobPostingRepository {
    private final JdbcTemplate jdbcTemplate;

    public JobPostingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<JobPosting> rowMapper = new RowMapper<JobPosting>() {
        @Override
        public JobPosting mapRow(ResultSet rs, int rowNum) throws SQLException {
            JobPosting j = new JobPosting();
            j.setId(rs.getInt("id"));
            j.setTitle(rs.getString("title"));
            j.setDepartment(rs.getString("department"));
            j.setDescription(rs.getString("description"));
            j.setRequirements(rs.getString("requirements"));
            j.setCreatedAt(rs.getTimestamp("created_at"));
            return j;
        }
    };

    public List<JobPosting> findAll() {
        return jdbcTemplate.query("SELECT * FROM job_postings ORDER BY created_at DESC", rowMapper);
    }

    public void save(JobPosting j) {
        jdbcTemplate.update("INSERT INTO job_postings (title, department, description, requirements) VALUES (?, ?, ?, ?)",
                j.getTitle(), j.getDepartment(), j.getDescription(), j.getRequirements());
    }
}
