package com.xsslab.hrportal.repository;

import com.xsslab.hrportal.model.OnboardingTask;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OnboardingTaskRepository {
    private final JdbcTemplate jdbcTemplate;

    public OnboardingTaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OnboardingTask> rowMapper = new RowMapper<OnboardingTask>() {
        @Override
        public OnboardingTask mapRow(ResultSet rs, int rowNum) throws SQLException {
            OnboardingTask t = new OnboardingTask();
            t.setId(rs.getInt("id"));
            t.setEmployeeName(rs.getString("employee_name"));
            t.setTaskName(rs.getString("task_name"));
            t.setNotes(rs.getString("notes"));
            t.setCompleted(rs.getBoolean("completed"));
            t.setCreatedAt(rs.getTimestamp("created_at"));
            return t;
        }
    };

    public List<OnboardingTask> findAll() {
        return jdbcTemplate.query("SELECT * FROM onboarding_tasks ORDER BY created_at DESC", rowMapper);
    }

    public void save(OnboardingTask t) {
        jdbcTemplate.update("INSERT INTO onboarding_tasks (employee_name, task_name, notes, completed) VALUES (?, ?, ?, ?)",
                t.getEmployeeName(), t.getTaskName(), t.getNotes(), t.isCompleted());
    }
}
