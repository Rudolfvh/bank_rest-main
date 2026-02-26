package com.example.bankcards.dto.card;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
public class CardDto {
    private Long id;
    private String number;
    private String holderName;
    private String status;
    private BigDecimal balance;
}