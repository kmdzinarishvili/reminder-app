package com.lineate.mdzinarishvili.reminderapp.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReminderCompletedRequest {
  LocalDateTime date;
  Long id;
}
