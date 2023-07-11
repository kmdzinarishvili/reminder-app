package com.lineate.mdzinarishvili.reminderapp.services;

import static com.lineate.mdzinarishvili.reminderapp.enums.TimePeriod.TOMORROW;
import static com.lineate.mdzinarishvili.reminderapp.enums.TimePeriod.WEEK;

import com.lineate.mdzinarishvili.reminderapp.dao.ReminderDAO;
import com.lineate.mdzinarishvili.reminderapp.dto.GetRemindersRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderResponse;
import com.lineate.mdzinarishvili.reminderapp.enums.TimePeriod;
import com.lineate.mdzinarishvili.reminderapp.exceptions.InvalidInputException;
import com.lineate.mdzinarishvili.reminderapp.exceptions.NotFoundException;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReminderService {
  private final ReminderDAO reminderDao;

  public ReminderService(ReminderDAO reminderDao) {
    this.reminderDao = reminderDao;
  }

  public List<Reminder> getReminders() {
    return reminderDao.selectReminders();
  }

  public List<Reminder> getOldReminders() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    int daysBefore = user.getDaysBeforeReminderDelete();
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime daysBeforeDate = LocalDateTime.now().minusDays(daysBefore);
    List<Reminder> reminders = getReminders();
    List<Reminder> oldReminders = new ArrayList<>();
    reminders.forEach((reminder) -> {
      LocalDateTime reminderDate = reminder.getDate();
      switch (reminder.getRecurrence()) {
        case NEVER:
          if (reminderDate.isAfter(daysBeforeDate)
              &&
              reminderDate.isBefore(today)) {
            oldReminders.add(reminder);
          }
          break;
        case DAILY:
          for (int i = 0; i < daysBefore; i++) {
            LocalDateTime date = daysBeforeDate.plusDays(i);
            date = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
            reminder.setDate(date);
          }
          oldReminders.add(reminder);
          break;
        case WEEKLY:
          for (int i = 0; i < daysBefore; i++) {
            LocalDateTime date = daysBeforeDate.plusDays(i);
            if (reminder.getDate().getDayOfWeek() ==
                date.getDayOfWeek()) {
              date = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
              reminder.setDate(date);
              oldReminders.add(reminder);
            }
          }
          break;
        case MONTHLY:
          for (int i = 0; i < daysBefore; i++) {
            LocalDateTime date = daysBeforeDate.plusDays(i);
            if (reminder.getDate().getDayOfMonth() ==
                date.getDayOfMonth()) {
              date = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
              reminder.setDate(date);
              oldReminders.add(reminder);
            }
          }
          break;
      }
    });
    return oldReminders;
  }

  private List<Reminder> getRemindersDay(LocalDateTime date) {
    List<Reminder> reminders = getReminders();
    List<Reminder> dateReminders = new ArrayList<>();
    reminders.forEach((reminder) -> {
      LocalTime reminderTime = reminder.getDate().toLocalTime();
      reminder.setDate(date.withHour(reminderTime.getHour()).withMinute(reminderTime.getMinute()));
      switch (reminder.getRecurrence()) {
        case NEVER:
          if (reminder.getDate().toLocalDate() == date.toLocalDate()) {
            dateReminders.add(reminder);
          }
          break;
        case DAILY:
          dateReminders.add(reminder);
          break;
        case WEEKLY:
          if (reminder.getDate().getDayOfWeek() ==
              date.getDayOfWeek()) {
            dateReminders.add(reminder);
          }
          break;
        case MONTHLY:
          if (reminder.getDate().getDayOfMonth() ==
              date.getDayOfMonth()) {
            dateReminders.add(reminder);
          }
          break;
      }
    });
    Collections.sort(dateReminders);
    return dateReminders;
  }

  public List<Reminder> getReminders(GetRemindersRequest getRemindersRequest) {
    TimePeriod timePeriod = getRemindersRequest.getTimePeriod();
    switch (timePeriod) {
      case TODAY:
        return getRemindersDay(LocalDateTime.now());
      case TOMORROW:
        return getRemindersDay(LocalDateTime.now().plusDays(1));
      case WEEK:
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime endOfWeek = LocalDateTime.now().plusDays(7);
        List<Reminder> reminders = getReminders();
        List<Reminder> newReminders = new ArrayList<>();
        reminders.forEach((reminder) -> {
          LocalDateTime reminderDate = reminder.getDate();
          switch (reminder.getRecurrence()) {
            case NEVER:
              if (reminderDate.isAfter(today)
                  &&
                  reminderDate.isBefore(endOfWeek)) {
                newReminders.add(reminder);
              }
              break;
            case DAILY:
              for (int i = 1; i <= 7; i++) {
                LocalDateTime date = today.plusDays(i);
                date = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
                reminder.setDate(date);
              }
              newReminders.add(reminder);
              break;
            case WEEKLY:
              for (int i = 1; i <= 7; i++) {
                LocalDateTime date = today.plusDays(i);
                if (reminder.getDate().getDayOfWeek() ==
                    date.getDayOfWeek()) {
                  date = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
                  reminder.setDate(date);
                  newReminders.add(reminder);
                }
              }
              break;
            case MONTHLY:
              for (int i = 1; i <= 7; i++) {
                LocalDateTime date = today.plusDays(i);
                if (reminder.getDate().getDayOfMonth() ==
                    date.getDayOfMonth()) {
                  date = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
                  reminder.setDate(date);
                  newReminders.add(reminder);
                }
              }
              break;
          }
        });
        return newReminders;
    }
    throw new InvalidInputException("Invalid Time Period indicated");
  }

  public ReminderResponse addNewReminder(ReminderRequest reminderRequest) {
    return new ReminderResponse(reminderDao.insertReminder(new Reminder(reminderRequest)));
  }

  public ReminderResponse updateReminder(Long id, ReminderRequest reminderRequest) {
    try {
      Reminder reminder = new Reminder(reminderRequest);
      reminder.setId(id);
      return new ReminderResponse(reminderDao.updateReminder(reminder));
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage());

    }

  }

  public Boolean deleteReminder(Long id) {
    return reminderDao.deleteReminderById(id);
  }

  public Reminder getReminder(Long id) {
    return reminderDao.selectReminderById(id)
        .orElseThrow(
            () -> new NotFoundException(String.format("Reminder with id %s not found", id)));
  }
}
