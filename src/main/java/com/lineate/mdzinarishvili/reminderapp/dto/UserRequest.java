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
//  public UserRequest(User user) {
//    this.username = user.getUsername();
//    this.timezoneOffsetHours = user.getTimezoneOffsetHours();
//    this.daysBeforeReminderDelete = user.getDaysBeforeReminderDelete();
//  }

}
