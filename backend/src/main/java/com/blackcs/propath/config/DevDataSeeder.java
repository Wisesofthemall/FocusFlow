package com.blackcs.propath.config;

import com.blackcs.propath.model.ApplicationPriority;
import com.blackcs.propath.model.ApplicationStatus;
import com.blackcs.propath.model.EventSource;
import com.blackcs.propath.model.User;
import com.blackcs.propath.repository.UserRepository;
import com.blackcs.propath.service.ApplicationService;
import com.blackcs.propath.service.CalendarEventService;
import com.blackcs.propath.service.UserService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DevDataSeeder implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

  private final UserRepository userRepository;
  private final UserService userService;
  private final ApplicationService applicationService;
  private final CalendarEventService calendarEventService;

  public DevDataSeeder(
      UserRepository userRepository,
      UserService userService,
      ApplicationService applicationService,
      CalendarEventService calendarEventService) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.applicationService = applicationService;
    this.calendarEventService = calendarEventService;
  }

  @Override
  public void run(String... args) {
    if (userRepository.count() > 0) {
      return;
    }
    log.info("Seeding demo data (empty database detected)");
    User demo = userService.register("Demo User", "demo@propath.local", "Passw0rd!");

    applicationService.create(
        demo.getId(),
        "Acme Corp",
        "Software Engineer Intern",
        ApplicationStatus.APPLIED,
        LocalDateTime.now().plusDays(3),
        ApplicationPriority.HIGH);
    applicationService.create(
        demo.getId(),
        "Globex",
        "Backend Engineer",
        ApplicationStatus.INTERVIEWING,
        LocalDateTime.now().plusDays(1),
        ApplicationPriority.MEDIUM);
    applicationService.create(
        demo.getId(),
        "Initech",
        "Full-Stack Developer",
        ApplicationStatus.APPLIED,
        LocalDateTime.now().plusDays(6),
        ApplicationPriority.LOW);

    calendarEventService.create(
        demo.getId(),
        "Technical phone screen — Globex",
        Instant.now().plus(1, ChronoUnit.DAYS),
        Instant.now().plus(1, ChronoUnit.DAYS).plus(45, ChronoUnit.MINUTES),
        EventSource.MANUAL,
        null);

    log.info("Seeded demo user demo@propath.local / Passw0rd! with 3 applications + 1 event");
  }
}
