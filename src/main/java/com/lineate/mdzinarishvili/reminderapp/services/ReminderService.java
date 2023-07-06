package com.lineate.mdzinarishvili.reminderapp.services;

import com.lineate.mdzinarishvili.reminderapp.dao.ReminderDAO;
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

    public void addNewReminder(Reminder reminder) {
        int result = reminderDao.insertReminder(reminder);
        if (result != 1) {
            throw new IllegalStateException("oops something went wrong, could not insert");
        }
    }

    public void updateReminder(Reminder reminder) {
        Optional<Reminder> movies = reminderDao.selectReminderById(reminder.getId());
        movies.ifPresentOrElse(movie -> {
            int result = reminderDao.updateReminder(reminder);
            if (result != 1) {
                throw new IllegalStateException("oops could not update reminder");
            }
        }, () -> {
            throw new NotFoundException(String.format("Reminder with id %s not found", reminder.getId()));
        });
    }

    public void deleteReminder(Long id) {
        Optional<Reminder> movies = reminderDao.selectReminderById(id);
        movies.ifPresentOrElse(movie -> {
            int result = reminderDao.deleteReminderById(id);
            if (result != 1) {
                throw new IllegalStateException("oops could not delete reminder");
            }
        }, () -> {
            throw new NotFoundException(String.format("Reminder with id %s not found", id));
        });
    }

    public Reminder getReminder(Long id) {
        return reminderDao.selectReminderById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Reminder with id %s not found", id)));
    }
}
