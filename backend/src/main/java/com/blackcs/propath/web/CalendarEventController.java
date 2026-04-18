package com.blackcs.propath.web;

import com.blackcs.propath.dto.CreateCalendarEventRequest;
import com.blackcs.propath.dto.UpdateCalendarEventRequest;
import com.blackcs.propath.model.CalendarEvent;
import com.blackcs.propath.security.CurrentUserService;
import com.blackcs.propath.service.CalendarEventService;
import jakarta.validation.Valid;
import java.time.Instant;
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
@RequestMapping("/api/calendar-events")
public class CalendarEventController {

  private final CalendarEventService calendarEventService;
  private final CurrentUserService currentUserService;

  public CalendarEventController(
      CalendarEventService calendarEventService, CurrentUserService currentUserService) {
    this.calendarEventService = calendarEventService;
    this.currentUserService = currentUserService;
  }

  @PostMapping
  public ResponseEntity<CalendarEvent> create(
      @Valid @RequestBody CreateCalendarEventRequest body) {
    Long userId = currentUserService.requireCurrentUserId();
    CalendarEvent created =
        calendarEventService.create(
            userId,
            body.title(),
            body.startsAt(),
            body.endsAt(),
            body.source(),
            body.externalId());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping("/between")
  public ResponseEntity<List<CalendarEvent>> listBetween(
      @RequestParam Instant start, @RequestParam Instant end) {
    Long userId = currentUserService.requireCurrentUserId();
    return ResponseEntity.ok(calendarEventService.listForUserBetween(userId, start, end));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CalendarEvent> getById(@PathVariable Long id) {
    Long userId = currentUserService.requireCurrentUserId();
    CalendarEvent event =
        calendarEventService
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    if (!event.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("Event does not belong to this user");
    }
    return ResponseEntity.ok(event);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CalendarEvent> update(
      @PathVariable Long id, @Valid @RequestBody UpdateCalendarEventRequest body) {
    Long userId = currentUserService.requireCurrentUserId();
    return ResponseEntity.ok(
        calendarEventService.update(
            id,
            userId,
            body.title(),
            body.startsAt(),
            body.endsAt(),
            body.source(),
            body.externalId()));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    Long userId = currentUserService.requireCurrentUserId();
    calendarEventService.delete(id, userId);
    return ResponseEntity.noContent().build();
  }
}
