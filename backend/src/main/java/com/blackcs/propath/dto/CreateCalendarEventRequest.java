package com.blackcs.propath.dto;

import com.blackcs.propath.model.EventSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record CreateCalendarEventRequest(
    @NotBlank @Size(max = 500) String title,
    @NotNull Instant startsAt,
    @NotNull Instant endsAt,
    @NotNull EventSource source,
    @Size(max = 512) String externalId) {}
