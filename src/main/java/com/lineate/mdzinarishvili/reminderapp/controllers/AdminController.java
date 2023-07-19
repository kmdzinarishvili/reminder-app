package com.lineate.mdzinarishvili.reminderapp.controllers;

import com.lineate.mdzinarishvili.reminderapp.dto.DeleteUserRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.UsersResponse;
import com.lineate.mdzinarishvili.reminderapp.services.UserService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final UserService userService;

  public AdminController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/username")
  public List<UsersResponse> getUsersByUsername() {
    return userService.getUsersOrderByUsername();
  }
  @GetMapping("/registration")
  public List<UsersResponse> getUsersByRegistrationDate() {
    return userService.getUsersOrderByRegistration();
  }
  @GetMapping("/activity")
  public List<UsersResponse> getUsersByLastActivity() {
    return userService.getUsersOrderByLastActivity();
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable("id") Long id) {
    userService.deleteUser(id);
  }

}
