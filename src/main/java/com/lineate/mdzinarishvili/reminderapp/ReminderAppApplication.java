package com.lineate.mdzinarishvili.reminderapp;

import com.lineate.mdzinarishvili.reminderapp.dto.RegisterRequest;
import com.lineate.mdzinarishvili.reminderapp.services.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.lineate.mdzinarishvili.reminderapp.enums.RoleType.ADMIN;

@Slf4j
@SpringBootApplication
public class ReminderAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReminderAppApplication.class, args);
  }
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:3000");
      }
    };
  }

  @Bean
  public CommandLineRunner commandLineRunner(
      AuthenticationService service
  ) {
    return args -> {
      log.info("Running command line runner");
      log.info("Creating user Admin in command line runner");
      var admin = RegisterRequest.builder()
          .username("Admin")
          .email("admin@mail.com")
          .password("password")
          .role(ADMIN)
          .build();
      log.info("Creating user keti in command line runner");
      var other = RegisterRequest.builder()
          .username("keti")
          .email("keti@keti.com")
          .password("keti")
          .role(ADMIN)
          .build();
      service.registerOrUpdateAdmin(admin);
      service.registerOrUpdateAdmin(other);

    };
  }

}
