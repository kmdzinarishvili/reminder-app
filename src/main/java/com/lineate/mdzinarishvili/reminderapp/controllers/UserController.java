package com.lineate.mdzinarishvili.reminderapp.controllers;

import com.lineate.mdzinarishvili.reminderapp.dto.UserResponse;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import com.lineate.mdzinarishvili.reminderapp.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/user")
@PreAuthorize("hasRole('USER')")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public UserResponse getLoggedInUserData() {
    return userService.getLoggedInUserData();
  }

//  @DeleteMapping("/")
//  public User deleteLoggedInUser() {
//    return userService.getLoggedInUserData();
//  }

  @PostMapping("/update")
  public User updateLoggedInUser(@RequestBody User user) { // maybe add user dto
    return userService.updateUser(user);
  }

}
