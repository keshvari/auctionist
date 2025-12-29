package com.example.auctionist.web;

import com.example.auctionist.domain.ItemStatus;
import com.example.auctionist.service.AuctionItemService;
import com.example.auctionist.service.BidService;
import com.example.auctionist.support.NotFoundException;
import com.example.auctionist.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/api/items")
public class ItemsController {

    private final AuctionItemService auctionItemService;
    private final BidService bids;

    public ItemsController(AuctionItemService service, BidService bidService){
        auctionItemService = service;
        bids = bidService;
    }


    @PostMapping
    public ResponseEntity<ItemResponse> create(@Valid @RequestBody CreateItemRequest req){
        var res = auctionItemService.create(req);
        var builder = org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest();
        builder.path("/{id}");
        var location = builder.buildAndExpand(res.id()).toUri();
        return org.springframework.http.ResponseEntity.created(location).body(res);
    }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable Long id) {
        return auctionItemService.get(id);
    }

    @GetMapping
    public Page<ItemResponse> list(@RequestParam(required = false) ItemStatus status,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return auctionItemService.list(status, page, size);
    }


    // --- new: open item for bidding
    @PatchMapping("/{id}/open")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void open(@PathVariable Long id) { bids.openItem(id); }


    @PostMapping("/{id}/bids")
    public ResponseEntity<BidResponse> bid(@PathVariable Long id,
                                           @jakarta.validation.Valid @RequestBody PlaceBidRequest req) {
        var res = bids.placeBidWithRetry(id, req);   // <-- uses bounded backoff (e.g., 3 tries)
        var loc = org.springframework.web.servlet.support.ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{bidId}").buildAndExpand(res.id()).toUri();
        return org.springframework.http.ResponseEntity.created(loc).body(res);
    }


    @GetMapping("/{id}/bids")
    public Page<BidSummary> bids(@PathVariable Long id,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return bids.listBids(id, page, size);
    }

//    @GetMapping("/ping")
//    public String ping() { return "pong"; }
//
//    @GetMapping("/boom")
//    public void boom(){
//        throw new NotFoundException("Example not found");
//    }
}