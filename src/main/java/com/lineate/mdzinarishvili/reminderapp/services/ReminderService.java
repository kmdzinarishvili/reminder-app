package com.lineate.mdzinarishvili.reminderapp.services;

import com.lineate.mdzinarishvili.reminderapp.dao.ReminderDAO;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderResponse;
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

    public ReminderResponse addNewReminder(ReminderRequest reminderRequest) {
        return new ReminderResponse(reminderDao.insertReminder(new Reminder(reminderRequest)));
    }

    public ReminderResponse updateReminder(Long id, ReminderRequest reminderRequest) {
        try {
            Reminder reminder = new Reminder(reminderRequest);
            reminder.setId(id);
            return new ReminderResponse(reminderDao.updateReminder(reminder));
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage());

        }

    }

    public Boolean deleteReminder(Long id) {
        return reminderDao.deleteReminderById(id);
    }

    public Reminder getReminder(Long id) {
        return reminderDao.selectReminderById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Reminder with id %s not found", id)));
    }
}
