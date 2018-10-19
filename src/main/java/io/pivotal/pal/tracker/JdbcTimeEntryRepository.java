package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.crypto.KeyGenerator;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        PreparedStatementCreator statementCreator = con -> {
            String query = "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, java.sql.Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());

            return statement;
        };
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        this.jdbcTemplate.update(statementCreator, keyHolder);

        timeEntry.setId(keyHolder.getKey().longValue());

        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        List<TimeEntry> results = this.jdbcTemplate.query("SELECT * FROM time_entries WHERE id=?",
                new Object[]{id},
                new TimeEntryRowMapper());

        return results.size() > 0 ? results.get(0) : null;
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        this.jdbcTemplate.update("UPDATE time_entries SET project_id=?, user_id=?, date=?, hours=? WHERE id=?",
            timeEntry.getProjectId(),
            timeEntry.getUserId(),
            java.sql.Date.valueOf(timeEntry.getDate()),
            timeEntry.getHours(),
            id);

        return find(id);
    }

    @Override
    public void delete(long id) {
        this.jdbcTemplate.update("DELETE FROM time_entries WHERE id=?", id);
    }

    @Override
    public List<TimeEntry> list() {
        return this.jdbcTemplate.query("SELECT * FROM time_entries", new TimeEntryRowMapper());
    }

    class TimeEntryRowMapper implements RowMapper<TimeEntry> {
        @Override
        public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TimeEntry(
                    rs.getLong("id"),
                    rs.getLong("project_id"),
                    rs.getLong("user_id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getInt("hours")
            );
        }
    }
}
