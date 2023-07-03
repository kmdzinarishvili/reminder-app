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

    @GetMapping("/{id}")
    public Reminder getReminderById(@PathVariable("id") Long id) {
        return reminderService.getReminder(id);
    }

    @PostMapping
    public Reminder addReminder(@RequestBody Reminder reminder) {
        return reminderService.addNewReminder(reminder);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteReminder(@PathVariable("id") Long id) {
       return reminderService.deleteReminder(id);
    }

    @PostMapping("/update")
    public Reminder updateReminder(@RequestBody Reminder reminder) {
        return reminderService.updateReminder(reminder);
    }

}
