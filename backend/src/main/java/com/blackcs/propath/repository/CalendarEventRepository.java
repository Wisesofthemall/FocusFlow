package com.blackcs.propath.repository;

import com.blackcs.propath.model.CalendarEvent;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

  List<CalendarEvent> findByUser_IdAndStartsAtBetweenOrderByStartsAtAsc(
      Long userId, Instant startInclusive, Instant endInclusive);
}
