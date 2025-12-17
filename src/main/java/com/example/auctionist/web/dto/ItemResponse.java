package com.example.auctionist.web.dto;

import com.example.auctionist.domain.ItemStatus;

public record ItemResponse(Long id, String title, ItemStatus status) {}
