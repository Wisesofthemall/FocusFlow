package com.blackcs.propath.service;

import com.blackcs.propath.model.User;
import com.blackcs.propath.repository.UserRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public User register(String name, String email, String rawPassword) {
    String normalized = email.trim().toLowerCase();
    if (userRepository.existsByEmailIgnoreCase(normalized)) {
      throw new IllegalArgumentException("Email already registered");
    }
    User user = new User(name.trim(), normalized, passwordEncoder.encode(rawPassword), null);
    return userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmailIgnoreCase(email.trim().toLowerCase());
  }

  @Transactional
  public User update(Long id, String name, String googleRefreshToken) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    if (name != null && !name.isBlank()) {
      user.setName(name.trim());
    }
    if (googleRefreshToken != null) {
      user.setGoogleRefreshToken(googleRefreshToken.isBlank() ? null : googleRefreshToken);
    }
    return userRepository.save(user);
  }

  @Transactional
  public void delete(Long id) {
    if (!userRepository.existsById(id)) {
      throw new IllegalArgumentException("User not found");
    }
    userRepository.deleteById(id);
  }
}
