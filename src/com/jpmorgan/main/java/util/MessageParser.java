package com.jpmorgan.main.java.util;

import java.math.BigDecimal;

import com.jpmorgan.main.java.product.OperationType;
import com.jpmorgan.main.java.product.Product;
import com.jpmorgan.main.java.product.ProductType;

public class MessageParser {
    private Product product;

    public MessageParser(Product product){
        this.product=product;
    }

    public Product getProduct() {
        return product;
    }

    public void parseInputMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        String[] splittedMessage = message.trim().split("\\s+");
        String firstWord = splittedMessage[0];
        if (firstWord.matches("Add|Subtract|Multiply")) {
             parseMessageType3(splittedMessage);
        } else if (firstWord.matches("^\\d+")) {
             parseMessageType2(splittedMessage);
        } else if (splittedMessage.length == 3 && ProductType.fromCode(parseProdType(splittedMessage[0]))!=null) {
             parseMessageType1(splittedMessage);
        } else {
            System.out.println("This message type is not supported");
        }
    }

    // Parse message type 1
    private void parseMessageType1(String[] splittedMessage) {
        product.setProductType(ProductType.fromCode(parseProdType((splittedMessage[0]))));
        product.setPrice(parsePrice(splittedMessage[2]));
        product.setQuantity(1); //This will be always 1
    }

    // Parse message type 2
    private void parseMessageType2(String[] splittedMessage) {
        if(splittedMessage.length > 7 || splittedMessage.length < 7) return;
        product.setProductType(ProductType.fromCode(parseProdType(splittedMessage[3])));
        product.setPrice(parsePrice(splittedMessage[5]));
        product.setQuantity(Long.parseLong(splittedMessage[0]));
    }

    // Parse message type 3
    private void parseMessageType3(String[] splittedMessage) {
        if(splittedMessage.length > 3 || splittedMessage.length < 3) return;
        product.setOperationType(OperationType.fromCode(splittedMessage[0]));
        product.setProductType(ProductType.fromCode(parseProdType(splittedMessage[2])));
        product.setPrice(parsePrice(splittedMessage[1]));
    }

    // handle the plural cases of the products
    // @return[String] parsed string of productType e.g 'apple' will become 'apples'
    public String parseProdType(String prodType) {
        String parsedType;
        String typeWithoutLastChar = prodType.substring(0, prodType.length() - 1);
        if (prodType.endsWith("o")) {
            parsedType = String.format("%soes", typeWithoutLastChar);
        } else if (prodType.endsWith("y")) {
            parsedType = String.format("%sies", typeWithoutLastChar);
        } else if (prodType.endsWith("h")) {
            parsedType = String.format("%shes", typeWithoutLastChar);
        } else if (!prodType.endsWith("s")) {
            parsedType = String.format("%ss", prodType);
        } else {
            parsedType = String.format("%s", prodType);
        }
        return parsedType.toLowerCase();
    }

    public BigDecimal parsePrice(String prodPrice) {
        BigDecimal price = new BigDecimal(prodPrice.replaceAll("p", ""));
        return price;
    }

}