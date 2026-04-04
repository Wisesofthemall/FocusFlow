package com.blackcs.propath.service;

import com.blackcs.propath.model.CalendarEvent;
import com.blackcs.propath.model.JobApplication;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates reads for a future "today / this week" dashboard (applications + calendar
 * context). Persistence for calendar may later mix live Google API data with cached rows.
 */
@Service
public class DashboardDataService {

  private final ApplicationService applicationService;
  private final CalendarEventService calendarEventService;

  public DashboardDataService(
      ApplicationService applicationService, CalendarEventService calendarEventService) {
    this.applicationService = applicationService;
    this.calendarEventService = calendarEventService;
  }

  public record DashboardSlice(
      List<JobApplication> applicationsDueInRange, List<CalendarEvent> eventsInRange) {}

  @Transactional(readOnly = true)
  public DashboardSlice sliceForUser(
      Long userId,
      LocalDateTime applicationWindowStart,
      LocalDateTime applicationWindowEnd,
      Instant eventWindowStart,
      Instant eventWindowEnd) {
    return new DashboardSlice(
        applicationService.listForUserDueBetween(
            userId, applicationWindowStart, applicationWindowEnd),
        calendarEventService.listForUserBetween(userId, eventWindowStart, eventWindowEnd));
  }
}
