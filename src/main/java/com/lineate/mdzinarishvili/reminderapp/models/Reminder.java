package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.dao.ReminderDAO;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderRequest;
import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import java.time.LocalDateTime;
import lombok.*;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Reminder implements Comparable<Reminder> {
  private Long id;
  private String title;
  private RecurrenceType recurrence;
  private LocalDateTime date;
  private byte[] attachment;

  public Reminder(ReminderRequest reminderRequest) {
    this.title = reminderRequest.getTitle();
    this.recurrence = reminderRequest.getRecurrence();
    this.date = reminderRequest.getDate();
    this.attachment = reminderRequest.getAttachment();
  }

  @Override
  public int compareTo(Reminder reminder) {
    if (this.getDate() == null || reminder.getDate() == null) {
      return 0;
    }
    return getDate().compareTo(reminder.getDate());
  }
}
