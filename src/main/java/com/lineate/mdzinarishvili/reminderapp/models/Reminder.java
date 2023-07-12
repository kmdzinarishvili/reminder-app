package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.dao.ReminderDAO;
import com.lineate.mdzinarishvili.reminderapp.dto.ReminderRequest;
import com.lineate.mdzinarishvili.reminderapp.enums.CategoryType;
import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
  private int priority;
  private CategoryType category;
  private List<Label> labels;
  private User user;
  private Boolean acceptanceStatus;

  public Reminder(ReminderRequest reminderRequest) {
    this.title = reminderRequest.getTitle();
    this.recurrence = reminderRequest.getRecurrence();
    if (reminderRequest.getTime() != null) {
      this.date =
          LocalDateTime.of(reminderRequest.getDate(), reminderRequest.getTime());
    } else {
      this.date = LocalDateTime.of(reminderRequest.getDate(), LocalTime.of(0, 0, 0));

    }
    this.attachment = reminderRequest.getAttachment();
    this.priority = reminderRequest.getPriority();
    this.category = reminderRequest.getCategory();
  }

  public Reminder(Long id, String title, LocalDateTime date, RecurrenceType recurrence,
                  byte[] attachment, int priority, CategoryType category,
                  Boolean acceptanceStatus) {
    this.id = id;
    this.title = title;
    this.date = date;
    this.recurrence = recurrence;
    this.attachment = attachment;
    this.priority = priority;
    this.category = category;
    this.acceptanceStatus = acceptanceStatus;
  }

  @Override
  public int compareTo(Reminder reminder) {
    if (this.getDate() == null || reminder.getDate() == null) {
      return 0;
    }
    return getDate().compareTo(reminder.getDate());
  }
}
