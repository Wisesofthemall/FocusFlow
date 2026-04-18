package com.blackcs.propath.web;

import com.blackcs.propath.security.CurrentUserService;
import com.blackcs.propath.service.DashboardDataService;
import com.blackcs.propath.service.DashboardDataService.DashboardSlice;
import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final DashboardDataService dashboardDataService;
  private final CurrentUserService currentUserService;

  public DashboardController(
      DashboardDataService dashboardDataService, CurrentUserService currentUserService) {
    this.dashboardDataService = dashboardDataService;
    this.currentUserService = currentUserService;
  }

  @GetMapping
  public ResponseEntity<DashboardSlice> slice(
      @RequestParam LocalDateTime appWindowStart,
      @RequestParam LocalDateTime appWindowEnd,
      @RequestParam Instant eventWindowStart,
      @RequestParam Instant eventWindowEnd) {
    Long userId = currentUserService.requireCurrentUserId();
    return ResponseEntity.ok(
        dashboardDataService.sliceForUser(
            userId, appWindowStart, appWindowEnd, eventWindowStart, eventWindowEnd));
  }
}
