package com.example.auctionist.concurrency;

import com.example.auctionist.domain.AuctionItem;
import com.example.auctionist.domain.ItemStatus;
import com.example.auctionist.repository.AuctionItemRepository;
import com.example.auctionist.service.BidService;
import com.example.auctionist.support.OptimisticRetry;
import com.example.auctionist.web.dto.PlaceBidRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({ BidService.class, OptimisticRetry.class, BidOptimisticLockTest.FixedClockConfig.class })
class BidOptimisticLockTest {

    @TestConfiguration
    static class FixedClockConfig {
        @Bean Clock clock() { return Clock.fixed(Instant.parse("2099-01-01T12:00:00Z"), ZoneOffset.UTC); }
    }

    @Autowired AuctionItemRepository items;
    @Autowired BidService bidService;

    @Test
    void twoConcurrentBids_oneWins_oneConflicts() throws Exception {
        var starts = Instant.parse("2099-01-01T00:00:00Z");
        var ends   = Instant.parse("2099-01-02T00:00:00Z");

        var item = new AuctionItem("Guitar", "Mint", new BigDecimal("10.00"), starts, ends);
        item.setStatus(ItemStatus.OPEN);
        item.setCurrentPrice(new BigDecimal("10.00"));
        Long itemId = items.saveAndFlush(item).getId();

        Callable<String> a = () -> tryBid(itemId, "ali", new BigDecimal("12.00"));
        Callable<String> b = () -> tryBid(itemId, "bob", new BigDecimal("12.00"));

        try (ExecutorService exec = Executors.newFixedThreadPool(2)) {
            // optional: CountDownLatch to release both at once
            var results = exec.invokeAll(List.of(a, b));
            var outcomes = results.stream().map(f -> {
                try { return f.get(5, TimeUnit.SECONDS); } catch (Exception e) { return "ERROR"; }
            }).toList();

            assertThat(outcomes).containsExactlyInAnyOrder("OK", "CONFLICT");
        }
    }

    private String tryBid(Long itemId, String who, BigDecimal amount) {
        try {
            // IMPORTANT: call the NON-retrying method so a conflict can surface
            bidService.placeBid(itemId, new PlaceBidRequest(amount, who));
            return "OK";
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            return "CONFLICT";
        } catch (Exception e) {
            return "ERROR:" + e.getClass().getSimpleName() + ":" + String.valueOf(e.getMessage());
        }
    }



}
