package com.lineate.mdzinarishvili.reminderapp.controllers;


import com.lineate.mdzinarishvili.reminderapp.dto.GetRemindersRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderCompletedRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderResponse;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import com.lineate.mdzinarishvili.reminderapp.services.ReminderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/reminders")
@PreAuthorize("hasRole('USER')")

public class ReminderController {
  private final ReminderService reminderService;

  public ReminderController(ReminderService reminderService) {
    this.reminderService = reminderService;
  }

  @GetMapping
  public List<Reminder> listReminders() {
    return reminderService.getReminders();
  }

  @GetMapping("/today")
  public List<Reminder> getTodayReminders() {
    return reminderService.getRemindersToday();
  }
  @GetMapping("/tomorrow")
  public List<Reminder> listRemindersTomorrow() {
    return reminderService.getRemindersTomorrow();
  }
  @GetMapping("/week")
  public List<Reminder> listRemindersThisWeek() {
    return reminderService.getRemindersWeek();
  }

  @GetMapping("/old")
  public List<Reminder> listOldReminders() {
    return reminderService.getOldReminders();
  }

  @GetMapping("/{id}")
  public Reminder getReminderById(@PathVariable("id") Long id) {
    return reminderService.getReminder(id);
  }

  @PostMapping
  public ReminderResponse addReminder(@RequestBody ReminderRequest reminderRequest) {
    return reminderService.addNewReminder(reminderRequest);
  }

  @DeleteMapping("/{id}")
  public Boolean deleteReminder(@PathVariable("id") Long id) {
    return reminderService.deleteReminder(id);
  }

  @PostMapping("/{id}/reject")
  public Boolean rejectReminder(@PathVariable("id") Long id) {
    return reminderService.rejectReminder(id);
  }

  @PostMapping("/{id}/accept")
  public Boolean acceptReminder(@PathVariable("id") Long id) {
    return reminderService.acceptReminder(id);
  }

  @PostMapping("/{id}/update")
  public ReminderResponse updateReminder(@PathVariable("id") Long id,
                                         @RequestBody ReminderRequest reminderRequest) {
    return reminderService.updateReminder(id, reminderRequest);
  }

  @GetMapping("/overdue")
  public List<Reminder> updateReminder() {
    return reminderService.getOverdueReminders();
  }

  @PostMapping("/completed")
  public boolean completeReminder(@RequestBody ReminderCompletedRequest reminderCompletedRequest) {
    return reminderService.markReminderCompleted(reminderCompletedRequest);
  }
}

