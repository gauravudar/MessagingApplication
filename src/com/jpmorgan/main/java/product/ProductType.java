package com.jpmorgan.main.java.product;

import java.util.Arrays;

public enum ProductType {
    APPLE("apples"),
    BANANA("bananas"),
    MANGO("mangoes"),
    COCONUT("coconuts"),
    GRAPE("grapes"),
    ORANGE("oranges"),
    WATERMELON("watermelons"),
    PINEAPPLE("pineapples"),
    STRAWBERRY("strawberries"),
    CHERRY("cherries");

    private final String code;

    ProductType(String code) {
        this.code = code;
    }

    public static ProductType fromCode(String code) {
        return Arrays.stream(values())
                     .filter(productType -> code.equals(productType.getCode()))
                     .findAny()
                     .orElse(null);
    }

    public String getCode() {
        return code;
    }
}
