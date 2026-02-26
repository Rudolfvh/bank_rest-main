package com.example.bankcards.service;

import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardMaskUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public Card createCard(CreateCardRequest request) {

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(UserNotFoundException::new);

        Card card = new Card();
        card.setNumber(CardMaskUtil.mask(request.getNumber()));
        card.setHolderName(request.getHolderName());
        card.setExpirationDate(request.getExpirationDate());
        card.setBalance(request.getBalance());
        card.setStatus(CardStatus.ACTIVE);
        card.setOwner(owner);

        return cardRepository.save(card);
    }

    public Card changeStatus(Long id, CardStatus status) {

        Card card = cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);

        if (card.isExpired()) {
            throw new InvalidCardStateException("Card is expired");
        }

        card.setStatus(status);
        return card;
    }

    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException();
        }
        cardRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Card> getAll(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Card> getUserCards(String username, Pageable pageable) {
        return cardRepository.findByOwnerUsername(username, pageable);
    }

    @Transactional(readOnly = true)
    public Card getUserCardById(Long id, String username) {

        return cardRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(CardNotFoundException::new);
    }

    public void requestBlock(Long cardId, String username) {

        Card card = cardRepository.findByIdAndOwnerUsername(cardId, username)
                .orElseThrow(CardNotFoundException::new);

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new InvalidCardStateException("Card already blocked");
        }

        card.setStatus(CardStatus.BLOCKED);
    }

    public void transfer(
            Long fromId,
            Long toId,
            BigDecimal amount,
            String username
    ) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Card from = cardRepository.findByIdAndOwnerUsername(fromId, username)
                .orElseThrow(CardNotFoundException::new);

        Card to = cardRepository.findByIdAndOwnerUsername(toId, username)
                .orElseThrow(CardNotFoundException::new);

        validateCardActive(from);
        validateCardActive(to);

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
    }

    private void validateCardActive(Card card) {

        if (card.isExpired()) {
            card.setStatus(CardStatus.EXPIRED);
            throw new InvalidCardStateException("Card expired");
        }

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardStateException("Card not active");
        }
    }
}