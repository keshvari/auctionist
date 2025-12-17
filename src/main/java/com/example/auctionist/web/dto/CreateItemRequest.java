package com.example.auctionist.web.dto;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;


public record CreateItemRequest(
        @NotBlank String title,
        @Size(max = 500) String description,
        @Positive BigDecimal reservePrice,
        @Future Instant startsAt,
        @Future Instant endsAt
) {
    @AssertTrue(message = "endsAt must be after startsAt")
    public boolean isEndAfterStart() {
        return startsAt != null && endsAt != null && endsAt.isAfter(startsAt);
    }
}