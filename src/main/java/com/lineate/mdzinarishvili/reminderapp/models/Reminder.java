package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.dto.ReminderRequest;
import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import lombok.*;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Reminder {
  private Long id;
  private String title;
  private RecurrenceType recurrence;
  private Date date;
  private byte[] attachment;

  public Reminder(ReminderRequest reminderRequest) {
    this.title = reminderRequest.getTitle();
    this.recurrence = reminderRequest.getRecurrence();
    this.date = reminderRequest.getDate();
    this.attachment = reminderRequest.getAttachment();
  }
}
