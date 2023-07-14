package com.lineate.mdzinarishvili.reminderapp.services;

import com.lineate.mdzinarishvili.reminderapp.dao.ReminderDAO;
import com.lineate.mdzinarishvili.reminderapp.dao.UserDao;
import com.lineate.mdzinarishvili.reminderapp.dto.GetRemindersRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderCompletedRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderResponse;
import com.lineate.mdzinarishvili.reminderapp.enums.TimePeriod;
import com.lineate.mdzinarishvili.reminderapp.exceptions.DatabaseException;
import com.lineate.mdzinarishvili.reminderapp.exceptions.InvalidInputException;
import com.lineate.mdzinarishvili.reminderapp.exceptions.NotFoundException;
import com.lineate.mdzinarishvili.reminderapp.models.Label;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReminderService {
  private final ReminderDAO reminderDao;
  private final UserDao userDao;

  public ReminderService(ReminderDAO reminderDao, UserDao userDao) {
    this.reminderDao = reminderDao;
    this.userDao = userDao;
  }

  public List<Reminder> getReminders() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long user_id = user.getId();
    log.info("reminder service get all reminders function called by user with username: {}",
        user.getUsername());
    return reminderDao.selectReminders(user_id);

  }

  public boolean markReminderCompleted(ReminderCompletedRequest reminderCompletedRequest) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service mark reminder completed function called by user with username: {} with data: {}",
        user.getUsername(), reminderCompletedRequest);
    boolean res = reminderDao.setCompletedDate(reminderCompletedRequest.getDate(),
        reminderCompletedRequest.getId());
    log.info(
        "reminder service setting date of last activity to now for completion user with username: {}",
        user.getUsername());
    userDao.setDateOfLastActivityNow(user.getId());
    return res;
  }

  public List<Reminder> getOverdueReminders() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long user_id = user.getId();
    log.info("reminder service get overdue reminders function called by user with username: {}",
        user.getUsername());
    return reminderDao.selectOverdueReminders(user_id);

  }

  public List<Reminder> getOldReminders() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("reminder service get old reminders function called by user with username: {}",
        user.getUsername());
    int daysBefore = user.getDaysBeforeReminderDelete();
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime daysBeforeDate = LocalDateTime.now().minusDays(daysBefore);
    List<Reminder> reminders = getReminders();
    List<Reminder> oldReminders = new ArrayList<>();
    reminders.forEach((reminder) -> {
      LocalDateTime reminderDate = reminder.getDate();
      LocalDateTime date;
      switch (reminder.getRecurrence()) {
        case NEVER:
          if (reminderDate.isAfter(daysBeforeDate)
              &&
              reminderDate.isBefore(today)) {
            oldReminders.add(reminder);
          }
          break;
        case DAILY:
          date = daysBeforeDate.plusDays(daysBefore - 1);
          date = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
          reminder.setDate(date);
          oldReminders.add(reminder);
          break;
        case WEEKLY:
          for (int i = 0; i < daysBefore; i++) {
            date = daysBeforeDate.plusDays(i);
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
            date = daysBeforeDate.plusDays(i);
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
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("reminder service get reminders on the day {}  called by user with username: {}",
        date, user.getUsername());
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


  private List<Reminder> getRemindersPeriod(LocalDateTime startDate, int howManyDays) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders in between {} for {} days  function called by user with username: {}",
        startDate, howManyDays, user.getUsername());
    LocalDateTime end = LocalDateTime.now().plusDays(howManyDays);
    List<Reminder> reminders = getReminders();
    List<Reminder> newReminders = new ArrayList<>();
    reminders.forEach((reminder) -> {
      LocalDateTime reminderDate = reminder.getDate();
      switch (reminder.getRecurrence()) {
        case NEVER:
          if (reminderDate.isAfter(startDate)
              &&
              reminderDate.isBefore(end)) {
            newReminders.add(reminder);
          }
          break;
        case DAILY:
          for (int i = 1; i <= howManyDays; i++) {
            LocalDateTime date = startDate.plusDays(i);
            date = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
            reminder.setDate(date);
            newReminders.add(reminder);
            break;
          }
          break;
        case WEEKLY:
          for (int i = 1; i <= howManyDays; i++) {
            LocalDateTime date = startDate.plusDays(i);
            if (reminder.getDate().getDayOfWeek() ==
                date.getDayOfWeek()) {
              date = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
              reminder.setDate(date);
              newReminders.add(reminder);
              break;
            }
          }
          break;
        case MONTHLY:
          for (int i = 1; i <= howManyDays; i++) {
            LocalDateTime date = startDate.plusDays(i);
            if (reminder.getDate().getDayOfMonth() ==
                date.getDayOfMonth()) {
              date = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
              reminder.setDate(date);
              newReminders.add(reminder);
              break;
            }
          }
          break;
      }
    });
    return newReminders;
  }

  public List<Reminder> getReminders(GetRemindersRequest getRemindersRequest) {
    TimePeriod timePeriod = getRemindersRequest.getTimePeriod();
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders function called with time period {} by user with username: {}",
        timePeriod, user.getUsername());
    switch (timePeriod) {
      case TODAY:
        return getRemindersDay(LocalDateTime.now());
      case TOMORROW:
        return getRemindersDay(LocalDateTime.now().plusDays(1));
      case WEEK:
        LocalDateTime today = LocalDateTime.now();
        int lengthOfWeek = 7;
        return getRemindersPeriod(today, lengthOfWeek);
    }
    log.error(
        "reminder service get reminders function called with invalid time period by user with username: {}",
        user.getUsername());
    throw new InvalidInputException("Invalid Time Period indicated");
  }

  public ReminderResponse addNewReminder(ReminderRequest reminderRequest) {
    User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get add new reminder function called by user with username: {} with data {}",
        principal.getUsername(), reminderRequest);
    Reminder reminder = new Reminder(reminderRequest);
    if (reminderRequest.getUserEmail() == null && reminderRequest.getUserUsername() == null) {
      User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      log.info(
          "reminder service new reminder assigned to self: {} with data {}",
          user.getUsername(), reminderRequest);
      reminder.setUser(user);
      reminder.setAcceptanceStatus(true);
    } else {
      if (reminderRequest.getUserUsername() != null) {
        var user = userDao.selectUserByUsername(reminderRequest.getUserUsername());
        if (user.isPresent()) {
          log.info(
              "reminder service new reminder assigned by username: {} with data {}",
              reminderRequest.getUserUsername(), reminderRequest);
          reminder.setUser(user.get());
        } else {
          log.error(
              "reminder service new reminder add failed with invalid username: {} by {}",
              reminderRequest.getUserUsername(), principal.getUsername());
          throw new InvalidInputException("User with username not found");
        }
      } else {
        var user = userDao.findByEmail(reminderRequest.getUserEmail());
        if (user.isPresent()) {
          log.error(
              "reminder service new reminder assigned by email: {} with data {}",
              reminderRequest.getUserEmail(), reminderRequest);
          reminder.setUser(user.get());
        } else {
          log.error(
              "reminder service new reminder add failed with invalid email: {} by {}",
              reminderRequest.getUserEmail(), principal.getUsername());
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
    ReminderResponse reminderResponse = new ReminderResponse(reminderDao.insertReminder(reminder));
    log.info(
        "reminder service setting date of last activity to now for completion user with username: {}",
        principal.getUsername());
    userDao.setDateOfLastActivityNow(principal.getId());
    return reminderResponse;
  }

  public ReminderResponse updateReminder(Long id, ReminderRequest reminderRequest) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    try {
      log.info(
          "reminder service update reminder assigned by user: {} with data {}",
          user.getUsername(), reminderRequest);
      Reminder reminder = new Reminder(reminderRequest);
      reminder.setId(id);
      return new ReminderResponse(reminderDao.updateReminder(reminder));
    } catch (Exception e) {
      log.error(
          "reminder service update FAILED assigned by user: {} with data {}",
          user.getUsername(), reminderRequest);
      throw new IllegalStateException(e.getMessage());
    }

  }

  public Boolean deleteReminder(Long id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service delete reminder by user: {} for reminder with id {}",
        user.getUsername(), id);
    return reminderDao.deleteReminderById(id);
  }

  public Boolean rejectReminder(Long id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (reminderDao.isPending(id)) {
      log.info(
          "reminder service reject reminder by user: {} for reminder with id {}",
          user.getUsername(), id);
      return reminderDao.deleteReminderById(id);
    } else {
      log.error(
          "reminder service reject reminder FAILED by user: {} for reminder with id {} because reminder not pending",
          user.getUsername(), id);
      throw new InvalidInputException("This reminder is not pending, cannot be rejected.");
    }
  }

  public Boolean acceptReminder(Long id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (reminderDao.isPending(id)) {
      log.info(
          "reminder service accept reminder by user: {} for reminder with id {}",
          user.getUsername(), id);
      return reminderDao.setAcceptedTrue(id);
    } else {
      log.error(
          "reminder service accept reminder FAILED by user: {} for reminder with id {} because reminder not pending",
          user.getUsername(), id);
      throw new InvalidInputException("This reminder is not pending, cannot be rejected.");
    }
  }


  public Reminder getReminder(Long id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminder by id ({}) by user: {}",
        id, user.getUsername());
    return reminderDao.selectReminderById(id)
        .orElseThrow(() -> {
          log.error(
              "reminder service get reminder FAILED by user: {} for reminder with id {} because reminder id not found",
              user.getUsername(), id);
          return new NotFoundException(String.format("Reminder with id %s not found", id));
        });
  }
}
