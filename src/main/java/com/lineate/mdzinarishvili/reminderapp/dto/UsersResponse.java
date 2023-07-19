package com.lineate.mdzinarishvili.reminderapp.dto;

import com.lineate.mdzinarishvili.reminderapp.models.User;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter
@Setter
public class UsersResponse {
  private Long id;
  private String username;
  private String email;
  private LocalDateTime registrationDate;
  private LocalDateTime lastActivityDate;
  public UsersResponse(User user){
    this.id = user.getId();
    this.username = user.getName();
    this.email =user.getEmail();
    this.registrationDate = user.getRegistrationDate();
    this.lastActivityDate = user.getLastActivityDate();
  }
}
