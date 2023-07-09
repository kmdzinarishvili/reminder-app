package com.lineate.mdzinarishvili.reminderapp.models;//package com.alibou.security.user;


import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.lineate.mdzinarishvili.reminderapp.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class User implements UserDetails {
  private Long id;
  private String username;
  private String email;
  private String password;
  private RoleType role;
  private Date registrationDate;
  private Date lastActivityDate;
  private float timezoneOffsetHours;
  private int daysBeforeReminderDelete;


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
