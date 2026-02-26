package com.example.bankcards.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.bankcards.entity.Card;


import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findAll(Pageable pageable);

    Page<Card> findByOwnerUsername(String username, Pageable pageable);

    Optional<Card> findByIdAndOwnerUsername(Long id, String username);
}
