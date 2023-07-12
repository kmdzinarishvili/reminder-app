package com.lineate.mdzinarishvili.reminderapp.dto;


import com.lineate.mdzinarishvili.reminderapp.enums.CategoryType;
import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import com.lineate.mdzinarishvili.reminderapp.models.Label;
import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReminderResponse {
  private Long id;
  private String title;
  private LocalDateTime date;
  private byte[] attachment;
  private RecurrenceType recurrence;
  private int priority;
  private CategoryType category;
  private List<Label> labels;
  private String username;

  public ReminderResponse(Reminder reminder) {
    this.id = reminder.getId();
    this.title = reminder.getTitle();
    this.date = reminder.getDate();
    this.attachment = reminder.getAttachment();
    this.recurrence = reminder.getRecurrence();
    this.priority = reminder.getPriority();
    this.category = reminder.getCategory();
    this.labels = reminder.getLabels();
    this.username = reminder.getUser().getUsername();
  }
}
