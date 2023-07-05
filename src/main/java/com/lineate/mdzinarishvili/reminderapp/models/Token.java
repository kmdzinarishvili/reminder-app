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
//@Entity
public class Token {

//  @Id
//  @GeneratedValue
  public Long id;

//  @Column(unique = true)
  public String token;

//  @Enumerated(EnumType.STRING)
  public TokenType tokenType = TokenType.BEARER;

  public boolean revoked;


  public boolean expired;


//  @ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "user_id")
  public User user;

  public Token (Long id, String token, boolean revoked, boolean expired){
    this.id = id;
    this.token=token;
    this.revoked = revoked;
    this.expired = expired;
  }

}
