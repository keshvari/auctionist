package com.example.auctionist.repository;

import com.example.auctionist.domain.AuctionItem;
import com.example.auctionist.domain.Bid;
import com.example.auctionist.web.dto.BidSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findTopByItemOrderByAmountDesc(AuctionItem item);

    @Query("""
    select new com.example.auctionist.web.dto.BidSummary(b.id, b.amount, b.bidder, b.createdAt)
    from Bid b
    where b.item.id = :itemId
    order by b.createdAt desc
  """)
    Page<BidSummary> findBidSummariesByItemId(@Param("itemId") Long itemId, Pageable pageable);
}
