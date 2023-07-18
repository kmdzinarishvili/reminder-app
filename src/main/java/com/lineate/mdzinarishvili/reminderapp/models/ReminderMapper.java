package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.enums.CategoryType;
import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ReminderMapper implements RowMapper<Reminder> {
  public Reminder mapRow(ResultSet resultSet, int i) throws SQLException {
    return new Reminder(resultSet.getLong("reminder_id"),
        resultSet.getString("title"),
        resultSet.getTimestamp("reminder_datetime").toLocalDateTime(),
        RecurrenceType.valueOf(resultSet.getString("recurrence_name")),
        resultSet.getBytes("attachment"),
        resultSet.getInt("priority"),
        CategoryType.valueOf(resultSet.getString("category_name")),
        resultSet.getBoolean("acceptance_status"),
        resultSet.getDate("which_reminder_completed")==null? null: resultSet.getDate("which_reminder_completed").toLocalDate(),
        resultSet.getDate("when_completed")==null? null: resultSet.getTimestamp("when_completed").toInstant(),
        new Label(resultSet.getLong("label_id"),
        resultSet.getString("label_name"))
    );
  }
}
