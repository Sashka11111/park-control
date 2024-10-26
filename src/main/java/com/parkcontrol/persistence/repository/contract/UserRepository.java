package com.parkcontrol.persistence.repository.contract;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.persistence.entity.User;
import com.parkcontrol.persistence.entity.UserRole;
import java.util.List;

public interface UserRepository {
  List<User> findAll();
  void addUser(User user);
  User findByUsername(String username) throws EntityNotFoundException;
  boolean isUsernameExists(String username);
  void updateUserRole(String username, UserRole newRole) throws EntityNotFoundException;
  void deleteUser(String username) throws EntityNotFoundException;
}
