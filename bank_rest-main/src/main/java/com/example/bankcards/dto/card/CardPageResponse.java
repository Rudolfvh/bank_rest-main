package com.example.bankcards.dto.card;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CardPageResponse {

    private List<CardResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}