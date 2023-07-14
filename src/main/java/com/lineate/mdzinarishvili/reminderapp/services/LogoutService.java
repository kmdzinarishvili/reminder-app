package com.lineate.mdzinarishvili.reminderapp.services;

import com.lineate.mdzinarishvili.reminderapp.dao.TokenDaoImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {
  private final TokenDaoImpl tokenDaoImpl;

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    String jwtHeaderBeginning = "Bearer ";
    if (authHeader == null || !authHeader.startsWith(jwtHeaderBeginning)) {
      log.warn(
          "Invalid authHeader passed to logout: {}", authHeader);
      return;
    }
    jwt = authHeader.substring(jwtHeaderBeginning.length());
    var storedToken = tokenDaoImpl.findByToken(jwt)
        .orElse(null);
    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenDaoImpl.save(storedToken);
      SecurityContextHolder.clearContext();
    } else {
      log.warn(
          "Invalid authHeader with stored token null passed to logout: {}", authHeader);
    }
  }
}
