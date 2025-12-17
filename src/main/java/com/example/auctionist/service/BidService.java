package com.example.auctionist.service;

import com.example.auctionist.domain.AuctionItem;
import com.example.auctionist.domain.Bid;
import com.example.auctionist.domain.ItemStatus;
import com.example.auctionist.repository.AuctionItemRepository;
import com.example.auctionist.repository.BidRepository;
import com.example.auctionist.support.NotFoundException;
import com.example.auctionist.web.dto.BidResponse;
import com.example.auctionist.web.dto.BidSummary;
import com.example.auctionist.web.dto.PlaceBidRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.Date;

@Service
public class BidService {
    private final AuctionItemRepository items;
    private final BidRepository bids;
    private final Clock clock;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BidService.class);

    public BidService(AuctionItemRepository items, BidRepository bids, Clock clock) {
        this.items = items; this.bids = bids; this.clock = clock;
    }

    @Transactional
    public BidResponse placeBid(Long itemId, PlaceBidRequest r) {
        // Load managed item inside the tx
        var item = items.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item " + itemId + " not found"));

        // Business rules


        var now = Instant.now(clock);
// allow startsAt ≤ now < endsAt
        if (now.isBefore(item.getStartsAt()) || !now.isBefore(item.getEndsAt())) {
            throw new IllegalArgumentException("Bidding window is closed");
        }

        var min = (item.getCurrentPrice() != null) ? item.getCurrentPrice() : item.getReservePrice();
        if (r.amount().compareTo(min) <= 0) {           // strictly GREATER
            throw new IllegalArgumentException("Bid must be greater than " + min);
        }

        if (r.amount().compareTo(min) <= 0) {
            throw new IllegalArgumentException("Bid must be greater than " + min);
        }

        // Persist bid
        var bid = new Bid(item, r.amount(), r.bidder(), now);
        bids.save(bid);

        // Update current price (optimistic lock via @Version on AuctionItem)
        item.setCurrentPrice(r.amount()); // dirty-checked; UPDATE on flush/commit

        return new BidResponse(bid.getId(), item.getId(), bid.getAmount(), bid.getBidder());
    }

    @Transactional
    public void openItem(Long id) {
        var item = items.findById(id).orElseThrow(() -> new NotFoundException("Item " + id + " not found"));

        if (item.getStatus() != ItemStatus.OPEN) {
            item.setStatus(ItemStatus.OPEN);
        }
        if (item.getCurrentPrice() == null) {
            item.setCurrentPrice(item.getReservePrice()); // first bid must be > reserve
        }
    }

    public Page<BidSummary> listBids(Long itemId, int page, int size) {
        if (!items.existsById(itemId)) {
            throw new com.example.auctionist.support.NotFoundException("Item " + itemId + " not found");
        }
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return bids.findBidSummariesByItemId(itemId, pageable);
    }
}
