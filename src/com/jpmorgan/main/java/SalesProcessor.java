package com.jpmorgan.main.java;

import static java.util.stream.Collectors.groupingBy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jpmorgan.main.java.product.OperationType;
import com.jpmorgan.main.java.product.Product;
import com.jpmorgan.main.java.product.ProductType;
import com.jpmorgan.main.java.sale.Sale;
import com.jpmorgan.main.java.util.MessageParser;

public class SalesProcessor {
    private static final String INPUT_FILE_PATH = "testmessages/inputData.txt";
	private static List<Sale> sales = new ArrayList<>();

    public static void main(String[] args) {
    	try {
			readInputMessageDataAndPrintReports(new FileReader(INPUT_FILE_PATH));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }

	public static void readInputMessageDataAndPrintReports(FileReader fileReader){
		// Read inputs from test file and generate sale and adjustment reports
        try {
            String line;
            long saleId = 1;
            BufferedReader inputFile = new BufferedReader(fileReader);
            while((line = inputFile.readLine()) != null) {
                Sale sale = processMessagesAndGetSaleObject(saleId,line);
                sales.add(sale);
                saleId++;
                
                if(saleId==10 || saleId%10==0) printSalesReport(saleId);

                if(saleId==50) {
                    System.out.println("Application can not process more than 50 messages. Following is the adjustment report.\n");
                    printAdjustmentReport();
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}

    private static Sale processMessagesAndGetSaleObject(long saleId,String message) {
        MessageParser messageParser = new MessageParser(new Product());
        // Process the given message
        messageParser.parseInputMessage(message);
        //create a Sale object out of processed messages
        Sale sale = new Sale();
        sale.setId(saleId);
        sale.setProduct(messageParser.getProduct());

        OperationType operationType = messageParser.getProduct().getOperationType();

        if(operationType == null) {
            sale.setValue(messageParser.getProduct()
                                       .getPrice()
                                       .multiply(BigDecimal.valueOf(messageParser.getProduct().getQuantity())));
        }
        if(operationType != null){
            performSaleAdjustmentForProduct(messageParser, operationType);
        }
        return sale;
    }

    private static void performSaleAdjustmentForProduct(MessageParser messageParser, OperationType operationType) {
        if(operationType==OperationType.ADD) {
            sales.stream()
                 .filter(sale1 -> filterSaleDataForAdjustment(messageParser, sale1))
                 .forEach(sale1 -> sale1.getValue().add(messageParser.getProduct().getPrice()));
        }
        if(operationType==OperationType.SUBTRACT) {
            sales.stream()
                 .filter(sale1 -> filterSaleDataForAdjustment(messageParser, sale1))
                 .forEach(sale1 -> sale1.getValue().subtract(messageParser.getProduct().getPrice()));
        }
        if(operationType==OperationType.MULTIPLY) {
            sales.stream()
                 .filter(sale1 -> filterSaleDataForAdjustment(messageParser, sale1))
                 .forEach(sale1 -> sale1.getValue().multiply(messageParser.getProduct().getPrice()));
        }
    }

    private static void printSalesReport(long saleId){
       Map<ProductType,List<Sale>> productTypeWithSale  = sales.stream()
                                                               .filter(sale -> sale.getProduct().getOperationType() == null)
                                                               .collect(groupingBy(sale -> sale.getProduct().getProductType()));
        System.out.println("***************Sales Report after "+ saleId +" sales *****************");
        System.out.println("|Product Type          | Number of sales   | Total Value      |");
        System.out.println("----------------------------------------------------------------------");
        for(ProductType productType : productTypeWithSale.keySet()) {
            BigDecimal totalValueOfSale = calcTotalValue(productTypeWithSale.get(productType));
            System.out.println("|" + productType+"                       |" +  productTypeWithSale.get(productType).size()+" |                            " + totalValueOfSale      +"p|");
        }
        System.out.println("***************End Of Sales Report ********************\n");
    }

    private static void printAdjustmentReport(){
        Map<ProductType,List<Sale>> productTypeWithSale  = sales.stream()
                                                                .filter(sale -> sale.getProduct().getOperationType() != null)
                                                                .collect(groupingBy(sale -> sale.getProduct().getProductType()));
        System.out.println("***************Adjustment Report after 50 sales *****************");
        System.out.println("|Product Type          | Operation Type        | Adjustment Price   |");
        System.out.println("---------------------------------------------------------------------------");
        for(ProductType productType : productTypeWithSale.keySet()) {
            List<Sale> adjustmentSales = productTypeWithSale.get(productType);
            for(Sale sale : adjustmentSales) {
                System.out.println("|" + productType+"                  | "+ sale.getProduct().getOperationType()+" |                                   " +  sale.getProduct().getPrice() + "p|" );
            }
        }
        System.out.println("***************End Of Adjustment Report ********************");
    }
    
    private static boolean filterSaleDataForAdjustment(MessageParser messageParser, Sale sale1) {
        return sale1.getProduct().getProductType() == messageParser.getProduct()
                                                                   .getProductType()
                         && sale1.getProduct().getOperationType() == null;
    }

    private static BigDecimal calcTotalValue(List<Sale> sales){
        return sales.stream()
                    .map(sale->sale.getValue())
                    .reduce(BigDecimal::add)
                    .get();

    }
}
