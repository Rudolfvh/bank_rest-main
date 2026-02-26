package com.example.bankcards.persistence;

import com.example.bankcards.security.AesEncryptionService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Converter
@RequiredArgsConstructor
public class CardNumberEncryptor
        implements AttributeConverter<String, String> {

    private final AesEncryptionService aesService;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null
                ? null
                : aesService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null
                ? null
                : aesService.decrypt(dbData);
    }
}