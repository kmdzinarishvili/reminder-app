package com.lineate.mdzinarishvili.reminderapp.models;

import com.lineate.mdzinarishvili.reminderapp.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
  public Long id;
  public String token;

  public TokenType tokenType = TokenType.BEARER;
  public boolean revoked;
  public boolean expired;
  public User user;

  public Token(Long id, String token, boolean revoked, boolean expired, User user) {
    this.id = id;
    this.token = token;
    this.revoked = revoked;
    this.expired = expired;
    this.user = user;
  }


}
