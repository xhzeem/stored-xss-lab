package com.xsslab.hrportal.repository;

import com.xsslab.hrportal.model.CalendarEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CalendarEventRepository {
    private final JdbcTemplate jdbcTemplate;

    public CalendarEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CalendarEvent> rowMapper = new RowMapper<CalendarEvent>() {
        @Override
        public CalendarEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            CalendarEvent e = new CalendarEvent();
            e.setId(rs.getInt("id"));
            e.setTitle(rs.getString("title"));
            e.setDescription(rs.getString("description"));
            e.setEventDate(rs.getString("event_date"));
            e.setOrganizer(rs.getString("organizer"));
            e.setCreatedAt(rs.getTimestamp("created_at"));
            return e;
        }
    };

    public List<CalendarEvent> findAll() {
        return jdbcTemplate.query("SELECT * FROM calendar_events ORDER BY event_date ASC", rowMapper);
    }

    public void save(CalendarEvent e) {
        jdbcTemplate.update("INSERT INTO calendar_events (title, description, event_date, organizer) VALUES (?, ?, ?, ?)",
                e.getTitle(), e.getDescription(), e.getEventDate(), e.getOrganizer());
    }
}
