package com.lineate.mdzinarishvili.reminderapp.services;

import com.lineate.mdzinarishvili.reminderapp.dao.ReminderDAO;
import com.lineate.mdzinarishvili.reminderapp.dao.UserDao;
import com.lineate.mdzinarishvili.reminderapp.dto.GetRemindersRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderResponse;
import com.lineate.mdzinarishvili.reminderapp.enums.TimePeriod;
import com.lineate.mdzinarishvili.reminderapp.exceptions.InvalidInputException;
import com.lineate.mdzinarishvili.reminderapp.exceptions.NotFoundException;
import com.lineate.mdzinarishvili.reminderapp.models.Label;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderService {
  private final ReminderDAO reminderDao;
  private final UserDao userDao;

  public ReminderService(ReminderDAO reminderDao, UserDao userDao) {
    this.reminderDao = reminderDao;
    this.userDao = userDao;
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
    Reminder reminder = new Reminder(reminderRequest);
    if (reminderRequest.getUserEmail() == null && reminderRequest.getUserUsername() == null) {
      User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      reminder.setUser(user);
      reminder.setAcceptanceStatus(true);
    } else {
      if (reminderRequest.getUserUsername() != null) {
        var user = userDao.selectUserByUsername(reminderRequest.getUserUsername());
        if (user.isPresent()) {
          reminder.setUser(user.get());
        } else {
          throw new InvalidInputException("User with username not found");
        }
      } else {
        var user = userDao.findByEmail(reminderRequest.getUserEmail());
        if (user.isPresent()) {
          reminder.setUser(user.get());
        } else {
          throw new InvalidInputException("User with email not found");
        }
      }
      reminder.setAcceptanceStatus(false);
    }
    List<Label> labels = new ArrayList<>();
    reminderRequest.getLabels().forEach(label -> {
      labels.add(reminderDao.getLabelByName(label));
    });
    reminder.setLabels(labels);
    return new ReminderResponse(reminderDao.insertReminder(reminder));
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

  public Boolean rejectReminder(Long id) {
    if (reminderDao.isPending(id)) {
      return reminderDao.deleteReminderById(id);
    } else {
      throw new InvalidInputException("This reminder is not pending, cannot be rejected.");
    }
  }

  public Boolean acceptReminder(Long id) {
    if (reminderDao.isPending(id)) {
      return reminderDao.setAcceptedTrue(id);
    } else {
      throw new InvalidInputException("This reminder is not pending, cannot be rejected.");
    }
  }


  public Reminder getReminder(Long id) {
    return reminderDao.selectReminderById(id)
        .orElseThrow(
            () -> new NotFoundException(String.format("Reminder with id %s not found", id)));
  }
}
