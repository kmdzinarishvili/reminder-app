package com.lineate.mdzinarishvili.reminderapp.dao;

import com.lineate.mdzinarishvili.reminderapp.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
  List<User> selectUsers(String sortType);

  Optional<User> selectUserById(Long id);

  Optional<User> selectUserByUsername(String username);

  Optional<User> updateUser(User user);

  int deleteUserById(Long id);

  Optional<User> insertUser(User user);
}
