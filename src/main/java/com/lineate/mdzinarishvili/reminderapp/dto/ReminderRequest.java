package com.lineate.mdzinarishvili.reminderapp.dto;

import com.lineate.mdzinarishvili.reminderapp.enums.RecurrenceType;
import java.time.LocalDateTime;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReminderRequest {
  private String title;
  private RecurrenceType recurrence;
  private LocalDateTime date;
  private byte[] attachment;
}
