package com.blackcs.propath.repository;

import com.blackcs.propath.model.JobApplication;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

  List<JobApplication> findByUser_IdOrderByNextActionDateAsc(Long userId);

  List<JobApplication> findByUser_IdAndNextActionDateBetweenOrderByNextActionDateAsc(
      Long userId, LocalDateTime startInclusive, LocalDateTime endInclusive);
}
