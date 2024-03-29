package com.lineate.mdzinarishvili.reminderapp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lineate.mdzinarishvili.reminderapp.dto.AuthenticationRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.AuthenticationResponse;
import com.lineate.mdzinarishvili.reminderapp.dao.UserDaoImpl;
import com.lineate.mdzinarishvili.reminderapp.dto.RegisterRequest;
import com.lineate.mdzinarishvili.reminderapp.enums.RoleType;
import com.lineate.mdzinarishvili.reminderapp.exceptions.InvalidInputException;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import com.lineate.mdzinarishvili.reminderapp.models.Token;
import com.lineate.mdzinarishvili.reminderapp.dao.TokenDaoImpl;
import com.lineate.mdzinarishvili.reminderapp.enums.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
  private final UserDaoImpl userDao;
  private final TokenDaoImpl tokenDaoImpl;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;


  public AuthenticationResponse register(RegisterRequest request) {
    log.info("Authentication service registration function called with username: {} and email: {}",
        request.getUsername(), request.getEmail());
    if (request.getUsername() == null) {
      log.error("Authentication service registration function called with username null");
      throw new InvalidInputException("username cannot be empty");
    } else if (request.getEmail() == null) {
      log.error("Authentication service registration function called with email null");
      throw new InvalidInputException("email cannot be empty");
    } else if (request.getPassword() == null) {
      log.error("Authentication service registration function called with password null");
      throw new InvalidInputException("password cannot be empty");
    }
    var user = User.builder()
        .name(request.getUsername())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(RoleType.USER)
        .build();
    var savedUser = userDao.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .role(savedUser.getRole().toString())
        .build();
  }

  public AuthenticationResponse registerOrUpdateAdmin(RegisterRequest request) {
    log.info("Authentication service update admin function called with username: {} and email: {}",
        request.getUsername(), request.getEmail());
    var user = User.builder()
        .name(request.getUsername())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
    User savedUser = userDao.updateOrInsertUser(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    log.info(
        "Authentication service authenticate function called with username: {}",
        request.getEmail());

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = userDao.findByEmail(request.getEmail()).orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .role(user.getRole().toString())
        .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    log.info(
        "Authentication service save token function called for user with username: {}",
        user.getName());
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenDaoImpl.save(token);
  }

  private void revokeAllUserTokens(User user) {
    log.info(
        "Authentication service revoke tokens function called for user with username: {}",
        user.getName());
    var validUserTokens = tokenDaoImpl.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenDaoImpl.saveAll(validUserTokens);
  }

  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String username;
    String jwtHeaderBeginning = "Bearer ";
    if (authHeader == null || !authHeader.startsWith(jwtHeaderBeginning)) {
      log.warn(
          "Invalid authHeader passed to refresh token: {}", authHeader);
      return;
    }
    refreshToken = authHeader.substring(jwtHeaderBeginning.length());
    username = jwtService.extractUsername(refreshToken);
    if (username != null) {
      var user = this.userDao.findByEmail(username).orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    } else {
      log.warn("Username passed as null for refresh token: {}", refreshToken);
    }
  }
}
