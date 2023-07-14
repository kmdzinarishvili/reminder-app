package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.models.Label;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderDAO {
  List<Reminder> selectReminders(Long user_id);

  Optional<Reminder> selectReminderById(Long id);

  Reminder insertReminder(Reminder reminder);

  Reminder updateReminder(Reminder reminder);

  Boolean deleteReminderById(Long id);

  boolean isIdValid(Long id);

  Label getLabelByName(String labelName);

  boolean isPending(Long id);

  boolean setAcceptedTrue(Long id);

  boolean setCompletedDate(LocalDateTime dateTime, Long id);

  List<Reminder> selectOverdueReminders(Long user_id);


}
