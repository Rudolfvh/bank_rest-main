package com.example.bankcards.dto.card;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateCardRequest {

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Card number must contain 16 digits")
    private String number;

    @NotBlank
    private String holderName;

    @Future
    private LocalDate expirationDate;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal balance;

    @NotNull
    private Long ownerId;
}