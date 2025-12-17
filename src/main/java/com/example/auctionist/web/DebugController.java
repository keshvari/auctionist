package com.example.auctionist.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
class DebugBidController {
    private final java.time.Clock clock;
    private final com.example.auctionist.repository.AuctionItemRepository items;

    DebugBidController(java.time.Clock clock, com.example.auctionist.repository.AuctionItemRepository items) {
        this.clock = clock; this.items = items;
    }

    @GetMapping("/items/{id}")
    public java.util.Map<String,Object> inspect(@PathVariable Long id) {
        var it = items.findById(id).orElseThrow();
        var now = java.time.Instant.now(clock);
        var min = (it.getCurrentPrice() != null) ? it.getCurrentPrice() : it.getReservePrice();
        return java.util.Map.of(
                "now", now.toString(),
                "startsAt", it.getStartsAt().toString(),
                "endsAt", it.getEndsAt().toString(),
                "status", it.getStatus().name(),
                "reservePrice", it.getReservePrice(),
                "currentPrice", it.getCurrentPrice(),
                "minRequired", min
        );
    }
}
