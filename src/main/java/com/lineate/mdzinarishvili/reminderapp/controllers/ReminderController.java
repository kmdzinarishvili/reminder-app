package com.lineate.mdzinarishvili.reminderapp.controllers;


import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import com.lineate.mdzinarishvili.reminderapp.services.ReminderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "reminders")

public class ReminderController {
    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {

        this.reminderService = reminderService;
    }

    @GetMapping
    public List<Reminder> listReminders() {
        return reminderService.getReminders();
    }

    @GetMapping("{id}")
    public Reminder getReminderById(@PathVariable("id") Long id) {
        return reminderService.getReminder(id);
    }

    @PostMapping
    public void addMovie(@RequestBody Reminder reminder) {
        reminderService.addNewReminder(reminder);
    }

    @DeleteMapping("{id}")
    public void deleteReminder(@PathVariable("id") Long id) {
        reminderService.deleteReminder(id);
    }

    @PostMapping("update/")
    public void updateReminder(@RequestBody Reminder reminder) {
        reminderService.updateReminder(reminder);
    }

}
