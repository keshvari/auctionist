package com.example.auctionist.repository;

import com.example.auctionist.domain.AuctionItem;
import com.example.auctionist.domain.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionItemRepository extends JpaRepository<AuctionItem, Long> {
    Page<AuctionItem> findByStatus(ItemStatus itemStatus, Pageable pageable);
}
