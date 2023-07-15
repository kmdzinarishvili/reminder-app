package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.enums.RoleType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserMapper implements RowMapper<User> {
  public User mapRow(final ResultSet resultSet, int i) throws SQLException {
    return new User(resultSet.getLong("user_id"),
        resultSet.getString("username"),
        resultSet.getString("email"),
        resultSet.getString("password"),
        RoleType.valueOf(resultSet.getString("role")),
        resultSet.getDate("registration_date"),
        resultSet.getDate("activity_date"),
        resultSet.getFloat("timezone_offset_hours"),
        resultSet.getInt("days_before_reminder_delete"));
  }
}
