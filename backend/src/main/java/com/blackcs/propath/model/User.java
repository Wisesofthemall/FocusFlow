package com.blackcs.propath.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Account for a ProPath user. Job applications and calendar events reference this entity via
 * {@link JobApplication#getUser()} and {@link CalendarEvent#getUser()} (many-to-one).
 */
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "google_refresh_token")
  private String googleRefreshToken;

  public User() {}

  public User(String name, String email, String passwordHash, String googleRefreshToken) {
    this.name = name;
    this.email = email;
    this.passwordHash = passwordHash;
    this.googleRefreshToken = googleRefreshToken;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @JsonIgnore
  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  @JsonIgnore
  public String getGoogleRefreshToken() {
    return googleRefreshToken;
  }

  public void setGoogleRefreshToken(String googleRefreshToken) {
    this.googleRefreshToken = googleRefreshToken;
  }
}
