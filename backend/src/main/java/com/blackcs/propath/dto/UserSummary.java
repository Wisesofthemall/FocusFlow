package com.blackcs.propath.dto;

import com.blackcs.propath.model.User;

public record UserSummary(Long id, String name, String email) {
  public static UserSummary from(User user) {
    return new UserSummary(user.getId(), user.getName(), user.getEmail());
  }
}
