package com.jpmorgan.main.java.product;

import java.util.Arrays;

public enum OperationType {
    ADD("Add"),
    SUBTRACT("Subtract"),
    MULTIPLY("Multiply");

    private final String code;

    OperationType(String code) {
        this.code = code;
    }

    public static OperationType fromCode(String code) {
        return Arrays.stream(values())
                     .filter(operation -> code.equals(operation.getCode()))
                     .findAny()
                     .orElse(null);
    }

    public String getCode() {
        return code;
    }
}
