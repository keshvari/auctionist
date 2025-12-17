package com.example.auctionist.service;

import com.example.auctionist.domain.AuctionItem;
import com.example.auctionist.domain.ItemStatus;
import com.example.auctionist.repository.AuctionItemRepository;
import com.example.auctionist.web.dto.CreateItemRequest;
import com.example.auctionist.web.dto.ItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.auctionist.support.NotFoundException;

@Service
public class AuctionItemService {
    private final AuctionItemRepository repo;

    public AuctionItemService(AuctionItemRepository repo){
        this.repo = repo;
    }

    public ItemResponse create(CreateItemRequest request){
        if (!request.endsAt().isAfter(request.startsAt()))
            throw new IllegalArgumentException("endsAt must be after startsAt");
        var item = new AuctionItem(request.title(),request.description(),request.reservePrice(),
                request.startsAt(),request.endsAt());
        var saved = repo.save(item);
        return new ItemResponse(saved.getId(),saved.getTitle(),saved.getStatus());
    }

    public ItemResponse get(Long id) {
        var it = repo.findById(id).orElseThrow(() -> new NotFoundException("Item " + id + " not found"));
        return new ItemResponse(it.getId(), it.getTitle(), it.getStatus());
    }

    public Page<ItemResponse> list(ItemStatus itemStatus, int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<AuctionItem> pageItems = (itemStatus == null)
                ? repo.findAll(pageable)              // returns Page<AuctionItem>
                : repo.findByStatus(itemStatus, pageable);// returns Page<AuctionItem>


        var a = (itemStatus == null)
                ? repo.findAll(pageable)              // returns Page<AuctionItem>
                : repo.findByStatus(itemStatus, pageable);

        return pageItems.map(it -> new ItemResponse(it.getId(), it.getTitle(), it.getStatus()));
    }

}
