package com.example.auctionist.domain;

import jakarta.persistence.*;
import org.apache.logging.log4j.util.Lazy;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AuctionItem item;

    @Column(nullable = false, precision = 19,scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, length = 100)
    private String bidder;
    @Column(nullable = false)
    private Instant createdAt;


    public Bid(){}

    public Bid(AuctionItem item, BigDecimal amount, String bidder, Instant createdAt) {
        this.id = id;
        this.item = item;
        this.amount = amount;
        if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be > 0");
        this.bidder = bidder;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuctionItem getItem() {
        return item;
    }

    public void setItem(AuctionItem item) {
        this.item = item;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBidder() {
        return bidder;
    }

    public void setBidder(String bidder) {
        this.bidder = bidder;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
