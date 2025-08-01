package com.residuesolution.pos.service;

import com.residuesolution.pos.dto.User;

import java.util.List;

public interface UserService {

    // Method for adding a user
    Boolean addUser(User user);

    // Method for getting all users
    List<User> getAllUsers();

    // Method for getting a user by ID
    User getUserById(Integer id);

    // Method for updating a user
    Boolean updateUser(Integer id, User user);

    // Method for deleting a user
    Boolean deleteUser(Integer id);
}
