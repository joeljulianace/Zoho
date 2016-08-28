package com.qtpselenium.zoho.project.util;

import java.util.Hashtable;

public class DataUtil {

	public static Object[][] getData(Xls_Reader xls, String testCaseName){
		String sheetName = "Data";
		//reads data for only testCaseName
		
		int testStartRowNum = 1;
		
		while(!xls.getCellData(sheetName, 0, testStartRowNum).equals(testCaseName)){
			testStartRowNum++;
		}
		
		System.out.println("Test start from row: " + testStartRowNum);
		
		int colStartRowNum = testStartRowNum + 1;
		int dataStartRowNum = testStartRowNum + 2;
		
		//calculate rows of data
		int rows = 0;
		while(!xls.getCellData(sheetName, 0, dataStartRowNum+rows).equals("")){
			rows++;
		}
		
		System.out.println("Total Rows Are: " + rows);
		
		//calculate the no. of cols of data
		int cols = 0;
		while(!xls.getCellData(sheetName, cols, colStartRowNum).equals("")){
			cols++;
		}
		
		System.out.println("Total Cols Are: " + cols);
		Object[][] data = new Object[rows][1];
		
		//read data
		int dataRow = 0;
		Hashtable<String, String> table = null;
		for(int rNum = dataStartRowNum; rNum < (rows + dataStartRowNum); rNum++){
			//System.out.println("Row No: " + rNum);
			table = new Hashtable<String, String>();
			for(int cNum = 0; cNum < cols; cNum++){
				//System.out.println("Col No: " + cNum);
				String key = xls.getCellData(sheetName, cNum, colStartRowNum);
				String value = xls.getCellData(sheetName, cNum, rNum);
				table.put(key, value);
			}
			data[dataRow][0] = table;
			dataRow++;
		}
		return data;
	}
	
	public static boolean isRunnable(String testName, Xls_Reader xls){
		String sheetName = "Test Cases";
		int rows = xls.getRowCount(sheetName);
		
		for(int row = 2; row <= rows; row++){
			String tcid = xls.getCellData(sheetName, "TCID", row);
			if(tcid.trim().equals(testName.trim())){
				String runmode = xls.getCellData(sheetName, "Runmode", row);
				if(runmode.equals("Y")){
					return true;
				}
			}
		}
		
		return false;
	}
}
