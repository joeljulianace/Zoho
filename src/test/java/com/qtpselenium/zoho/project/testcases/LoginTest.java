package com.qtpselenium.zoho.project.testcases;

import java.util.Hashtable;

import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.qtpselenium.zoho.project.base.BaseTest;
import com.qtpselenium.zoho.project.util.DataUtil;
import com.qtpselenium.zoho.project.util.Xls_Reader;
import com.relevantcodes.extentreports.LogStatus;

public class LoginTest extends BaseTest{

	SoftAssert softAssert = null;
	String testCaseName = "LoginTest";
	Xls_Reader xls = null;
	
	@Test(dataProvider="getData")
	public void doLogin(Hashtable<String, String> table){
		//System.out.println("==> IN TEST ANNOTATION");
		test = rep.startTest("LoginTest");
		test.log(LogStatus.INFO, table.toString());
		
		if(!DataUtil.isRunnable(testCaseName, xls)){
			test.log(LogStatus.SKIP, "Skipping as test run is No");
			throw new SkipException("Skipping as test run is No");
		}
		
		if(table.get("Runmode").trim().equals("N")){
			test.log(LogStatus.SKIP, "Skipping as test data run is No");
			throw new SkipException("Skipping as test data run is No");
		}
		
		test.log(LogStatus.INFO, "Starting LoginTest");
		openBrowser(table.get("Browser"));
		navigate("appurl");
		boolean actualResult = doLogin(table.get("Username"), table.get("Password"));
		boolean expectedResult = false;
		
		if(table.get("ExpectedResult").equals("Pass")){
			expectedResult = true;
		}else{
			expectedResult = false;
		}
		
		if(expectedResult != actualResult){
			reportFailure("LoginTest Failed");
		}
		
		reportPass("LoginTest Passed");
	}
	
	@BeforeTest
	public void start(){
		//System.out.println("==> IN BEFORE TEST ANNOTATION");
		init();
	}
	
	@BeforeMethod
	public void initialize(){
		//System.out.println("==> IN BEFORE METHOD ANNOTATION");
		softAssert = new SoftAssert();
	}
	
	@AfterMethod
	public void tearDown(){
		//System.out.println("==> IN AFTER METHOD ANNOTATION");
		try{
			softAssert.assertAll();
		}catch(Error e){
			test.log(LogStatus.FAIL, e.getMessage());
		}
		
		rep.endTest(test);
		rep.flush();
		
		if(driver != null){
			driver.quit();
		}
	}
	
	@DataProvider(parallel=false)
	public Object[][] getData(){
		//System.out.println("==> IN DATA PROVIDER ANNOTATION");
		xls = new Xls_Reader(prop.getProperty("xls_data_path"));
		return DataUtil.getData(xls, testCaseName);
	}
}
