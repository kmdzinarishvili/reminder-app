package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.enums.RoleType;
import com.lineate.mdzinarishvili.reminderapp.enums.UsersSortType;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import com.lineate.mdzinarishvili.reminderapp.models.UserMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

  private final String SQL_FIND_USER =
      "select  u.user_id, username,  email, password, registration_date, " +
          " activity_date, timezone_offset_hours, days_before_reminder_delete, role_name as role from users u " +
          " join roles r on r.role_id = u.role_id " +
          " where u.user_id = ?";
  private final String SQL_FIND_USER_BY_USERNAME =
      "select  u.user_id, username,  email, password, registration_date," +
          " activity_date, timezone_offset_hours, days_before_reminder_delete, role_name as role  from users u" +
          " join roles r on u.role_id = r.role_id " +
          " where username = ?";
  private final String SQL_DELETE_USER = "delete from users where user_id = ?";
  private final String SQL_UPDATE_USER =
      "update users set username = ?, password  = ?, timezone_offset_hours = ? where user_id = ?";
  private final String SQL_GET_ALL =
      "select u.user_id, username,  email, password, registration_date, " +
          " activity_date, timezone_offset_hours, days_before_reminder_delete, " +
          " role_name as role from users u" +
          " join roles r on u.role_id = r.role_id" +
          " order by " +
          " CASE WHEN :sortType = 'LOGIN' THEN username END, " +
          " CASE WHEN :sortType = 'REGISTRATION_DATE' THEN registration_date END," +
          " CASE  WHEN :sortType = 'LAST_ACTIVITY' THEN activity_date END";

  private final String SQL_INSERT_USER =
      "insert into users(username, email, password, timezone_offset_hours, role_id) values(?,?,?,?,?)";
  private final String SQL_FIND_USER_BY_EMAIL =
      "select  u.user_id, username,  email, password, registration_date, " +
          "activity_date, timezone_offset_hours, days_before_reminder_delete, role_name as role from users u " +
          " join roles r on r.role_id = u.role_id " +
          " where u.email = ?";


  @Override
  public List<User> selectUsers(String sortType) {
    log.info("Selecting users with sort type: " + sortType);
    Map<String, Object> params = new HashMap<>();
    params.put("sortType", sortType);
    return namedParameterJdbcTemplate.query(SQL_GET_ALL, new MapSqlParameterSource(params),
        new UserMapper());
  }

  public User save(User user) {
    log.info("Saving user with username " + user.getUsername());
    int result = jdbcTemplate.update(SQL_INSERT_USER, user.getUsername(), user.getEmail(),
        user.getPassword(), user.getTimezoneOffsetHours(), findRoleId(user.getRole()));
    return this.findByEmail(user.getEmail()).get();
  }

  public User insertOrUpdate(User user) {
    log.info("Updating or inserting user with username: " + user.getUsername());
    try {
      return updateUser(findByEmail(user.getEmail()).get()).get();
    } catch (NoSuchElementException exception) {
      jdbcTemplate.update(SQL_INSERT_USER, user.getUsername(), user.getEmail(),
          user.getPassword(), user.getTimezoneOffsetHours(), findRoleId(user.getRole()));
      return this.findByEmail(user.getEmail()).get();
    }
  }

  @Override
  public Optional<User> findByEmail(String email) {
    log.info("Finding user by email : " + email);
    List<User> users = jdbcTemplate.query(SQL_FIND_USER_BY_EMAIL, new UserMapper(), email);
    return users.stream().findFirst();
  }

  @Override
  public Optional<User> selectUserById(Long id) {
    log.info("Finding user by id : " + id);
    List<User> users = jdbcTemplate.query(SQL_FIND_USER, new UserMapper(), id);
    return users.stream().findFirst();
  }

  @Override
  public Optional<User> selectUserByUsername(String username) {
    log.info("Finding user by username : " + username);
    List<User> users = jdbcTemplate.query(SQL_FIND_USER_BY_USERNAME, new UserMapper(), username);
    return users.stream().findFirst();
  }

  @Override
  public int deleteUserById(Long id) {
    log.info("Deleting user by id : " + id);
    return jdbcTemplate.update(SQL_DELETE_USER, id);
  }

  @Override
  public Optional<User> updateUser(User user) {
    log.info("updating user with email : " + user.getEmail());
    jdbcTemplate.update(SQL_UPDATE_USER, user.getUsername(),
        user.getPassword(), user.getTimezoneOffsetHours(),
        user.getId()); // can't update role or email
    return this.selectUserById(user.getId());
  }

  private int findRoleId(RoleType roleType) {
    log.info("Find roletype id " + roleType);
    final String SQL_FIND_RECURRENCE_TYPE = "select role_id from roles where role_name = ?";
    return jdbcTemplate.query(SQL_FIND_RECURRENCE_TYPE, (rs, rowNum) -> rs.getInt("role_id"),
        roleType.toString()).stream().findFirst().orElseThrow();
  }

  @Override
  public Optional<User> insertUser(User user) {
    log.info("Insert user with email: " + user.getEmail());
    int result = jdbcTemplate.update(SQL_INSERT_USER, user.getUsername(), user.getEmail(),
        user.getPassword(), findRoleId(user.getRole()));
    Long id = this.selectUserByUsername(user.getUsername()).get().getId();
    return this.selectUserById(id);

  }
}
