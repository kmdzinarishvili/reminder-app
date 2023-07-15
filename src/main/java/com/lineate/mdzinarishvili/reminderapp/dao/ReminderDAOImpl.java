package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import com.lineate.mdzinarishvili.reminderapp.exceptions.DatabaseException;
import com.lineate.mdzinarishvili.reminderapp.exceptions.InvalidInputException;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import com.lineate.mdzinarishvili.reminderapp.models.ReminderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
public class ReminderDAOImpl implements ReminderDAO {
  JdbcTemplate jdbcTemplate;
  NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  private final String SQL_FIND_REMINDER =
      "select r.reminder_id, title, rt.recurrence_name as recurrence, r.reminder_datetime, r.attachment from reminders r " +
          "join recurrence_types rt " +
          "on r.recurrence_id = rt.recurrence_id " +
          " where reminder_id = ? ";
  private final String SQL_DELETE_REMINDER = "delete from reminders where reminder_id = ?";
  private final String SQL_UPDATE_REMINDER =
      "update reminders set title = ?, recurrence_id = ?, reminder_datetime  = ?, attachment = ? where reminder_id = ?";
  private final String SQL_GET_ALL =
      "select r.reminder_id, title, rt.recurrence_name as recurrence, r.reminder_datetime, r.attachment from reminders r " +
          "join recurrence_types rt " +
          "on r.recurrence_id = rt.recurrence_id ";

  @Autowired
  public ReminderDAOImpl(JdbcTemplate jdbcTemplate,
                         NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  private int getEnumId(RecurrenceType recurrenceType) {
    final String SQL_FIND_RECURRENCE_TYPE =
        "select recurrence_id from recurrence_types where recurrence_name = ?";
    try {
      return jdbcTemplate.query(SQL_FIND_RECURRENCE_TYPE,
              (rs, rowNum) -> rs.getInt("recurrence_id"), recurrenceType.toString()).stream()
          .findFirst().orElseThrow();
    } catch (Throwable throwable) {
      throw new DatabaseException("Recurrence type invalid");
    }
  }

  public boolean isIdValid(Long id) {
    return Boolean.TRUE.equals(
        jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM reminders WHERE reminder_id = ?)",
            Boolean.class, id));
  }

  @Override
  public List<Reminder> selectReminders() {
    return jdbcTemplate.query(SQL_GET_ALL, new ReminderMapper());
  }

  @Override
  public Optional<Reminder> selectReminderById(Long id) {
    if (!isIdValid(id)) {
      throw new DatabaseException("ID not found");
    }
    List<Reminder> reminders = jdbcTemplate.query(SQL_FIND_REMINDER, new ReminderMapper(), id);
    return reminders.stream().findFirst();
  }

  @Override
  public Boolean deleteReminderById(Long id) {
    if (!isIdValid(id)) {
      throw new DatabaseException("ID not found");
    }
    return jdbcTemplate.update(SQL_DELETE_REMINDER, id) == 1;
  }

  @Override
  public Reminder updateReminder(Reminder reminder) {
    if (!isIdValid(reminder.getId())) {
      throw new DatabaseException("ID not found");
    }
    Integer enumId = getEnumId(reminder.getRecurrence());
    try {
      jdbcTemplate.update(SQL_UPDATE_REMINDER, reminder.getTitle(), enumId,
          reminder.getDate(), reminder.getAttachment(),
          reminder.getId());
      return reminder;
    } catch (Throwable throwable) {
      throw new DatabaseException("oops something went wrong, could not insert");
    }

  }

  @Override
  public Reminder insertReminder(Reminder reminder) {
    Integer enumId = getEnumId(reminder.getRecurrence());
    try {
      GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
      final String SQL_INSERT_REMINDER =
          "insert into reminders(title, recurrence_id, reminder_datetime,  attachment) " +
              "values( :title, :recurrence_id, :reminder_datetime ,:attachment )";
      Map<String, Object> params = new HashMap<>();
      params.put("title", reminder.getTitle());
      params.put("recurrence_id", enumId);
      params.put("reminder_datetime", reminder.getDate());
      params.put("attachment", reminder.getAttachment());
      namedParameterJdbcTemplate.update(SQL_INSERT_REMINDER, new MapSqlParameterSource(params),
          generatedKeyHolder, new String[] {"reminder_id"});
      int id = generatedKeyHolder.getKey().intValue();
      reminder.setId((long) id);
      return reminder;
    } catch (Throwable throwable) {
      throw new DatabaseException("oops something went wrong, could not insert");
    }
  }
}
