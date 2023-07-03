package com.lineate.mdzinarishvili.reminderapp.services;

import com.lineate.mdzinarishvili.reminderapp.dao.ReminderDAO;
import com.lineate.mdzinarishvili.reminderapp.exceptions.InvalidInputException;
import com.lineate.mdzinarishvili.reminderapp.exceptions.NotFoundException;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
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

    public Reminder addNewReminder(Reminder reminder) {
        return reminderDao.insertReminder(reminder);
    }

    public Reminder updateReminder(Reminder reminder) {
        if(!reminderDao.isIdValid(reminder.getId())){
            throw new InvalidInputException("No reminder with this id");
        }
       return reminderDao.updateReminder(reminder);

    }

    public Boolean deleteReminder(Long id) {
        if(!reminderDao.isIdValid(id)){
            throw new InvalidInputException("No reminder with this id");
        }
        return reminderDao.deleteReminderById(id);
    }

    public Reminder getReminder(Long id) {
        return reminderDao.selectReminderById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Reminder with id %s not found", id)));
    }
}
