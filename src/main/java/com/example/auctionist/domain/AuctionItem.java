package com.example.auctionist.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class AuctionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal reservePrice;

    @Column(precision =19, scale= 2)
    private BigDecimal currentPrice;

    @Column(nullable = false)
    private Instant startsAt;

    @Column(nullable = false)
    private Instant endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status = ItemStatus.DRAFT;

    @Version
    private Long version;

    protected AuctionItem() {} // JPA needs this

    public AuctionItem(String title, String description,BigDecimal reservePrice, Instant startsAt, Instant endsAt){
        this.title = title;
        this.description = description;
        this.reservePrice = reservePrice;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.status = ItemStatus.DRAFT;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getReservePrice() { return reservePrice; }
    public void setReservePrice(BigDecimal reservePrice) { this.reservePrice = reservePrice; }
    public Instant getStartsAt() { return startsAt; }
    public void setStartsAt(Instant startsAt) { this.startsAt = startsAt; }
    public Instant getEndsAt() { return endsAt; }
    public void setEndsAt(Instant endsAt) { this.endsAt = endsAt; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }
    public Long getVersion() { return version; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
}