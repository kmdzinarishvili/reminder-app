package com.lineate.mdzinarishvili.reminderapp.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReminderController {

    @GetMapping("/reminders")
    public String getAll(){
        return "all";
    }



}
