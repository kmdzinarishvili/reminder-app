package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.enums.ReminderOrderType;
import com.lineate.mdzinarishvili.reminderapp.enums.TimePeriod;
import com.lineate.mdzinarishvili.reminderapp.models.Label;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;

import java.util.List;
import java.util.Optional;

public interface ReminderDAO {
  List<Reminder> selectReminders();

  Optional<Reminder> selectReminderById(Long id);

  Reminder insertReminder(Reminder reminder);

  Reminder updateReminder(Reminder reminder);

  Boolean deleteReminderById(Long id);

  boolean isIdValid(Long id);

  Label getLabelByName(String labelName);

  boolean isPending(Long id);

  boolean setAcceptedTrue(Long id);

  //TO DO: implement select Overdue Reminders
  //List<Reminder> selectOverdueReminders();


}
