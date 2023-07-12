package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.enums.CategoryType;
import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import com.lineate.mdzinarishvili.reminderapp.exceptions.DatabaseException;
import com.lineate.mdzinarishvili.reminderapp.exceptions.InvalidInputException;
import com.lineate.mdzinarishvili.reminderapp.models.Label;
import com.lineate.mdzinarishvili.reminderapp.models.LabelMapper;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import com.lineate.mdzinarishvili.reminderapp.models.ReminderMapper;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
public class ReminderDAOImpl implements ReminderDAO {
  JdbcTemplate jdbcTemplate;
  NamedParameterJdbcTemplate namedParameterJdbcTemplate;


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

  private int getCategoryId(CategoryType categoryType) {
    final String SQL_FIND_CATEGORY_TYPE =
        "select category_id from categories where category_name = ?";
    try {
      return jdbcTemplate.query(SQL_FIND_CATEGORY_TYPE,
              (rs, rowNum) -> rs.getInt("category_id"), categoryType.toString()).stream()
          .findFirst().orElseThrow();
    } catch (Throwable throwable) {
      throw new InvalidInputException("Category type invalid");
    }
  }

  public Label getLabelByName(String labelName) {
    final String SQL_FIND_LABEL =
        "select * from labels where label_name = ?";
    try {
      return jdbcTemplate.query(SQL_FIND_LABEL, new LabelMapper(), labelName.toUpperCase()).stream()
          .findFirst().orElseThrow();
    } catch (Throwable throwable) {
      GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
      final String SQL_INSERT_LABEL =
          "insert into labels(label_name) values (:label_name)";
      Map<String, Object> params = new HashMap<>();
      params.put("label_name", labelName.toUpperCase());
      var id = Long.valueOf(namedParameterJdbcTemplate.update(SQL_INSERT_LABEL,
          new MapSqlParameterSource(params),
          generatedKeyHolder, new String[] {"label_id"}));
      return new Label(id, labelName.toUpperCase());
    }
  }

  public boolean isIdValid(Long id) {
    return Boolean.TRUE.equals(
        jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM reminders WHERE reminder_id = ?)",
            Boolean.class, id));
  }


  @Override
  public List<Reminder> selectReminders() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    final String SQL_GET_ALL =
        "select r.reminder_id, title, rt.recurrence_name as recurrence, r.reminder_datetime, r.attachment from reminders r " +
            "join recurrence_types rt " +
            "on r.recurrence_id = rt.recurrence_id " +
            "join reminders_users ru " +
            "on ru.reminder_id = r.reminder_id" +
            " where user_id = ?";
    return jdbcTemplate.query(SQL_GET_ALL, new ReminderMapper(), user.getId());
  }

  @Override
  public Optional<Reminder> selectReminderById(Long id) {
    if (!isIdValid(id)) {
      throw new DatabaseException("ID not found");
    }
    final String SQL_FIND_REMINDER =
        "select r.reminder_id, title, rt.recurrence_name as recurrence, r.reminder_datetime, r.attachment from reminders r " +
            "join recurrence_types rt " +
            "on r.recurrence_id = rt.recurrence_id " +
            " where reminder_id = ?";
    List<Reminder> reminders = jdbcTemplate.query(SQL_FIND_REMINDER, new ReminderMapper(), id);
    return reminders.stream().findFirst();
  }

  @Override
  public Boolean deleteReminderById(Long id) {
    if (!isIdValid(id)) {
      throw new DatabaseException("ID not found");
    }
    final String SQL_DELETE_REMINDER = "delete from reminders where reminder_id = ?";
    return jdbcTemplate.update(SQL_DELETE_REMINDER, id) == 1;
  }

  @Override
  public Reminder updateReminder(Reminder reminder) {
    if (!isIdValid(reminder.getId())) {
      throw new DatabaseException("ID not found");
    }
    Integer enumId = getEnumId(reminder.getRecurrence());
    try {
      final String SQL_UPDATE_REMINDER =
          "update reminders set title = ?, recurrence_id = ?, reminder_datetime  = ?, attachment = ? where reminder_id = ?";
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
    Integer categoryId = getCategoryId(reminder.getCategory());
    try {
      GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
      final String SQL_INSERT_REMINDER =
          "insert into reminders(title, recurrence_id, reminder_datetime,  priority, category_id, user_id, acceptance_status, attachment) " +
              "values( :title, :recurrence_id,:reminder_datetime, :priority, :category_id, :user_id, :acceptance_status, :attachment )";
      Map<String, Object> params = new HashMap<>();
      params.put("title", reminder.getTitle());
      params.put("recurrence_id", enumId);
      params.put("reminder_datetime", reminder.getDate());
      params.put("attachment", reminder.getAttachment());
      params.put("user_id", reminder.getUser().getId());
      params.put("priority", reminder.getPriority());
      params.put("category_id", categoryId);
      params.put("acceptance_status", reminder.getAcceptanceStatus());
      System.out.println(params);
      namedParameterJdbcTemplate.update(SQL_INSERT_REMINDER, new MapSqlParameterSource(params),
          generatedKeyHolder, new String[] {"reminder_id"});
      int id = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
      if (reminder.getLabels() != null) {
        reminder.getLabels().forEach((label) -> {
          final String SQL_INSERT_LABELS =
              "insert into labels_reminders(reminder_id, label_id) " +
                  "values(  :reminder_id, :label_id)";
          Map<String, Object> params_pivot_table = new HashMap<>();
          params_pivot_table.put("reminder_id", id);
          params_pivot_table.put("label_id", label.getId());
          namedParameterJdbcTemplate.update(SQL_INSERT_LABELS,
              new MapSqlParameterSource(params_pivot_table));
        });
      }
      reminder.setId((long) id);
      return reminder;
    } catch (Throwable throwable) {
      throw new DatabaseException("oops something went wrong, could not insert");
    }
  }

}
