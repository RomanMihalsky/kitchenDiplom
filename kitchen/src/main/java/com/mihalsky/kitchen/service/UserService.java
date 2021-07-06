package com.mihalsky.kitchen.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import com.mihalsky.kitchen.domain.User;
import com.mihalsky.kitchen.exception.domain.EmailExistException;
import com.mihalsky.kitchen.exception.domain.EmailNotFoundException;
import com.mihalsky.kitchen.exception.domain.NotAnImageFileException;
import com.mihalsky.kitchen.exception.domain.UserNotFoundException;
import com.mihalsky.kitchen.exception.domain.UsernameExistException;

public interface UserService {
  User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;
  
  List<User> getUsers();
  
  User findUserByUsername(String username);
  
  User findUserByEmail(String email);
  
  User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException, MessagingException;

  User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

  void deleteUser(String username) throws IOException;

  void resetPassword(String email) throws MessagingException, EmailNotFoundException;

  User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;
}

