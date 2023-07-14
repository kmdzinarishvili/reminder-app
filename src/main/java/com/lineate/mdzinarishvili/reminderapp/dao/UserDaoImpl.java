package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.enums.RoleType;
import com.lineate.mdzinarishvili.reminderapp.enums.UsersSortType;
import com.lineate.mdzinarishvili.reminderapp.exceptions.DatabaseException;
import com.lineate.mdzinarishvili.reminderapp.exceptions.InvalidInputException;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import com.lineate.mdzinarishvili.reminderapp.models.UserMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public UserDaoImpl(JdbcTemplate jdbcTemplate,
                     NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  private final String SQL_INSERT_USER =
      "insert into users(username, email, password, timezone_offset_hours, role_id) values(?,?,?,?,?)";


  @Override
  public List<User> selectUsers(String sortType) {
    log.info("Selecting users with sort type: {} ", sortType);
    Map<String, Object> params = new HashMap<>();
    params.put("sortType", sortType);
    String SQL_GET_ALL = "select u.user_id, username,  email, password, registration_date, " +
        " activity_date, timezone_offset_hours, days_before_reminder_delete, " +
        " role_name as role from users u" +
        " join roles r on u.role_id = r.role_id" +
        " order by " +
        " CASE WHEN :sortType = 'LOGIN' THEN username END, " +
        " CASE WHEN :sortType = 'REGISTRATION_DATE' THEN registration_date END," +
        " CASE  WHEN :sortType = 'LAST_ACTIVITY' THEN activity_date END";
    return namedParameterJdbcTemplate.query(SQL_GET_ALL, new MapSqlParameterSource(params),
        new UserMapper());
  }

  public User save(User user) {

    try {
      log.info("Saving user with username: {}", user.getUsername());
      jdbcTemplate.update(SQL_INSERT_USER, user.getUsername(), user.getEmail(),
          user.getPassword(), user.getTimezoneOffsetHours(), findRoleId(user.getRole()));
      return this.findByEmail(user.getEmail()).get();
    } catch (Exception exception) {
      throw new InvalidInputException("email or username already in use");
    }
  }

  @Override
  public User updateOrInsertUser(User user) {
    log.info("Updating or inserting user with username: {}", user.getUsername());
    try {
      Long id = findByEmail(user.getEmail()).get().getId();
      user.setId(id);
      return updateUser(user).get();
    } catch (NoSuchElementException exception) {
      return insertUser(user).get();
    }
  }

  @Override
  public Optional<User> findByEmail(String email) {
    log.info("Finding user by email: {}", email);
    String SQL_FIND_USER_BY_EMAIL =
        "select  u.user_id, username,  email, password, registration_date, " +
            "activity_date, timezone_offset_hours, days_before_reminder_delete, role_name as role from users u " +
            " join roles r on r.role_id = u.role_id " +
            " where u.email = ?";
    List<User> users = jdbcTemplate.query(SQL_FIND_USER_BY_EMAIL, new UserMapper(), email);
    return users.stream().findFirst();
  }

  @Override
  public Optional<User> selectUserById(Long id) {
    log.info("Finding user by id: {}", id);
    String SQL_FIND_USER = "select  u.user_id, username,  email, password, registration_date, " +
        " activity_date, timezone_offset_hours, days_before_reminder_delete, role_name as role from users u " +
        " join roles r on r.role_id = u.role_id " +
        " where u.user_id = ?";
    List<User> users = jdbcTemplate.query(SQL_FIND_USER, new UserMapper(), id);
    return users.stream().findFirst();
  }

  @Override
  public Optional<User> selectUserByUsername(String username) {
    log.info("Finding user by username: {}", username);
    String SQL_FIND_USER_BY_USERNAME =
        "select  u.user_id, username,  email, password, registration_date," +
            " activity_date, timezone_offset_hours, days_before_reminder_delete, role_name as role  from users u" +
            " join roles r on u.role_id = r.role_id " +
            " where username = ?";
    List<User> users = jdbcTemplate.query(SQL_FIND_USER_BY_USERNAME, new UserMapper(), username);
    return users.stream().findFirst();
  }

  @Override
  public int deleteUserById(Long id) {
    log.info("Deleting user by id: {}", id);
    String SQL_DELETE_USER = "delete from users where user_id = ?";
    return jdbcTemplate.update(SQL_DELETE_USER, id);
  }

  @Override
  public Optional<User> updateUser(User user) {
    log.info("updating user with email: {}", user.getEmail());
    String SQL_UPDATE_USER =
        "update users set username = ?, password  = ?, timezone_offset_hours = ? where user_id = ?";
    jdbcTemplate.update(SQL_UPDATE_USER, user.getUsername(),
        user.getPassword(), user.getTimezoneOffsetHours(),
        user.getId()); // can't update role or email
    return this.selectUserById(user.getId());
  }

  private int findRoleId(RoleType roleType) {
    log.info("Find role type id: {} ", roleType);
    final String SQL_FIND_RECURRENCE_TYPE = "select role_id from roles where role_name = ?";
    return jdbcTemplate.query(SQL_FIND_RECURRENCE_TYPE, (rs, rowNum) -> rs.getInt("role_id"),
        roleType.toString()).stream().findFirst().orElseThrow();
  }

  @Override
  public Optional<User> insertUser(User user) {
    log.info("Insert user with email: {}", user.getEmail());
    jdbcTemplate.update(SQL_INSERT_USER, user.getUsername(), user.getEmail(),
        user.getPassword(), findRoleId(user.getRole()));
    Long id = this.selectUserByUsername(user.getUsername()).get().getId();
    return this.selectUserById(id);

  }

  @Override
  public boolean setDateOfLastActivityNow(Long user_id) {
    log.info("setting date of last activity to now for user with id {}", user_id);
    final String SQL_LAST_ACTIVITY = "update users set activity_date = now() where user_id = ?";
    return jdbcTemplate.update(SQL_LAST_ACTIVITY, user_id) == 1;
  }
}
