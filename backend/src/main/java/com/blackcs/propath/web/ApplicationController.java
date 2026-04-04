package com.blackcs.propath.web;

import com.blackcs.propath.dto.CreateJobApplicationRequest;
import com.blackcs.propath.dto.UpdateJobApplicationRequest;
import com.blackcs.propath.model.JobApplication;
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
@RequestMapping("/api")
public class ApplicationController {

  private final ApplicationService applicationService;

  public ApplicationController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @PostMapping("/users/{userId}/applications")
  public ResponseEntity<JobApplication> create(
      @PathVariable Long userId, @Valid @RequestBody CreateJobApplicationRequest body) {
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

  @GetMapping("/applications/{id}")
  public ResponseEntity<JobApplication> getById(@PathVariable Long id) {
    return applicationService
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/users/{userId}/applications")
  public ResponseEntity<List<JobApplication>> listForUser(@PathVariable Long userId) {
    return ResponseEntity.ok(applicationService.listForUser(userId));
  }

  @GetMapping("/users/{userId}/applications/due-between")
  public ResponseEntity<List<JobApplication>> listDueBetween(
      @PathVariable Long userId,
      @RequestParam LocalDateTime start,
      @RequestParam LocalDateTime end) {
    return ResponseEntity.ok(
        applicationService.listForUserDueBetween(userId, start, end));
  }

  @PutMapping("/applications/{id}")
  public ResponseEntity<JobApplication> update(
      @PathVariable Long id,
      @RequestParam Long userId,
      @Valid @RequestBody UpdateJobApplicationRequest body) {
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

  @DeleteMapping("/applications/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
    applicationService.delete(id, userId);
    return ResponseEntity.noContent().build();
  }
}
