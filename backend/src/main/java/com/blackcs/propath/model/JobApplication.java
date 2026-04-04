package com.blackcs.propath.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class JobApplication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String company;

  @Column(name = "role_title", nullable = false)
  private String roleTitle;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private ApplicationStatus status;

  @Column(name = "next_action_date", nullable = false)
  private LocalDateTime nextActionDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private ApplicationPriority priority;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public JobApplication() {}

  public JobApplication(
      User user,
      String company,
      String roleTitle,
      ApplicationStatus status,
      LocalDateTime nextActionDate,
      ApplicationPriority priority) {
    this.user = user;
    this.company = company;
    this.roleTitle = roleTitle;
    this.status = status;
    this.nextActionDate = nextActionDate;
    this.priority = priority;
  }

  @PrePersist
  void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @JsonIgnore
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Long getUserId() {
    return user == null ? null : user.getId();
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getRoleTitle() {
    return roleTitle;
  }

  public void setRoleTitle(String roleTitle) {
    this.roleTitle = roleTitle;
  }

  public ApplicationStatus getStatus() {
    return status;
  }

  public void setStatus(ApplicationStatus status) {
    this.status = status;
  }

  public LocalDateTime getNextActionDate() {
    return nextActionDate;
  }

  public void setNextActionDate(LocalDateTime nextActionDate) {
    this.nextActionDate = nextActionDate;
  }

  public ApplicationPriority getPriority() {
    return priority;
  }

  public void setPriority(ApplicationPriority priority) {
    this.priority = priority;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
