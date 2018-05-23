package com.jpmorgan.test.java;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.PrintStream;

import com.jpmorgan.main.java.SalesProcessor;
import junit.framework.TestCase;

public class SalesProcessorTest extends TestCase {
	private SalesProcessor salesProcessor;	

	public void setUp() throws Exception {
		super.setUp();
		salesProcessor = new SalesProcessor();	
	}
	
	//Positive input data test case
	public void testGenerateAndPrintSalesReport() throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		SalesProcessor.readInputMessageDataAndPrintReports(new FileReader("testmessages/inputData.txt"));
		assertTrue(out.toString().contains("***************Adjustment Report after 50 sales *****************"));	
	}

	//Negative input data test case
	//This will throw NullPointerException due to wrong format of input data
	public void testGenerateAndPrintSalesReportWithWrongData() throws Exception {
		try {
			SalesProcessor.readInputMessageDataAndPrintReports(new FileReader("testmessages/wrongInputData.txt"));
		}
		catch (Throwable expected) {
			assertEquals(NullPointerException.class, expected.getClass());
		}
	}
	
}
