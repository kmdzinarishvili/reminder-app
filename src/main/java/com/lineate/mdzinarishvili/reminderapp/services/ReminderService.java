package com.lineate.mdzinarishvili.reminderapp.services;

import com.lineate.mdzinarishvili.reminderapp.dao.ReminderDAO;
import com.lineate.mdzinarishvili.reminderapp.dao.UserDao;
import com.lineate.mdzinarishvili.reminderapp.dto.GetRemindersRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderCompletedRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderResponse;
import com.lineate.mdzinarishvili.reminderapp.dto.UsersResponse;
import com.lineate.mdzinarishvili.reminderapp.enums.TimePeriod;
import com.lineate.mdzinarishvili.reminderapp.exceptions.InvalidInputException;
import com.lineate.mdzinarishvili.reminderapp.exceptions.NotFoundException;
import com.lineate.mdzinarishvili.reminderapp.models.Label;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.stream.Collectors;
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

  private List<Reminder> groupReminders(List<Reminder> reminders){
    List<Reminder> filtered = new ArrayList<>();
    reminders.forEach(r ->{
      if(filtered.stream().anyMatch(rem-> rem.getId().equals(r.getId()))){
        Reminder reminder =  filtered.stream()
            .filter(rem-> rem.getId().equals(r.getId()))
            .findFirst()
            .orElse(null);
        assert reminder != null;
        reminder.addLabels(r.getLabels());
      }else{
        filtered.add(r);
      }
    });
    return filtered;
  }
  public List<Reminder> getRemindersOrderByPriority() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long user_id = user.getId();
    log.info("reminder service get all reminders by priority function called by user with username: {}",
        user.getName());
    List<Reminder> reminders = reminderDao.selectRemindersOrderByPriority(user_id);
    return groupReminders(reminders);
  }

  public List<Reminder> getRemindersOrderByCreation() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long user_id = user.getId();
    log.info("reminder service get all reminders by creation function called by user with username: {}",
        user.getName());
    List<Reminder> reminders = reminderDao.selectRemindersOrderByCreationDate(user_id);
    return groupReminders(reminders);
  }

  public List<Reminder> getReminders() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long user_id = user.getId();
    log.info("reminder service get all reminders function called by user with username: {}",
        user.getName());
    List<Reminder> reminders = reminderDao.selectReminders(user_id);
    return groupReminders(reminders);
  }

  public boolean markReminderCompleted(ReminderCompletedRequest reminderCompletedRequest) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service mark reminder completed function called by user with username: {} with data: {}",
        user.getName(), reminderCompletedRequest);
    boolean res = reminderDao.setCompletedDate(reminderCompletedRequest.getDate(),
        reminderCompletedRequest.getId());
    log.info(
        "reminder service setting date of last activity to now for completion user with username: {}",
        user.getName());
    userDao.setDateOfLastActivityNow(user.getId());
    return res;
  }

  private LocalDateTime getDailyOverdueReminderDate(Reminder reminder, LocalDateTime today){
    LocalDateTime reminderDate = reminder.getDate();
    LocalDateTime date;
    if (reminderDate.toLocalTime().compareTo(today.toLocalTime()) >= 0) {
       date = today.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
    } else {
      date = today.minusDays(1).withHour(reminderDate.getHour())
          .withMinute(reminderDate.getMinute());
    }
    date = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
    return date;
  }
  private LocalDateTime getWeeklyOverdueReminderDate(Reminder reminder, LocalDateTime today){
    LocalDateTime reminderDate = reminder.getDate();
    LocalDateTime date;
    if (reminderDate.toLocalDate().getDayOfWeek() !=
        today.toLocalDate().getDayOfWeek() ||
        reminderDate.toLocalTime().compareTo(today.toLocalTime()) >= 0) {
      date = LocalDateTime.now()
          .with(TemporalAdjusters.previous(reminder.getDate().toLocalDate().getDayOfWeek()));
    } else {
      date = today.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
    }
    return date;
  }
  private LocalDateTime getMonthlyOverdueReminderDate(Reminder reminder, LocalDateTime today){
    LocalDateTime reminderDate = reminder.getDate();
    LocalDateTime date;
    if (reminderDate.toLocalDate().getDayOfMonth() <
        today.toLocalDate().getDayOfMonth() ||
        (reminderDate.toLocalDate().getDayOfMonth() ==
            today.toLocalDate().getDayOfMonth()
            &&
            reminderDate.toLocalTime().compareTo(today.toLocalTime()) >= 0)) {
      date = LocalDateTime.now()
          .withDayOfMonth(reminderDate.toLocalDate().getDayOfMonth());
    } else {
      date = today.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
    }
    return date;
  }
  public List<Reminder> getOverdueReminders() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long user_id = user.getId();
    log.info("reminder service get overdue reminders function called by user with username: {}",
        user.getName());
    List<Reminder> reminders = reminderDao.selectOverdueReminders(user_id);
    reminders = groupReminders(reminders);
    List<Reminder> overdueReminders = new ArrayList<>();
    LocalDateTime today = LocalDateTime.now();
    reminders.forEach((reminder) -> {
      switch (reminder.getRecurrence()) {
        case DAILY ->
          reminder.setDate(getDailyOverdueReminderDate(reminder, today));
        case WEEKLY ->
          reminder.setDate(getWeeklyOverdueReminderDate(reminder, today));
        case MONTHLY ->
          reminder.setDate(getMonthlyOverdueReminderDate(reminder, today));
      }
      overdueReminders.add(reminder);
    });
    return overdueReminders;
  }

  private boolean checkInRange(LocalDateTime date, LocalDateTime start, LocalDateTime end){
    return date.isAfter(start) && date.isBefore(end);
  }

  private LocalDateTime calculateDailyDate(LocalDateTime date, LocalDateTime dateWithDesiredTime){
      return date.withHour(dateWithDesiredTime.getHour()).withMinute(dateWithDesiredTime.getMinute());
  }
  private LocalDateTime getWeeklyReminderDate(int start, int end, LocalDateTime startDate, LocalDateTime reminderDate, boolean getFirst){
    LocalDateTime finalDate = null;
    for (int i = start; i < end; i++) {
      LocalDateTime date = startDate.plusDays(i);
      if (reminderDate.getDayOfWeek() ==
          date.getDayOfWeek()) {
        finalDate = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
        if(getFirst){
          return finalDate;
        }
      }
    }
    return finalDate;
  }

  private LocalDateTime getMonthlyReminderDate(int start, int end, LocalDateTime startDate, LocalDateTime reminderDate, boolean getFirst) {
    LocalDateTime finalDate = null;
    for (int i = start; i < end; i++) {
      LocalDateTime date = startDate.plusDays(i);
      if (reminderDate.getDayOfMonth() ==
          date.getDayOfMonth()) {
        finalDate = date.withHour(reminderDate.getHour()).withMinute(reminderDate.getMinute());
        if(getFirst){
          return finalDate;
        }
      }
    }
    return finalDate;
  }

  public List<ReminderResponse> getOldReminders() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("reminder service get old reminders function called by user with username: {}",
        user.getName());
    int daysBefore = user.getDaysBeforeReminderDelete();
    log.info("user info {} how many days reminders shown {}", user, daysBefore);
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime daysBeforeDate = LocalDateTime.now().minusDays(daysBefore);
    List<Reminder> reminders = getReminders();
    List<Reminder> oldReminders = new ArrayList<>();
    reminders.forEach((reminder) -> {
      LocalDateTime reminderDate = reminder.getDate();
      switch (reminder.getRecurrence()) {
        case NEVER:
          if (reminder.getWhenCompleted()!=null &&checkInRange(LocalDateTime.ofInstant(reminder.getWhenCompleted(), ZoneOffset.UTC), daysBeforeDate, today) ) {
            oldReminders.add(reminder);
          }
          break;
        case DAILY:
          if( reminder.getWhenCompleted()!=null&& checkInRange(LocalDateTime.ofInstant(reminder.getWhenCompleted(), ZoneOffset.UTC), daysBeforeDate, today)) {
            reminder.setDate(
                calculateDailyDate(reminder.getWhichCompleted().atStartOfDay(), reminderDate));
            oldReminders.add(reminder);
          }
          break;
        case WEEKLY:
          if( reminder.getWhenCompleted()!=null&& checkInRange(LocalDateTime.ofInstant(reminder.getWhenCompleted(), ZoneOffset.UTC), daysBeforeDate, today)) {
            LocalDateTime weeklyDate =
                getWeeklyReminderDate(0, daysBefore, daysBeforeDate, LocalDateTime.of(reminder.getWhichCompleted(), reminderDate.toLocalTime()), false);
            if (weeklyDate != null) {
              reminder.setDate(weeklyDate);
              oldReminders.add(reminder);
            }
          }
          break;
        case MONTHLY:
          if( reminder.getWhenCompleted()!=null&& checkInRange(LocalDateTime.ofInstant(reminder.getWhenCompleted(), ZoneOffset.UTC), daysBeforeDate, today)) {
            LocalDateTime monthlyDate =
                getMonthlyReminderDate(0, daysBefore, daysBeforeDate, LocalDateTime.of(reminder.getWhichCompleted(), reminder.getDate().toLocalTime()), false);
            if (monthlyDate != null) {
              reminder.setDate(monthlyDate);
              oldReminders.add(reminder);
            }
          }
          break;
      }
    });
    return oldReminders.stream().map(ReminderResponse::new).collect(
        Collectors.toList());
  }
  private boolean checkUpcomingNeverReminder(Reminder reminder, LocalDateTime date ){
    return reminder.getDate().toLocalDate().equals(date.toLocalDate())&&
        reminder.getDate().toLocalTime().compareTo(date.toLocalTime())>=0&&
        reminder.getWhichCompleted()==null;
  }

  private boolean checkUpcomingReminder(LocalDate reminderCompleted, LocalDateTime date){
    return reminderCompleted ==null || reminderCompleted.isBefore(date.toLocalDate());
  }
  private List<Reminder> getRemindersDay(LocalDateTime date, List<Reminder> reminders) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("reminder service get reminders on the day {}  called by user with username: {}",
        date, user.getName());
    List<Reminder> dateReminders = new ArrayList<>();
    reminders.forEach((reminder) -> {
      LocalTime reminderTime = reminder.getDate().toLocalTime();
      switch (reminder.getRecurrence()) {
        case NEVER:
          if (checkUpcomingNeverReminder(reminder, date)) {
            reminder.setDate(date.withHour(reminderTime.getHour()).withMinute(reminderTime.getMinute()));
            dateReminders.add(reminder);
          }
          break;
        case DAILY:
          if(checkUpcomingReminder(reminder.getWhichCompleted(), date)) {
            reminder.setDate(
                date.withHour(reminderTime.getHour()).withMinute(reminderTime.getMinute()));
            dateReminders.add(reminder);
          }
          break;
        case WEEKLY:
          if (reminder.getDate().getDayOfWeek() ==
              date.getDayOfWeek() && checkUpcomingReminder(reminder.getWhichCompleted(), date)) {
            reminder.setDate(date.withHour(reminderTime.getHour()).withMinute(reminderTime.getMinute()));
            dateReminders.add(reminder);
          }
          break;
        case MONTHLY:
          if (reminder.getDate().getDayOfMonth() ==
              date.getDayOfMonth() && checkUpcomingReminder(reminder.getWhichCompleted(), date)) {
            reminder.setDate(date.withHour(reminderTime.getHour()).withMinute(reminderTime.getMinute()));
            dateReminders.add(reminder);
          }
          break;
      }
    });
    return dateReminders;
  }


  private List<Reminder> getRemindersPeriod(LocalDateTime startDate, int howManyDays, List<Reminder> reminders) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders in between {} for {} days  function called by user with username: {}",
        startDate, howManyDays, user.getName());
    LocalDateTime end = LocalDateTime.now().plusDays(howManyDays);
    List<Reminder> newReminders = new ArrayList<>();
    reminders.forEach((reminder) -> {
      LocalDateTime reminderDate = reminder.getDate();
      LocalDateTime date;
      switch (reminder.getRecurrence()) {
        case NEVER:
          if (checkInRange(reminderDate, startDate, end)&& reminder.getWhichCompleted()==null) {
            newReminders.add(reminder);
          }
          break;
        case DAILY:
          for (int i =0; i < howManyDays; i++) {
            if (checkUpcomingReminder(reminder.getWhichCompleted(), startDate.plusDays(i))) {
              date = calculateDailyDate(startDate.plusDays(i), reminderDate);
              reminder.setDate(date);
              newReminders.add(reminder);
              break;
            }
          }
          break;
        case WEEKLY:
          date = getWeeklyReminderDate(1,howManyDays, startDate, reminderDate, true);
          if (date!= null && checkUpcomingReminder(reminder.getWhichCompleted(), date)){
            reminder.setDate(date);
            newReminders.add(reminder);
          }
          break;
        case MONTHLY:
          date = getMonthlyReminderDate(1, howManyDays,  startDate, reminderDate, true);
          if (date!=null&& checkUpcomingReminder(reminder.getWhichCompleted(), date)){
              reminder.setDate(date);
              newReminders.add(reminder);
          }
          break;
      }
    });
    return newReminders;
  }

  public List<Reminder> getRemindersToday() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders function called for today by user with username: {}",
        user.getName());
    List<Reminder> reminders = getReminders();
    return getRemindersDay(LocalDateTime.now(), reminders);
  }
  public List<ReminderResponse> getRemindersTodayByCreation() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders function called for today by user with username: {}",
        user.getName());
    List<Reminder> reminders = getRemindersOrderByCreation();
    return getRemindersDay(LocalDateTime.now(),reminders).stream().map(ReminderResponse::new).collect(
        Collectors.toList());
  }
  public List<ReminderResponse> getRemindersTodayByPriority() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders function called for today by user with username: {}",
        user.getName());
    List<Reminder> reminders = getRemindersOrderByPriority();
    return getRemindersDay(LocalDateTime.now(),reminders).stream().map(ReminderResponse::new).collect(
        Collectors.toList());
  }
  public List<Reminder> getRemindersTomorrow() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders function called for tomorrow by user with username: {}",
        user.getName());
    List<Reminder> reminders = getReminders();
    return  getRemindersDay(LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1), reminders);
  }
  public List<Reminder> getRemindersWeek() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders function called for this week by user with username: {}",
        user.getName());
    LocalDateTime today = LocalDateTime.now();
    int lengthOfWeek = 7;
    List<Reminder> reminders = getReminders();
    return getRemindersPeriod(today, lengthOfWeek, reminders );
  }

  public ReminderResponse addNewReminder(ReminderRequest reminderRequest) {
    User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get add new reminder function called by user with username: {} with data {}",
        principal.getName(), reminderRequest);
    Reminder reminder = new Reminder(reminderRequest);
    if (reminderRequest.getUserEmail() == null && reminderRequest.getUserUsername() == null) {
      User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      log.info(
          "reminder service new reminder assigned to self: {} with data {}",
          user.getName(), reminderRequest);
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
              reminderRequest.getUserUsername(), principal.getName());
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
              reminderRequest.getUserEmail(), principal.getName());
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
        principal.getName());
    userDao.setDateOfLastActivityNow(principal.getId());
    return reminderResponse;
  }

  public ReminderResponse updateReminder(Long id, ReminderRequest reminderRequest) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    try {
      log.info(
          "reminder service update reminder assigned by user: {} with data {}",
          user.getName(), reminderRequest);
      Reminder reminder = new Reminder(reminderRequest);
      reminder.setUser(user);
      reminder.setId(id);
      return new ReminderResponse(reminderDao.updateReminder(reminder));
    } catch (Exception e) {
      log.error(
          "reminder service update FAILED assigned by user: {} with data {}",
          user.getName(), reminderRequest);
      throw new IllegalStateException(e.getMessage());
    }

  }

  public Boolean deleteReminder(Long id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service delete reminder by user: {} for reminder with id {}",
        user.getName(), id);
    return reminderDao.deleteReminderById(id);
  }

  public Boolean rejectReminder(Long id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (reminderDao.isPending(id)) {
      log.info(
          "reminder service reject reminder by user: {} for reminder with id {}",
          user.getName(), id);
      return reminderDao.deleteReminderById(id);
    } else {
      log.error(
          "reminder service reject reminder FAILED by user: {} for reminder with id {} because reminder not pending",
          user.getName(), id);
      throw new InvalidInputException("This reminder is not pending, cannot be rejected.");
    }
  }

  public Boolean acceptReminder(Long id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (reminderDao.isPending(id)) {
      log.info(
          "reminder service accept reminder by user: {} for reminder with id {}",
          user.getName(), id);
      return reminderDao.setAcceptedTrue(id);
    } else {
      log.error(
          "reminder service accept reminder FAILED by user: {} for reminder with id {} because reminder not pending",
          user.getName(), id);
      throw new InvalidInputException("This reminder is not pending, cannot be rejected.");
    }
  }


  public ReminderResponse getReminder(Long id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminder by id ({}) by user: {}",
        id, user.getName());
    try{
      return new ReminderResponse(groupReminders(reminderDao.selectReminderById(id)).get(0));
    }catch (Exception e) {
      log.error(
          "reminder service get reminder FAILED by user: {} for reminder with id {} because reminder id not found",
          user.getName(), id);
      throw new NotFoundException(String.format("Reminder with id %s not found", id));
    }
  }

  public List<ReminderResponse> getRemindersTomorrowByCreation() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders function called for today by user with username: {}",
        user.getName());
    List<Reminder> reminders = getRemindersOrderByCreation();
    return getRemindersDay(LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1),reminders).stream().map(ReminderResponse::new).collect(
        Collectors.toList());
  }

  public List<ReminderResponse> getRemindersTomorrowByPriority() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders function called for today by user with username: {}",
        user.getName());
    List<Reminder> reminders = getRemindersOrderByPriority();
    return getRemindersDay(LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1),reminders).stream().map(ReminderResponse::new).collect(
        Collectors.toList());
  }

  public List<ReminderResponse> getRemindersWeekByCreation() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders function called for this week order by creation by user with username: {}",
        user.getName());
    LocalDateTime today = LocalDateTime.now();
    int lengthOfWeek = 7;
    List<Reminder> reminders = getRemindersOrderByCreation();
    return getRemindersPeriod(today, lengthOfWeek, reminders ).stream().map(ReminderResponse::new).collect(
        Collectors.toList());
  }
  public List<ReminderResponse> getRemindersWeekByPriority() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "reminder service get reminders function called for this week order by prioirty by user with username: {}",
        user.getName());
    LocalDateTime today = LocalDateTime.now();
    int lengthOfWeek = 7;
    List<Reminder> reminders = getRemindersOrderByPriority();
    return getRemindersPeriod(today, lengthOfWeek, reminders).stream().map(ReminderResponse::new).collect(
        Collectors.toList());
  }
}

