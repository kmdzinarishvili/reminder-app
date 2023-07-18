package com.lineate.mdzinarishvili.reminderapp.dto;

import com.lineate.mdzinarishvili.reminderapp.models.User;
import java.util.Date;
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
  private Date registrationDate;
  private Date lastActivityDate;
  public UsersResponse(User user){
    this.id = user.getId();
    this.username = user.getUsername();
    this.email =user.getEmail();
    this.registrationDate = user.getRegistrationDate();
    this.lastActivityDate = user.getLastActivityDate();
  }
}
