package com.blackcs.propath.web;

import com.blackcs.propath.dto.AuthResponse;
import com.blackcs.propath.dto.LoginRequest;
import com.blackcs.propath.dto.RegisterUserRequest;
import com.blackcs.propath.dto.UserSummary;
import com.blackcs.propath.model.User;
import com.blackcs.propath.security.JwtService;
import com.blackcs.propath.security.JwtService.IssuedToken;
import com.blackcs.propath.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthController(
      UserService userService,
      AuthenticationManager authenticationManager,
      JwtService jwtService) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterUserRequest req) {
    User user = userService.register(req.name(), req.email(), req.password());
    IssuedToken issued = jwtService.generateToken(user);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new AuthResponse(issued.token(), issued.expiresAt(), UserSummary.from(user)));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.email(), req.password()));
    User user =
        userService
            .findByEmail(req.email())
            .orElseThrow(() -> new IllegalStateException("User missing after auth"));
    IssuedToken issued = jwtService.generateToken(user);
    return ResponseEntity.ok(
        new AuthResponse(issued.token(), issued.expiresAt(), UserSummary.from(user)));
  }
}
