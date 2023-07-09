package com.lineate.mdzinarishvili.reminderapp.services;

import com.lineate.mdzinarishvili.reminderapp.dao.UserDao;
import com.lineate.mdzinarishvili.reminderapp.dao.UserDao;
import com.lineate.mdzinarishvili.reminderapp.dto.UsersRequest;
import com.lineate.mdzinarishvili.reminderapp.enums.UsersSortType;
import com.lineate.mdzinarishvili.reminderapp.exceptions.NotFoundException;
import com.lineate.mdzinarishvili.reminderapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
  private final UserDao userDao;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  public List<User> getUsers(UsersRequest usersRequest) {
    System.out.println("USERS REQUWST" + usersRequest);
    return userDao.selectUsers(usersRequest.getUsersSortType());
  }

  public User addNewUser(User user) {
    return userDao.insertUser(user).orElseThrow();
  }

  public User updateUser(User user) {
    User result = userDao.updateUser(user).orElseThrow();
    return result;
  }

  public void deleteUser(Long id) {
    Optional<User> movies = userDao.selectUserById(id);
    movies.ifPresentOrElse(movie -> {
      int result = userDao.deleteUserById(id);
      if (result != 1) {
        throw new IllegalStateException("oops could not delete user");
      }
    }, () -> {
      throw new NotFoundException(String.format("User with id %s not found", id));
    });
  }

  public User getUser(Long id) {
    return userDao.selectUserById(id)
        .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", id)));
  }
}