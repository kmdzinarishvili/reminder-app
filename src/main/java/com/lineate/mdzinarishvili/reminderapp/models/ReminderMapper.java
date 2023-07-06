package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

public class ReminderMapper implements RowMapper<Reminder> {
    public Reminder mapRow(ResultSet resultSet, int i) throws SQLException {

        return new Reminder(resultSet.getLong("reminder_id"),
                resultSet.getString("title"),
                valueOfIgnoreCase(resultSet.getString("recurrence"), RecurrenceType.NEVER),
                LocalDate.parse(resultSet.getString("reminder_date")),
                LocalTime.parse(resultSet.getString("reminder_time")),
                resultSet.getBytes("attachment"));
    }
    public static RecurrenceType valueOfIgnoreCase(String name, RecurrenceType orElseDefault) {
        return Arrays.stream(RecurrenceType.values())
                .filter(recurrenceType -> recurrenceType.name().equalsIgnoreCase(name))
                .findAny()
                .orElse(orElseDefault);
    }
}
