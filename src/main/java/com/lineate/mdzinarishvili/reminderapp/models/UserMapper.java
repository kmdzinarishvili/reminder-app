package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import com.lineate.mdzinarishvili.reminderapp.enums.RoleType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static com.lineate.mdzinarishvili.reminderapp.models.ReminderMapper.valueOfIgnoreCase;

public class UserMapper implements RowMapper<User> {
    public User mapRow(ResultSet resultSet, int i) throws SQLException {

        return new User(resultSet.getLong("user_id"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                valueOfIgnoreCase( resultSet.getString("role")).orElse(null));
    }
    public static Optional<RoleType> valueOfIgnoreCase(String name) {
        return Arrays.stream(RoleType.values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findAny();
    }
}
//    private Long id;
//    private String username;
//    private String email;
//    private String password;
//    private RoleType role;