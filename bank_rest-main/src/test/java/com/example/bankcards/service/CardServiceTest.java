package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.InvalidCardStateException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    @DisplayName("transfer should move money between cards when everything is valid")
    void transfer_success() {
        String username = "user";
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal amount = BigDecimal.TEN;

        Card from = new Card();
        from.setId(fromId);
        from.setStatus(CardStatus.ACTIVE);
        from.setExpirationDate(LocalDate.now().plusYears(1));
        from.setBalance(BigDecimal.valueOf(100));

        Card to = new Card();
        to.setId(toId);
        to.setStatus(CardStatus.ACTIVE);
        to.setExpirationDate(LocalDate.now().plusYears(1));
        to.setBalance(BigDecimal.ZERO);

        when(cardRepository.findByIdAndOwnerUsername(fromId, username)).thenReturn(Optional.of(from));
        when(cardRepository.findByIdAndOwnerUsername(toId, username)).thenReturn(Optional.of(to));

        cardService.transfer(fromId, toId, amount, username);

        assertEquals(BigDecimal.valueOf(90), from.getBalance());
        assertEquals(BigDecimal.TEN, to.getBalance());
    }

    @Test
    @DisplayName("transfer should throw when amount is not positive")
    void transfer_invalidAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> cardService.transfer(1L, 2L, BigDecimal.ZERO, "user"));
    }

    @Test
    @DisplayName("transfer should throw when card not found")
    void transfer_cardNotFound() {
        when(cardRepository.findByIdAndOwnerUsername(1L, "user")).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> cardService.transfer(1L, 2L, BigDecimal.TEN, "user"));
    }

    @Test
    @DisplayName("transfer should throw when card is expired")
    void transfer_expiredCard() {

        Card from = new Card();
        from.setId(1L);
        from.setStatus(CardStatus.ACTIVE);
        from.setExpirationDate(LocalDate.now().minusDays(1)); // ПРОСРОЧЕНА
        from.setBalance(BigDecimal.valueOf(100));

        Card to = new Card();
        to.setId(2L);
        to.setStatus(CardStatus.ACTIVE);
        to.setExpirationDate(LocalDate.now().plusYears(1));
        to.setBalance(BigDecimal.ZERO);

        when(cardRepository.findByIdAndOwnerUsername(1L, "user"))
                .thenReturn(Optional.of(from));

        when(cardRepository.findByIdAndOwnerUsername(2L, "user"))
                .thenReturn(Optional.of(to));

        assertThrows(InvalidCardStateException.class,
                () -> cardService.transfer(1L, 2L, BigDecimal.TEN, "user"));
    }

    @Test
    @DisplayName("transfer should throw when balance is insufficient")
    void transfer_insufficientFunds() {
        Card from = new Card();
        from.setId(1L);
        from.setStatus(CardStatus.ACTIVE);
        from.setExpirationDate(LocalDate.now().plusYears(1));
        from.setBalance(BigDecimal.ONE);

        Card to = new Card();
        to.setId(2L);
        to.setStatus(CardStatus.ACTIVE);
        to.setExpirationDate(LocalDate.now().plusYears(1));
        to.setBalance(BigDecimal.ZERO);

        when(cardRepository.findByIdAndOwnerUsername(1L, "user")).thenReturn(Optional.of(from));
        when(cardRepository.findByIdAndOwnerUsername(2L, "user")).thenReturn(Optional.of(to));

        assertThrows(InsufficientFundsException.class,
                () -> cardService.transfer(1L, 2L, BigDecimal.TEN, "user"));
    }

    @Test
    @DisplayName("requestBlock should set status to BLOCKED for user card")
    void requestBlock_success() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findByIdAndOwnerUsername(1L, "user"))
                .thenReturn(Optional.of(card));

        cardService.requestBlock(1L, "user");

        assertEquals(CardStatus.BLOCKED, card.getStatus());
    }

    @Test
    @DisplayName("requestBlock should throw when card already blocked")
    void requestBlock_alreadyBlocked() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findByIdAndOwnerUsername(1L, "user"))
                .thenReturn(Optional.of(card));

        assertThrows(InvalidCardStateException.class,
                () -> cardService.requestBlock(1L, "user"));
    }

    @Test
    @DisplayName("deleteCard should delete when exists")
    void deleteCard_success() {
        when(cardRepository.existsById(1L)).thenReturn(true);

        cardService.deleteCard(1L);

        verify(cardRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteCard should throw when not found")
    void deleteCard_notFound() {
        when(cardRepository.existsById(1L)).thenReturn(false);

        assertThrows(CardNotFoundException.class,
                () -> cardService.deleteCard(1L));
    }
}

