package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.models.Reminder;

import java.util.List;
import java.util.Optional;

public interface ReminderDAO {

    List<Reminder> selectReminders();
    Optional<Reminder> selectReminderById(Long id);
    int insertReminder(Reminder person);
    int updateReminder(Reminder person);

    int deleteReminderById(Long id);


}
