package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.*;
import com.example.bankcards.entity.*;
import org.mapstruct.*;

@Mapper(config = CentralMapperConfig.class)
public interface CardMapper {

    @Mapping(target = "maskedNumber",
            source = "number",
            qualifiedByName = "maskCard")
    @Mapping(target = "status",
            expression = "java(card.getStatus().name())")
    CardResponse toResponse(Card card);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(com.example.bankcards.entity.CardStatus.ACTIVE)")
    @Mapping(target = "owner", ignore = true)
    Card toEntity(CreateCardRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status",
            expression = "java(com.example.bankcards.entity.CardStatus.valueOf(dto.getStatus()))")
    void updateStatus(UpdateCardStatusRequest dto, @MappingTarget Card card);

    @Named("maskCard")
    default String maskCard(String number) {
        if (number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}