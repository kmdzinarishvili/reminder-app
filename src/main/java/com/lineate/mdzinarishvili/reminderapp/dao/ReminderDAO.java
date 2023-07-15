package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.models.Reminder;

import java.util.List;
import java.util.Optional;

public interface ReminderDAO {
  List<Reminder> selectReminders();

  Optional<Reminder> selectReminderById(Long id);

  Reminder insertReminder(Reminder person);

  Reminder updateReminder(Reminder person);

  Boolean deleteReminderById(Long id);

  public boolean isIdValid(Long id);

}
