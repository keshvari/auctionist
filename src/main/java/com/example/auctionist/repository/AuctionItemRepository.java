package com.example.auctionist.repository;

import com.example.auctionist.domain.AuctionItem;
import com.example.auctionist.domain.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AuctionItemRepository extends JpaRepository<AuctionItem, Long> {
    Page<AuctionItem> findByStatus(ItemStatus itemStatus, Pageable pageable);

    @Modifying
    @Transactional
    @Query("""
   update AuctionItem i
     set i.status = com.example.auctionist.domain.ItemStatus.CLOSED
   where i.status = com.example.auctionist.domain.ItemStatus.OPEN
     and i.endsAt <= :now
""")
    int closeExpired(java.time.Instant now);
}
