package com.jpmorgan.test.java;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;

import com.jpmorgan.main.java.product.OperationType;
import com.jpmorgan.main.java.product.Product;
import com.jpmorgan.main.java.product.ProductType;
import com.jpmorgan.main.java.util.MessageParser;

import junit.framework.TestCase;

public class MessageParserTest extends TestCase{
   private MessageParser messageParser;
   
   public void setUp() throws Exception {
		super.setUp();
		messageParser = new MessageParser(new Product());
   }
   
   public void testParseMessageForType1() throws Exception{
	   String message = "apple at 10p";
	   messageParser.parseInputMessage(message);
	   assertEquals(messageParser.getProduct().getProductType(),ProductType.APPLE);
	   assertEquals(messageParser.getProduct().getQuantity(),1L);
	   assertEquals(messageParser.getProduct().getPrice(),new BigDecimal("10"));
	   assertEquals(messageParser.getProduct().getOperationType(),null);	   
   }
   
   public void testParseMessageForType2() throws Exception{
	   String message = "20 sales of mangoes at 10p each";
	   messageParser.parseInputMessage(message);
	   assertEquals(messageParser.getProduct().getProductType(),ProductType.MANGO);
	   assertEquals(messageParser.getProduct().getQuantity(),20L);
	   assertEquals(messageParser.getProduct().getPrice(),new BigDecimal("10"));
	   assertEquals(messageParser.getProduct().getOperationType(),null);	   
   }
   
   public void testParseMessageForType3() throws Exception{
	   String message = "Add 20p cherries";
	   messageParser.parseInputMessage(message);
	   assertEquals(messageParser.getProduct().getProductType(),ProductType.CHERRY);
	   assertEquals(messageParser.getProduct().getQuantity(),0L);
	   assertEquals(messageParser.getProduct().getPrice(),new BigDecimal("20"));
	   assertEquals(messageParser.getProduct().getOperationType(),OperationType.ADD);	   
   }
   
   public void testParseMessageForWrongType() throws Exception{
	   final ByteArrayOutputStream out = new ByteArrayOutputStream();
	   System.setOut(new PrintStream(out));
	   String message = "potato at 20p";
	   messageParser.parseInputMessage(message);
	   assertTrue(out.toString().contains("This message type is not supported"));	   
   }
   
  
}
