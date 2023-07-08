package com.lineate.mdzinarishvili.reminderapp.token;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenMapper  implements RowMapper<Token>{


    public Token mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Token(resultSet.getLong("token_id"),
                resultSet.getString("token_value"),
//                TokenType.BEARER,
                resultSet.getBoolean("revoked"),
                resultSet.getBoolean("expired")
//                ,
//                new User(resultSet.getLong("user_id"),resultSet.getString("username"),
//                         resultSet.getString("email"),resultSet.getString("password"),Role.ADMIN)
        );
        }

}
