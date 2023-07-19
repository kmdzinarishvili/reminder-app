package com.lineate.mdzinarishvili.reminderapp.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class LabelMapper implements RowMapper<Label> {
  public Label mapRow(ResultSet resultSet, int i) throws SQLException {
    return new Label(resultSet.getLong("label_id"),
        resultSet.getString("label_name"));
  }
}

