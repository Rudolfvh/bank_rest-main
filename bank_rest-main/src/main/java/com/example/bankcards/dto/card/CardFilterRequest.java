package com.example.bankcards.dto.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardFilterRequest {

    private String status; // ACTIVE / BLOCKED / EXPIRED
    private String holderName;
}