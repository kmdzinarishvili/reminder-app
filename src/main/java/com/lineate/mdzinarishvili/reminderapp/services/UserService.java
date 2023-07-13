package com.lineate.mdzinarishvili.reminderapp.services;

import com.lineate.mdzinarishvili.reminderapp.dao.UserDao;
import com.lineate.mdzinarishvili.reminderapp.dao.UserDao;
import com.lineate.mdzinarishvili.reminderapp.dto.DeleteUserRequest;
import com.lineate.mdzinarishvili.reminderapp.dto.UsersRequest;
import com.lineate.mdzinarishvili.reminderapp.enums.UsersSortType;
import com.lineate.mdzinarishvili.reminderapp.exceptions.NotFoundException;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

  public List<User> getUsers(UsersRequest usersRequest) {
    UserDetails userDetails =
        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    log.info(
        "Get users request made by: {}", username);
    return userDao.selectUsers(usersRequest.getUsersSortType());
  }

  public User addNewUser(User user) {
    log.info("Add user request for user with username: {}", user.getUsername());
    return userDao.insertUser(user).orElseThrow();
  }

  public User updateUser(User user) {
    UserDetails userDetails =
        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    log.info("User: {} made update request for user with username: {}", username,
        user.getUsername());
    User result = userDao.updateUser(user).orElseThrow();
    return result;
  }

  public void deleteUser(Long id) {
    UserDetails userDetails =
        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
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
    UserDetails userDetails =
        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
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
}