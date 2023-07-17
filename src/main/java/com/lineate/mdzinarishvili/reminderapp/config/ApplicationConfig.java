package com.lineate.mdzinarishvili.reminderapp.config;

import com.lineate.mdzinarishvili.reminderapp.dao.UserDaoImpl;
import com.lineate.mdzinarishvili.reminderapp.exceptions.InvalidInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfig {
  private final UserDaoImpl repository;

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      log.info("Selecting user by username {}", username);
      return repository.selectUserByUsername(username)
          .orElseThrow(() -> {
            log.error("Username not found {}", username);
            return new InvalidInputException(String.format("Username Not found %s", username));
          });
    };
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    log.info("Calling authentication provider");
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    log.info("Calling authentication manager");
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    log.info("Calling password encoder");
    return new BCryptPasswordEncoder();
  }

}
