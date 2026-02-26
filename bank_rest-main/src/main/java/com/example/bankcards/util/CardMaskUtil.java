package com.example.bankcards.util;

public class CardMaskUtil {

    public static String mask(String number) {

        if (number.length() < 4) {
            return "****";
        }

        String last4 = number.substring(number.length() - 4);
        return "**** **** **** " + last4;
    }
}