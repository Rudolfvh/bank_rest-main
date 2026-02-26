package com.example.bankcards.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Mapper(config = CentralMapperConfig.class)
public interface PageMapper {

    public static <T, R> List<R> mapContent(
            Page<T> page,
            Function<T, R> mapper
    ) {
        return page.getContent()
                .stream()
                .map(mapper)
                .toList();
    }
}