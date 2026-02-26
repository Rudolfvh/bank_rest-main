package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardPageResponse;
import com.example.bankcards.dto.card.CardResponse;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final CardService cardService;
    private final CardMapper cardMapper;

    @PostMapping
    public CardResponse create(@Valid @RequestBody CreateCardRequest request) {
        return cardMapper.toResponse(cardService.createCard(request));
    }

    @PutMapping("/{id}/activate")
    public CardResponse activate(@PathVariable Long id) {
        return cardMapper.toResponse(
                cardService.changeStatus(id, CardStatus.ACTIVE));
    }

    @PutMapping("/{id}/block")
    public CardResponse block(@PathVariable Long id) {
        return cardMapper.toResponse(
                cardService.changeStatus(id, CardStatus.BLOCKED));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    @GetMapping
    public CardPageResponse getAll(Pageable pageable) {
        Page<Card> page = cardService.getAll(pageable);

        CardPageResponse response = new CardPageResponse();
        response.setContent(
                page.getContent().stream()
                        .map(cardMapper::toResponse)
                        .toList()
        );
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());

        return response;
    }
}
