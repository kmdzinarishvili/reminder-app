package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.enums.RoleType;
import com.lineate.mdzinarishvili.reminderapp.models.Token;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenMapper implements RowMapper<Token> {

  public Token mapRow(ResultSet resultSet, int i) throws SQLException {
    return new Token(resultSet.getLong("token_id"),
        resultSet.getString("token_value"),
        resultSet.getBoolean("revoked"),
        resultSet.getBoolean("expired"),
        new User(resultSet.getLong("user_id"))
    );
  }

}
