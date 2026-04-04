package com.blackcs.propath.dto;

import com.blackcs.propath.model.ApplicationPriority;
import com.blackcs.propath.model.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UpdateJobApplicationRequest(
    @NotBlank @Size(max = 200) String company,
    @NotBlank @Size(max = 200) String roleTitle,
    @NotNull ApplicationStatus status,
    @NotNull LocalDateTime nextActionDate,
    @NotNull ApplicationPriority priority) {}
