package com.example.auctionist.service;

import com.example.auctionist.repository.AuctionItemRepository;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Clock;
import java.time.Instant;

@Component
public class AuctionCloser {
    private static final Logger log = LoggerFactory.getLogger(AuctionCloser.class);
    private final AuctionItemRepository items;
    private final Clock clock;
    public AuctionCloser(AuctionItemRepository items, Clock clock) { this.items = items; this.clock = clock; }

    @Scheduled(fixedDelay = 60_000L) // every minute; for testing you can drop to 5000
    public void closeExpiredAuctions() {
        Instant now = Instant.now(clock);
        int updated = items.closeExpired(now);
        if (updated > 0) log.info("Closed {} expired auctions at {}", updated, now);
    }
}
