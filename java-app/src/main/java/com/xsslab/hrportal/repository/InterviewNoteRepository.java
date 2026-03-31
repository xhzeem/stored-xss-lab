package com.xsslab.hrportal.repository;

import com.xsslab.hrportal.model.InterviewNote;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class InterviewNoteRepository {
    private final JdbcTemplate jdbcTemplate;

    public InterviewNoteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<InterviewNote> rowMapper = new RowMapper<InterviewNote>() {
        @Override
        public InterviewNote mapRow(ResultSet rs, int rowNum) throws SQLException {
            InterviewNote n = new InterviewNote();
            n.setId(rs.getInt("id"));
            n.setApplicantId(rs.getInt("applicant_id"));
            n.setInterviewer(rs.getString("interviewer"));
            n.setNotes(rs.getString("notes"));
            n.setRating(rs.getInt("rating"));
            n.setCreatedAt(rs.getTimestamp("created_at"));
            return n;
        }
    };

    public List<InterviewNote> findAll() {
        return jdbcTemplate.query("SELECT * FROM interview_notes ORDER BY created_at DESC", rowMapper);
    }

    public void save(InterviewNote n) {
        jdbcTemplate.update("INSERT INTO interview_notes (applicant_id, interviewer, notes, rating) VALUES (?, ?, ?, ?)",
                n.getApplicantId(), n.getInterviewer(), n.getNotes(), n.getRating());
    }
}
