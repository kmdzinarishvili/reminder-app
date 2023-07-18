package com.lineate.mdzinarishvili.reminderapp.dto;

import com.lineate.mdzinarishvili.reminderapp.enums.RoleType;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserResponse {
  private String username;
  private String email;
  private float timezoneOffsetHours;
  private int daysBeforeReminderDelete;
  public UserResponse(User user) {
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.timezoneOffsetHours = user.getTimezoneOffsetHours();
    this.daysBeforeReminderDelete = user.getDaysBeforeReminderDelete();
  }

}
