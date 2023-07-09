package com.lineate.mdzinarishvili.reminderapp;

import com.lineate.mdzinarishvili.reminderapp.dto.RegisterRequest;
import com.lineate.mdzinarishvili.reminderapp.services.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.lineate.mdzinarishvili.reminderapp.enums.RoleType.ADMIN;

@SpringBootApplication
public class ReminderAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReminderAppApplication.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner(
      AuthenticationService service
  ) {
    return args -> {
      var admin = RegisterRequest.builder()
          .username("Admin")
          .email("admin@mail.com")
          .password("password")
          .role(ADMIN)
          .build();
      System.out.println("Admin token: " + service.registerOrUpdateAdmin(admin).getAccessToken());

    };
  }

}
