package com.blackcs.propath.web;

import com.blackcs.propath.service.DashboardDataService;
import com.blackcs.propath.service.DashboardDataService.DashboardSlice;
import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{userId}/dashboard")
public class DashboardController {

  private final DashboardDataService dashboardDataService;

  public DashboardController(DashboardDataService dashboardDataService) {
    this.dashboardDataService = dashboardDataService;
  }

  @GetMapping
  public ResponseEntity<DashboardSlice> slice(
      @PathVariable Long userId,
      @RequestParam LocalDateTime appWindowStart,
      @RequestParam LocalDateTime appWindowEnd,
      @RequestParam Instant eventWindowStart,
      @RequestParam Instant eventWindowEnd) {
    return ResponseEntity.ok(
        dashboardDataService.sliceForUser(
            userId, appWindowStart, appWindowEnd, eventWindowStart, eventWindowEnd));
  }
}
