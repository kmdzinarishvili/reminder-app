package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.models.Token;
import com.lineate.mdzinarishvili.reminderapp.models.TokenMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TokenDaoImpl implements TokenDao {
  private final JdbcTemplate jdbcTemplate;

  private final String SQL_FIND_TOKEN = "select t.token_id, t.token_value, t.revoked, " +
      "t.expired, t.user_id from tokens t " +
      " where token_value = ? ";
  private final String SQL_ALL_VALID_TOKENS_BY_USER =
      "select *" +
          " from tokens t inner join users u " +
          " using (user_id)" +
          " where u.user_id = ? and (t.expired = false or t.revoked = false)";
  private final String SQL_INSERT_TOKEN =
      "insert into tokens (token_value, revoked, expired, user_id) values (?,?,?,?)";

  public TokenDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }


  public List<Token> findAllValidTokenByUser(Long id) {
    return jdbcTemplate.query(SQL_ALL_VALID_TOKENS_BY_USER, new TokenMapper(), id);
  }

  public Optional<Token> findByToken(String token) {
    return jdbcTemplate.query(SQL_FIND_TOKEN, new TokenMapper(), token).stream().findFirst();
  }

  public Optional<Token> save(Token token) {
    jdbcTemplate.update(SQL_INSERT_TOKEN, token.getToken(),
        token.isRevoked(), token.isExpired(), token.getUser().getId());
    return this.findByToken(token.getToken());
  }

  public List<Token> saveAll(List<Token> tokens) {
    List<Token> savedTokens = new ArrayList<>();
    for (Token token : tokens) {
      savedTokens.add(this.save(token).get());
    }
    return savedTokens;
  }

}
