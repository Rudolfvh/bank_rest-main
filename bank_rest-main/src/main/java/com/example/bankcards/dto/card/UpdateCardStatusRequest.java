package com.example.bankcards.dto.card;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCardStatusRequest {

    @NotBlank
    private String status; // ACTIVE / BLOCKED
}