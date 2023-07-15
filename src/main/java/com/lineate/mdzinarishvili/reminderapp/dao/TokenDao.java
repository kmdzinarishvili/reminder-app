package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.models.Token;

import java.util.List;
import java.util.Optional;

public interface TokenDao {
  public List<Token> findAllValidTokenByUser(Long id);

  public Optional<Token> findByToken(String token);

  public Optional<Token> save(Token token);

  public List<Token> saveAll(List<Token> tokens);

}
