package com.lineate.mdzinarishvili.reminderapp.controllers;

import com.lineate.mdzinarishvili.reminderapp.dao.UserDaoImpl;
import com.lineate.mdzinarishvili.reminderapp.dto.DeleteUserRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.UsersRequest;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import com.lineate.mdzinarishvili.reminderapp.services.UserService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


//Admin role capabilities:
//    A user can:
//
//    View a list of users. Sort by login,
//    registration date,
//    or date of last activity
//    (creation or marking a reminder as completed).
//    Delete users.

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final UserService userService;

  public AdminController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public List<User> getUsers(@RequestBody UsersRequest usersRequest) {
    return userService.getUsers(usersRequest);
  }

  @DeleteMapping
  public boolean delete(@RequestBody DeleteUserRequest deleteUserRequest) {
    return userService.deleteUser(deleteUserRequest);
  }

}
