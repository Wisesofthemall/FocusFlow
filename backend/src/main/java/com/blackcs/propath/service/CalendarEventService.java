package com.blackcs.propath.service;

import com.blackcs.propath.model.CalendarEvent;
import com.blackcs.propath.model.EventSource;
import com.blackcs.propath.model.User;
import com.blackcs.propath.repository.CalendarEventRepository;
import com.blackcs.propath.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalendarEventService {

  private final CalendarEventRepository calendarEventRepository;
  private final UserRepository userRepository;

  public CalendarEventService(
      CalendarEventRepository calendarEventRepository, UserRepository userRepository) {
    this.calendarEventRepository = calendarEventRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public CalendarEvent create(
      Long userId,
      String title,
      Instant startsAt,
      Instant endsAt,
      EventSource source,
      String externalId) {
    if (!endsAt.isAfter(startsAt)) {
      throw new IllegalArgumentException("Event end must be after start");
    }
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    CalendarEvent event =
        new CalendarEvent(user, title.trim(), startsAt, endsAt, source, externalId);
    return calendarEventRepository.save(event);
  }

  @Transactional(readOnly = true)
  public Optional<CalendarEvent> findById(Long id) {
    return calendarEventRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<CalendarEvent> listForUserBetween(
      Long userId, Instant startInclusive, Instant endInclusive) {
    return calendarEventRepository.findByUser_IdAndStartsAtBetweenOrderByStartsAtAsc(
        userId, startInclusive, endInclusive);
  }

  @Transactional
  public CalendarEvent update(
      Long eventId,
      Long actingUserId,
      String title,
      Instant startsAt,
      Instant endsAt,
      EventSource source,
      String externalId) {
    if (!endsAt.isAfter(startsAt)) {
      throw new IllegalArgumentException("Event end must be after start");
    }
    CalendarEvent event =
        calendarEventRepository
            .findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    if (!event.getUser().getId().equals(actingUserId)) {
      throw new IllegalArgumentException("Event does not belong to this user");
    }
    event.setTitle(title.trim());
    event.setStartsAt(startsAt);
    event.setEndsAt(endsAt);
    event.setSource(source);
    event.setExternalId(externalId == null || externalId.isBlank() ? null : externalId);
    return calendarEventRepository.save(event);
  }

  @Transactional
  public void delete(Long eventId, Long actingUserId) {
    CalendarEvent event =
        calendarEventRepository
            .findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    if (!event.getUser().getId().equals(actingUserId)) {
      throw new IllegalArgumentException("Event does not belong to this user");
    }
    calendarEventRepository.delete(event);
  }
}
