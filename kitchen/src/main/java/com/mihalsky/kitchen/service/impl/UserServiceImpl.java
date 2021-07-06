package com.mihalsky.kitchen.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.mihalsky.kitchen.constant.UserImplConstant.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import com.mihalsky.kitchen.domain.User;
import com.mihalsky.kitchen.domain.UserPrincipal;
import com.mihalsky.kitchen.enumeration.Role;
import com.mihalsky.kitchen.exception.domain.EmailExistException;
import com.mihalsky.kitchen.exception.domain.EmailNotFoundException;
import com.mihalsky.kitchen.exception.domain.NotAnImageFileException;
import com.mihalsky.kitchen.exception.domain.UserNotFoundException;
import com.mihalsky.kitchen.exception.domain.UsernameExistException;
import com.mihalsky.kitchen.repository.UserRepository;
import com.mihalsky.kitchen.service.EmailService;
import com.mihalsky.kitchen.service.LoginAttemptService;
import com.mihalsky.kitchen.service.UserService;
import static com.mihalsky.kitchen.constant.FileConstant.*;
import static com.mihalsky.kitchen.constant.FileConstant.NOT_AN_IMAGE_FILE;
import static com.mihalsky.kitchen.enumeration.Role.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.*;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService,UserDetailsService{
  private UserRepository userRepository;
  private BCryptPasswordEncoder passwordEncoder;
  private LoginAttemptService loginAttemptService;
  private EmailService emailService;

  @Autowired
  public UserServiceImpl(UserRepository userRepository,
                         BCryptPasswordEncoder passwordEncoder,
                         LoginAttemptService loginAttemptService,
                         EmailService emailService) {
      this.userRepository = userRepository;
      this.passwordEncoder = passwordEncoder;
      this.loginAttemptService = loginAttemptService;
      this.emailService = emailService;
  }
  
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findUserByUsername(username);
    if (user == null) {
        log.error(NO_USER_FOUND_BY_USERNAME + username);
        throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
    } else {
        validateLoginAttempt(user);
        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(new Date());
        userRepository.save(user);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        log.info(FOUND_USER_BY_USERNAME + username);
        return userPrincipal;
    }
  }

  private void validateLoginAttempt(User user) {
    if(user.isNotLocked()) {
      if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
          user.setNotLocked(false);
      } else {
          user.setNotLocked(true);
      }
  } else {
      loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
  }
 }

  @Override
  public User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
    validateNewUsernameAndEmail(EMPTY, username, email);
    User user = new User();
    user.setUserId(generateUserId());
    String password = generatePassword();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUsername(username);
    user.setEmail(email);
    user.setJoinDate(new Date());
    user.setPassword(encodePassword(password));
    user.setActive(true);
    user.setNotLocked(true);
    user.setRole(ROLE_USER.name());
    user.setAuthorities(ROLE_USER.getAuthorities());
    user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
    userRepository.save(user);
    //log.info("New user password: " + password);
    emailService.sendNewPasswordEmail(firstName, password, email);
    return user;
  }
  
  @Override
  public User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException, MessagingException {
      validateNewUsernameAndEmail(EMPTY, username, email);
      User user = new User();
      String password = generatePassword();
      user.setUserId(generateUserId());
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setJoinDate(new Date());
      user.setUsername(username);
      user.setEmail(email);
      user.setPassword(encodePassword(password));
      user.setActive(isActive);
      user.setNotLocked(isNonLocked);
      user.setRole(getRoleEnumName(role).name());
      user.setAuthorities(getRoleEnumName(role).getAuthorities());
      user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
      userRepository.save(user);
      saveProfileImage(user, profileImage);
      emailService.sendNewPasswordEmail(firstName, password, email);
      //log.info("New user password: " + password);
      return user;
  }

  private Role getRoleEnumName(String role) {
    return Role.valueOf(role.toUpperCase());
  }

  @Override
  public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
      User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
      currentUser.setFirstName(newFirstName);
      currentUser.setLastName(newLastName);
      currentUser.setUsername(newUsername);
      currentUser.setEmail(newEmail);
      currentUser.setActive(isActive);
      currentUser.setNotLocked(isNonLocked);
      currentUser.setRole(getRoleEnumName(role).name());
      currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
      userRepository.save(currentUser);
      saveProfileImage(currentUser, profileImage);
      return currentUser;
  }
  
  private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
    if (profileImage != null) {
      if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
          throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
      }
      Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
      if(!Files.exists(userFolder)) {
          Files.createDirectories(userFolder);
          log.info(DIRECTORY_CREATED + userFolder);
      }
      Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
      Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
      user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
      userRepository.save(user);
      log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
  }
  }

  private String setProfileImageUrl(String username) {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH
        + username + DOT + JPG_EXTENSION).toUriString();
  }

  @Override
  public void deleteUser(String username) throws IOException {
    User user = userRepository.findUserByUsername(username);
    Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
    FileUtils.deleteDirectory(new File(userFolder.toString()));
    userRepository.deleteById(user.getId());
  }

  @Override
  public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
    User user = userRepository.findUserByEmail(email);
    if (user == null) {
        throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
    }
    String password = generatePassword();
    user.setPassword(encodePassword(password));
    userRepository.save(user);
    //log.info("New user password: " + password);
    emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
  }

  @Override
  public User updateProfileImage(String username, MultipartFile profileImage)
      throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
    User user = validateNewUsernameAndEmail(username, null, null);
    saveProfileImage(user, profileImage);
    return user;
  }

  private String getTemporaryProfileImageUrl(String username) {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
  }

  private String encodePassword(String password) {
    return passwordEncoder.encode(password);
  }

  private String generatePassword() {
    return RandomStringUtils.randomAlphanumeric(10);
  }

  private String generateUserId() {
    return RandomStringUtils.randomNumeric(10);
  }

  @Override
  public List<User> getUsers() {
    return userRepository.findAll();
  }

  @Override
  public User findUserByUsername(String username) {
    return userRepository.findUserByUsername(username);
  }

  @Override
  public User findUserByEmail(String email) {
    return userRepository.findUserByEmail(email);
  }
  
  private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
    User userByNewUsername = findUserByUsername(newUsername);
    User userByNewEmail = findUserByEmail(newEmail);
    if(StringUtils.isNotBlank(currentUsername)) {
        User currentUser = findUserByUsername(currentUsername);
        if(currentUser == null) {
            throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
        }
        if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
            throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
        if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
            throw new EmailExistException(EMAIL_ALREADY_EXISTS);
        }
        return currentUser;
    } else {
        if(userByNewUsername != null) {
            throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
        if(userByNewEmail != null) {
            throw new EmailExistException(EMAIL_ALREADY_EXISTS);
        }
        return null;
    }
  }
}
