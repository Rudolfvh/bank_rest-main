package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardPageResponse;
import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.mapper.TransferMapper;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserCardController {

    private final CardService cardService;
    private final CardMapper cardMapper;
    private final TransferMapper transferMapper;

    @GetMapping
    public CardPageResponse getMyCards(
            Pageable pageable,
            Authentication authentication
    ) {

        String username = authentication.getName();

        Page<Card> page =
                cardService.getUserCards(username, pageable);

        CardPageResponse response = new CardPageResponse();
        response.setContent(
                page.getContent()
                        .stream()
                        .map(cardMapper::toResponse)
                        .toList()
        );
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());

        return response;
    }

    @GetMapping("/{id}")
    public CardResponse getMyCard(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();

        Card card = cardService.getUserCardById(id, username);

        return cardMapper.toResponse(card);
    }

    @PostMapping("/{id}/block-request")
    public void requestBlock(
            @PathVariable Long id,
            Authentication authentication
    ) {
        cardService.requestBlock(id, authentication.getName());
    }

    @PostMapping("/transfer")
    public TransferResponse transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        cardService.transfer(
                request.getFromCardId(),
                request.getToCardId(),
                request.getAmount(),
                username
        );

        return transferMapper.toResponse(
                request.getFromCardId(),
                request.getToCardId(),
                request.getAmount()
        );
    }
}