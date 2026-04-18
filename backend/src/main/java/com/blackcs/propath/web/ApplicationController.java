package com.blackcs.propath.web;

import com.blackcs.propath.dto.CreateJobApplicationRequest;
import com.blackcs.propath.dto.UpdateJobApplicationRequest;
import com.blackcs.propath.model.JobApplication;
import com.blackcs.propath.security.CurrentUserService;
import com.blackcs.propath.service.ApplicationService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

  private final ApplicationService applicationService;
  private final CurrentUserService currentUserService;

  public ApplicationController(
      ApplicationService applicationService, CurrentUserService currentUserService) {
    this.applicationService = applicationService;
    this.currentUserService = currentUserService;
  }

  @PostMapping
  public ResponseEntity<JobApplication> create(
      @Valid @RequestBody CreateJobApplicationRequest body) {
    Long userId = currentUserService.requireCurrentUserId();
    JobApplication created =
        applicationService.create(
            userId,
            body.company(),
            body.roleTitle(),
            body.status(),
            body.nextActionDate(),
            body.priority());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<JobApplication>> list() {
    Long userId = currentUserService.requireCurrentUserId();
    return ResponseEntity.ok(applicationService.listForUser(userId));
  }

  @GetMapping("/due-between")
  public ResponseEntity<List<JobApplication>> listDueBetween(
      @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
    Long userId = currentUserService.requireCurrentUserId();
    return ResponseEntity.ok(applicationService.listForUserDueBetween(userId, start, end));
  }

  @GetMapping("/{id}")
  public ResponseEntity<JobApplication> getById(@PathVariable Long id) {
    Long userId = currentUserService.requireCurrentUserId();
    JobApplication app =
        applicationService
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
    if (!app.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("Application does not belong to this user");
    }
    return ResponseEntity.ok(app);
  }

  @PutMapping("/{id}")
  public ResponseEntity<JobApplication> update(
      @PathVariable Long id, @Valid @RequestBody UpdateJobApplicationRequest body) {
    Long userId = currentUserService.requireCurrentUserId();
    return ResponseEntity.ok(
        applicationService.update(
            id,
            userId,
            body.company(),
            body.roleTitle(),
            body.status(),
            body.nextActionDate(),
            body.priority()));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    Long userId = currentUserService.requireCurrentUserId();
    applicationService.delete(id, userId);
    return ResponseEntity.noContent().build();
  }
}
