package com.example.auctionist.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PlaceBidRequest(
        @Positive BigDecimal amount,
        @NotBlank String bidder
) {}
