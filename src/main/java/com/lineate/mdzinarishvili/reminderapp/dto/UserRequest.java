package com.lineate.mdzinarishvili.reminderapp.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserRequest {
  private String username;
  private float timezoneOffsetHours;
  private int daysBeforeReminderDelete;
}
