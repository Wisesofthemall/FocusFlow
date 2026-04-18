package com.blackcs.propath.web;

import com.blackcs.propath.dto.UpdateUserRequest;
import com.blackcs.propath.dto.UserSummary;
import com.blackcs.propath.model.User;
import com.blackcs.propath.security.CurrentUserService;
import com.blackcs.propath.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final CurrentUserService currentUserService;

  public UserController(UserService userService, CurrentUserService currentUserService) {
    this.userService = userService;
    this.currentUserService = currentUserService;
  }

  @GetMapping("/me")
  public ResponseEntity<UserSummary> getMe() {
    return ResponseEntity.ok(UserSummary.from(currentUserService.requireCurrentUser()));
  }

  @PutMapping("/me")
  public ResponseEntity<UserSummary> updateMe(@Valid @RequestBody UpdateUserRequest body) {
    Long id = currentUserService.requireCurrentUserId();
    User updated = userService.update(id, body.name(), body.googleRefreshToken());
    return ResponseEntity.ok(UserSummary.from(updated));
  }

  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteMe() {
    userService.delete(currentUserService.requireCurrentUserId());
    return ResponseEntity.noContent().build();
  }
}
