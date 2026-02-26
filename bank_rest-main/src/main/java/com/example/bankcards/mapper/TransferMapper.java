package com.example.bankcards.mapper;

import com.example.bankcards.dto.transfer.TransferResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(config = CentralMapperConfig.class)
public interface TransferMapper {

    default TransferResponse toResponse(
            Long fromId,
            Long toId,
            BigDecimal amount
    ) {
        return new TransferResponse(
                fromId,
                toId,
                amount,
                "Transfer successful"
        );
    }
}