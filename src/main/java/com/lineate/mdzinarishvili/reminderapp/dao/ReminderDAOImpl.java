package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import com.lineate.mdzinarishvili.reminderapp.models.ReminderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class ReminderDAOImpl  implements  ReminderDAO{
    JdbcTemplate jdbcTemplate;

    private final String SQL_FIND_REMINDER = "select * from reminders where reminder_id = ?";
    private final String SQL_DELETE_REMINDER = "delete from reminders where reminder_id = ?";
    private final String SQL_UPDATE_REMINDER = "update reminders set title = ?, recurrence = ?, reminder_date  = ?, reminder_time = ?, attachment = ?, where reminder_id = ?";
    private final String SQL_GET_ALL = "select * from reminders";
    private final String SQL_INSERT_REMINDER = "insert into reminders(title, recurrence, reminder_date, reminder_time, attachment) values(?,?,?,?,?)";

    @Autowired
    public ReminderDAOImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Reminder> selectReminders() {
        return jdbcTemplate.query(SQL_GET_ALL, new ReminderMapper());
    }
    @Override
    public Optional<Reminder>  selectReminderById(Long id) {
        List<Reminder> reminders =  jdbcTemplate.query(SQL_FIND_REMINDER, new ReminderMapper(), id);
        return reminders.stream().findFirst();
    }

    @Override
    public int deleteReminderById(Long id) {
        return jdbcTemplate.update(SQL_DELETE_REMINDER, id) ;
    }

    @Override
    public int updateReminder(Reminder reminder) {
        return jdbcTemplate.update(SQL_UPDATE_REMINDER, reminder.getTitle(), reminder.getRecurrence().name(),
                reminder.getDate(), reminder.getTime(), reminder.getAttachment(),
                reminder.getId()) ;
    }

    @Override
    public int insertReminder(Reminder reminder) {
        return jdbcTemplate.update(SQL_INSERT_REMINDER, reminder.getTitle(), reminder.getRecurrence().name(),
                reminder.getDate(), reminder.getTime(), reminder.getAttachment()) ;
    }
}
