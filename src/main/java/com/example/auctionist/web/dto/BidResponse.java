package com.example.auctionist.web.dto;

import java.math.BigDecimal;

public record BidResponse(Long id, Long itemId, BigDecimal amount, String bidder) {}
