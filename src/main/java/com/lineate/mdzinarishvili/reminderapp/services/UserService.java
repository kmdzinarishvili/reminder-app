package com.lineate.mdzinarishvili.reminderapp.services;

import com.lineate.mdzinarishvili.reminderapp.dao.UserDao;
import com.lineate.mdzinarishvili.reminderapp.dto.DeleteUserRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.UserRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.UserResponse;
import com.lineate.mdzinarishvili.reminderapp.dto.UsersResponse;
import com.lineate.mdzinarishvili.reminderapp.exceptions.NotFoundException;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
  private final UserDao userDao;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  public List<UsersResponse> getUsers(String sortType) {
    User user =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = user.getName();
    log.info(
        "Get users request made by: {}", username);
    return userDao.selectUsers(sortType).stream().map(UsersResponse::new).collect(Collectors.toList());
  }

  public User addNewUser(User user) {
    log.info("Add user request for user with username: {}", user.getName());
    return userDao.insertUser(user).orElseThrow();
  }

  public UserResponse updateUser(UserRequest userRequest) {
    User user =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = user.getName();
    System.out.println("user from security context"+user);
    log.info("User: {} made update request with data {}", username,
        userRequest);
    user.setName(userRequest.getUsername());
    user.setTimezoneOffsetHours(userRequest.getTimezoneOffsetHours());
    user.setDaysBeforeReminderDelete(userRequest.getDaysBeforeReminderDelete());
    User result = userDao.updateUser(user).orElseThrow();
    return new UserResponse(result);
  }

  public void deleteUser(Long id) {
    User authUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = authUser.getName();
    log.info("User: {} made delete request for user with id: {}", username, id);
    Optional<User> users = userDao.selectUserById(id);
    users.ifPresentOrElse(user -> {
      int result = userDao.deleteUserById(id);
      if (result != 1) {
        log.error("User's: {} delete request FAILED for user with id: {} ", username, id);
        throw new IllegalStateException("oops could not delete user");
      }
    }, () -> {
      log.error("User's: {} delete request FAILED for user with id: {} not found ", username, id);
      throw new NotFoundException(String.format("User with id %s not found", id));
    });
  }

  public boolean deleteUser(DeleteUserRequest deleteUserRequest) {
    User authUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = authUser.getName();
    log.info("User: {} made delete request for user with email: {} ",
        username,
        deleteUserRequest.getEmail());
    Optional<User> users = userDao.findByEmail(deleteUserRequest.getEmail());
    users.ifPresentOrElse(user -> {
      int result = userDao.deleteUserById(user.getId());
      if (result != 1) {
        log.error("User's: {} delete request FAILED for user with email: {}",
            username,
            deleteUserRequest.getEmail());
        throw new IllegalStateException("oops could not delete user");
      }
    }, () -> {
      log.error("User's: {} delete request FAILED for user with email: {} email not found",
          username,
          deleteUserRequest.getEmail());
      throw new NotFoundException(
          String.format("User with email %s not found", deleteUserRequest.getEmail()));
    });
    return true;
  }

  public User getUser(Long id) {
    return userDao.selectUserById(id)
        .orElseThrow(() -> {
          log.error("get user for user id: {} failed", id);
          return new NotFoundException(String.format("User with id %s not found", id));
        });
  }

  public UserResponse getLoggedInUserData() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "user service get getting logged in user data for: {}",
        user.getName());
    return new UserResponse(getUser(user.getId()));
  }

  public void deleteLoggedInUser() {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info(
        "user service get deleting logged in user data for: {}",
        user.getName());
    deleteUser(user.getId());
  }

  public List<UsersResponse> getUsersOrderByRegistration() {
    return getUsers("REGISTRATION_DATE");
  }
  public List<UsersResponse> getUsersOrderByUsername() {
    return getUsers("LOGIN");
  }

  public List<UsersResponse> getUsersOrderByLastActivity() {
    return getUsers("LAST_ACTIVITY");

  }
}