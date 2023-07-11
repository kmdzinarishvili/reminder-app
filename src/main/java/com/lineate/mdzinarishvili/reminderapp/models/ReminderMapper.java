package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;

public class ReminderMapper implements RowMapper<Reminder> {
  public Reminder mapRow(ResultSet resultSet, int i) throws SQLException {
    return new Reminder(resultSet.getLong("reminder_id"),
        resultSet.getString("title"),
        RecurrenceType.valueOf(resultSet.getString("recurrence")),
        resultSet.getTimestamp("reminder_datetime").toLocalDateTime(),
        resultSet.getBytes("attachment"));
  }
}
