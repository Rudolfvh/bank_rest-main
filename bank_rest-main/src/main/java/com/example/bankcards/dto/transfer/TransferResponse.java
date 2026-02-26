package com.example.bankcards.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TransferResponse {

    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private String message;
}