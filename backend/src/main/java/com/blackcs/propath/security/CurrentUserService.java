package com.blackcs.propath.security;

import com.blackcs.propath.model.User;
import com.blackcs.propath.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

  private final UserRepository userRepository;

  public CurrentUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User requireCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
      throw new IllegalStateException("No authenticated user in context");
    }
    return userRepository
        .findByEmailIgnoreCase(auth.getName())
        .orElseThrow(
            () -> new IllegalStateException("Authenticated user missing from repository"));
  }

  public Long requireCurrentUserId() {
    return requireCurrentUser().getId();
  }
}
