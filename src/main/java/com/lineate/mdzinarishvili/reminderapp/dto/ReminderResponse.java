package com.lineate.mdzinarishvili.reminderapp.dto;


import com.lineate.mdzinarishvili.reminderapp.models.Reminder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReminderResponse {
  private String title;
  private LocalDateTime date;
  private byte[] attachment;

  public ReminderResponse(Reminder reminder) {
    this.title = reminder.getTitle();
    this.date = reminder.getDate();
    this.attachment = reminder.getAttachment();
  }
}
