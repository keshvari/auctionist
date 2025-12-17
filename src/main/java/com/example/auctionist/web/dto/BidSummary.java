package com.example.auctionist.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record BidSummary(Long id, BigDecimal amount, String bidder, Instant createdAt) {}
