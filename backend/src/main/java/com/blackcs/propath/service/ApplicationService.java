package com.blackcs.propath.service;

import com.blackcs.propath.model.ApplicationPriority;
import com.blackcs.propath.model.ApplicationStatus;
import com.blackcs.propath.model.JobApplication;
import com.blackcs.propath.model.User;
import com.blackcs.propath.repository.JobApplicationRepository;
import com.blackcs.propath.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService {

  private final JobApplicationRepository jobApplicationRepository;
  private final UserRepository userRepository;

  public ApplicationService(
      JobApplicationRepository jobApplicationRepository, UserRepository userRepository) {
    this.jobApplicationRepository = jobApplicationRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public JobApplication create(
      Long userId,
      String company,
      String roleTitle,
      ApplicationStatus status,
      LocalDateTime nextActionDate,
      ApplicationPriority priority) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    JobApplication app =
        new JobApplication(
            user,
            company.trim(),
            roleTitle.trim(),
            status,
            nextActionDate,
            priority);
    return jobApplicationRepository.save(app);
  }

  @Transactional(readOnly = true)
  public Optional<JobApplication> findById(Long id) {
    return jobApplicationRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<JobApplication> listForUser(Long userId) {
    return jobApplicationRepository.findByUser_IdOrderByNextActionDateAsc(userId);
  }

  @Transactional(readOnly = true)
  public List<JobApplication> listForUserDueBetween(
      Long userId, LocalDateTime startInclusive, LocalDateTime endInclusive) {
    return jobApplicationRepository.findByUser_IdAndNextActionDateBetweenOrderByNextActionDateAsc(
        userId, startInclusive, endInclusive);
  }

  @Transactional
  public JobApplication update(
      Long applicationId,
      Long actingUserId,
      String company,
      String roleTitle,
      ApplicationStatus status,
      LocalDateTime nextActionDate,
      ApplicationPriority priority) {
    JobApplication app =
        jobApplicationRepository
            .findById(applicationId)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
    if (!app.getUser().getId().equals(actingUserId)) {
      throw new IllegalArgumentException("Application does not belong to this user");
    }
    app.setCompany(company.trim());
    app.setRoleTitle(roleTitle.trim());
    app.setStatus(status);
    app.setNextActionDate(nextActionDate);
    app.setPriority(priority);
    return jobApplicationRepository.save(app);
  }

  @Transactional
  public void delete(Long applicationId, Long actingUserId) {
    JobApplication app =
        jobApplicationRepository
            .findById(applicationId)
            .orElseThrow(() -> new IllegalArgumentException("Application not found"));
    if (!app.getUser().getId().equals(actingUserId)) {
      throw new IllegalArgumentException("Application does not belong to this user");
    }
    jobApplicationRepository.delete(app);
  }
}
