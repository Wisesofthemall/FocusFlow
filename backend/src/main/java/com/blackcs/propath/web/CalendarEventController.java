package com.blackcs.propath.web;

import com.blackcs.propath.dto.CreateCalendarEventRequest;
import com.blackcs.propath.dto.UpdateCalendarEventRequest;
import com.blackcs.propath.model.CalendarEvent;
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
@RequestMapping("/api")
public class CalendarEventController {

  private final CalendarEventService calendarEventService;

  public CalendarEventController(CalendarEventService calendarEventService) {
    this.calendarEventService = calendarEventService;
  }

  @PostMapping("/users/{userId}/calendar-events")
  public ResponseEntity<CalendarEvent> create(
      @PathVariable Long userId, @Valid @RequestBody CreateCalendarEventRequest body) {
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

  @GetMapping("/calendar-events/{id}")
  public ResponseEntity<CalendarEvent> getById(@PathVariable Long id) {
    return calendarEventService
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/users/{userId}/calendar-events/between")
  public ResponseEntity<List<CalendarEvent>> listBetween(
      @PathVariable Long userId,
      @RequestParam Instant start,
      @RequestParam Instant end) {
    return ResponseEntity.ok(
        calendarEventService.listForUserBetween(userId, start, end));
  }

  @PutMapping("/calendar-events/{id}")
  public ResponseEntity<CalendarEvent> update(
      @PathVariable Long id,
      @RequestParam Long userId,
      @Valid @RequestBody UpdateCalendarEventRequest body) {
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

  @DeleteMapping("/calendar-events/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
    calendarEventService.delete(id, userId);
    return ResponseEntity.noContent().build();
  }
}
