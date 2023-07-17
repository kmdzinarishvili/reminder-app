package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.enums.CategoryType;
import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import com.lineate.mdzinarishvili.reminderapp.exceptions.DatabaseException;
import com.lineate.mdzinarishvili.reminderapp.exceptions.InvalidInputException;
import com.lineate.mdzinarishvili.reminderapp.models.Label;
import com.lineate.mdzinarishvili.reminderapp.models.LabelMapper;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import com.lineate.mdzinarishvili.reminderapp.models.ReminderMapper;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
@Slf4j
public class ReminderDAOImpl implements ReminderDAO {
  JdbcTemplate jdbcTemplate;
  NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  final String SQL_GET_ALL =
      "select r.*, rt.recurrence_name, lr.label_id, l.label_name, c.category_name  from reminders r " +
          " join recurrence_types rt " +
          " on r.recurrence_id = rt.recurrence_id " +
          " join categories c using (category_id)" +
          " join labels_reminders lr using (reminder_id)" +
          " join labels l using(label_id)" +
          " where user_id = ? ";

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
      log.info("reminder dao get recurrence id for {}", recurrenceType);
      return jdbcTemplate.query(SQL_FIND_RECURRENCE_TYPE,
              (rs, rowNum) -> rs.getInt("recurrence_id"), recurrenceType.toString()).stream()
          .findFirst().orElseThrow();
    } catch (Throwable throwable) {
      log.error("reminder dao get recurrence id for {} FAILED invalid type", recurrenceType);
      throw new DatabaseException("Recurrence type invalid");
    }
  }

  private int getCategoryId(CategoryType categoryType) {
    final String SQL_FIND_CATEGORY_TYPE =
        "select category_id from categories where category_name = ?";
    try {
      log.info("reminder dao get category id for {}", categoryType);
      return jdbcTemplate.query(SQL_FIND_CATEGORY_TYPE,
              (rs, rowNum) -> rs.getInt("category_id"), categoryType.toString()).stream()
          .findFirst().orElseThrow();
    } catch (Throwable throwable) {
      log.error("reminder dao get category id for {} FAILED invalid type", categoryType);
      throw new InvalidInputException("Category type invalid");
    }
  }

  public Label getLabelByName(String labelName) {
    final String SQL_FIND_LABEL =
        "select * from labels where label_name = ?";
    try {
      log.info("reminder dao get label by name for {}", labelName);
      return jdbcTemplate.query(SQL_FIND_LABEL, new LabelMapper(), labelName.toUpperCase()).stream()
          .findFirst().orElseThrow();
    } catch (Throwable throwable) {
      log.info("reminder dao get label creating by name for {} since not found", labelName);
      GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
      final String SQL_INSERT_LABEL =
          "insert into labels(label_name) values (:label_name)";
      Map<String, Object> params = new HashMap<>();
      params.put("label_name", labelName.toUpperCase());
      namedParameterJdbcTemplate.update(SQL_INSERT_LABEL,
          new MapSqlParameterSource(params),
          generatedKeyHolder, new String[] {"label_id"});
      return new Label(generatedKeyHolder.getKey().longValue(), labelName.toUpperCase());
    }
  }

  @Override
  public boolean isIdValid(Long id) {
    log.info("Check if reminder id: {} valid dao", id);
    return Boolean.TRUE.equals(
        jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM reminders WHERE reminder_id = ?)",
            Boolean.class, id));
  }

  @Override
  public List<Reminder> selectReminders(Long user_id) {
    log.info("select all reminders in dao for user with id: {}", user_id);
    return jdbcTemplate.query(SQL_GET_ALL, new ReminderMapper(), user_id);
  }

  @Override
  public List<Reminder> selectRemindersOrderByCreationDate(Long user_id) {
    log.info("select all reminders by creation date in dao for user with id: {}", user_id);
    final String SQL_GET_ALL_CREATION = SQL_GET_ALL + " order by creation_date";
    return jdbcTemplate.query(SQL_GET_ALL_CREATION, new ReminderMapper(), user_id);
  }

  @Override
  public List<Reminder> selectRemindersOrderByPriority(Long user_id) {
    log.info("select all reminders by priority in dao for user with id: {}", user_id);
    final String SQL_GET_ALL_PRIORITY = SQL_GET_ALL +" order by priority";
    return jdbcTemplate.query(SQL_GET_ALL_PRIORITY, new ReminderMapper(), user_id);
  }
  @Override
  public List<Reminder> selectOverdueReminders(Long user_id) {
    log.info("select all overdue reminders in dao for user with id: {}", user_id);
    final String SQL_GET_OVERDUE =
        "select r.*, rt.recurrence_name, c.category_name, l.* from reminders r " +
            " join recurrence_types rt " +
            " on r.recurrence_id = rt.recurrence_id " +
            " join categories c using (category_id)" +
            " join labels_reminders lr using (reminder_id)" +
            " join labels l using (label_id) " +
            " where user_id = ? and reminder_datetime<now() and (which_reminder_completed is null or  " +
            " (rt.recurrence_name ='DAILY' and " +
            " ((reminder_datetime::time>now()::time and which_reminder_completed <current_date - INTEGER '1') or" +
            " (reminder_datetime::time<=now()::time and which_reminder_completed <current_date::timestamp))) or " +
            " (rt.recurrence_name = 'WEEKLY' and " +
            " (((extract('dow' from current_date)!= extract('dow' from reminder_datetime) or (extract('dow' from current_date)= extract('dow' from reminder_datetime) and reminder_datetime::time<=now()::time))" +
            " and which_reminder_completed <current_date - ((extract('dow' from reminder_datetime) + cast(extract(dow FROM current_date) AS int)) % 7)::integer or" +
            " ((extract('dow' from current_date)=extract('dow' from reminder_datetime)) and reminder_datetime::time>now()::time and which_reminder_completed<current_date)))) or " +
            " (rt.recurrence_name = 'MONTHLY' and " +
            " (((extract('day' from reminder_datetime) < extract('day' from current_date)) or (extract('day' from reminder_datetime) = extract('day' from current_date) and reminder_datetime::time<=now()::time" +
            " and which_reminder_completed<date_trunc('month', current_date)+ interval '1' day * (extract('day' from  reminder_datetime)::integer-1)+reminder_datetime::time))or " +
            " ((extract('day' from reminder_datetime) < extract('day' from current_date))" +
            " and which_reminder_completed<date_trunc('month', current_date)+ interval '1' month+ interval '1' day *" +
            " (extract('day' from  reminder_datetime)::integer-1)+reminder_datetime::time))))";
    return jdbcTemplate.query(SQL_GET_OVERDUE, new ReminderMapper(), user_id);
  }

  @Override
  public List<Reminder> selectReminderById(Long id) {
    if (!isIdValid(id)) {
      log.error("select reminder by id FAILED invalid id: {}", id);
      throw new DatabaseException("ID not found");
    }
    log.info("select reminder by id: {}", id);
    final String SQL_FIND_REMINDER =
        "select r.*, rt.recurrence_name from reminders r " +
            " join recurrence_types rt " +
            " on r.recurrence_id = rt.recurrence_id " +
            " join categories c using (category_id)" +
            " join labels_reminders lr using (reminder_id)" +
            " join labels l using(label_id)" +
            " where reminder_id = ?";
    List<Reminder> reminders = jdbcTemplate.query(SQL_FIND_REMINDER, new ReminderMapper(), id);
    return reminders;
  }

  @Override
  public Boolean deleteReminderById(Long id) {
    if (!isIdValid(id)) {
      log.error("delete reminder by id FAILED invalid id: {}", id);
      throw new DatabaseException("ID not found");
    }
    log.info("delete reminder by id: {}", id);
    final String SQL_DELETE_REMINDER = "delete from reminders where reminder_id = ?";
    return jdbcTemplate.update(SQL_DELETE_REMINDER, id) == 1;
  }

  @Override
  public Reminder updateReminder(Reminder reminder) {
    if (!isIdValid(reminder.getId())) {
      log.error("update reminder by id FAILED invalid id: {}", reminder.getId());
      throw new DatabaseException("ID not found");
    }
    Integer enumId = getEnumId(reminder.getRecurrence());
    try {
      log.info("update reminder by with info: {}", reminder);
      final String SQL_UPDATE_REMINDER =
          "update reminders" +
              " set title = ?," +
              " recurrence_id = ?," +
              " reminder_datetime  = ?," +
              " priority = ?," +
              " category_id = ?," +
              " attachment = ?" +
              " where reminder_id = ?";
      jdbcTemplate.update(SQL_UPDATE_REMINDER, reminder.getTitle(), enumId,
          reminder.getDate(), reminder.getPriority(), getCategoryId(reminder.getCategory()),
          reminder.getAttachment(), reminder.getId());
      return reminder;
    } catch (Throwable throwable) {
      log.error("update reminder FAILED");
      throw new DatabaseException("oops something went wrong, could not insert");
    }
  }

  //TO DO UPDATE LABELS

  @Override
  public Reminder insertReminder(Reminder reminder) {
    Integer enumId = getEnumId(reminder.getRecurrence());
    Integer categoryId = getCategoryId(reminder.getCategory());
    try {
      log.info("insert reminder by with info: {}", reminder);
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
      log.error("insert reminder FAILED");
      throw new DatabaseException("oops something went wrong, could not insert");
    }
  }

  public boolean isPending(Long id) {
    Reminder reminder;
    try {
      log.info("check reminder pending for id: {}", id);
      reminder = selectReminderById(id).get(0);
    } catch (Exception e) {
      log.error("check reminder pending FAILED invalid id: {}", id);
      throw new InvalidInputException("Reminder with id not found");
    }
    return !reminder.getAcceptanceStatus();
  }

  public boolean setAcceptedTrue(Long id) {
    try {
      log.info("set reminder accepted true for id: {}", id);
      selectReminderById(id).get(0);
    } catch (Exception e) {
      log.error("check reminder accepted true for id: {} failed id not found", id);
      throw new InvalidInputException("Reminder with id not found");
    }
    final String SQL_SET_ACCEPTANCE =
        "update reminders set acceptance_status=TRUE where reminder_id = ?";
    return jdbcTemplate.update(SQL_SET_ACCEPTANCE, id) == 1;
  }

  public boolean setCompletedDate(LocalDateTime dateTime, Long id) {
    log.info("set reminder completed date with date: {} for id: {}", dateTime, id);
    final String SQL_SET_COMPLETE =
        "update reminders set which_reminder_completed= ?" +
            " where reminder_id = ?";
    return jdbcTemplate.update(SQL_SET_COMPLETE, dateTime, id) == 1;
  }
}
