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

public class PotentialTest extends BaseTest{

	SoftAssert softAssert = null;
	Xls_Reader xls = null;
	
	@Test(priority=1, dataProvider="getData")
	public void createPotentialTest(Hashtable<String, String> data){
		test = rep.startTest("Create Potential Test");
		test.log(LogStatus.INFO, data.toString());
		
		if(!DataUtil.isRunnable("CreatePotentialTest", xls)){
			test.log(LogStatus.SKIP, "Skipping test as test run is No");
			throw new SkipException("Skipping test as test run is No");
		}
		
		if(data.get("Runmode").trim().equals("N")){
			test.log(LogStatus.SKIP, "Skipping test data as test run is No");
			throw new SkipException("Skipping test data as test run is No");
		}
		
		openBrowser("Chrome");
		navigate("appurl");
		doLogin(envProp.getProperty("username"), envProp.getProperty("password"));
		click("zoho_deals_tab_link_xpath");
		click("zoho_add_deal_button_xpath");
		type("zoho_potentialname_input_xpath", data.get("PotentialName"));
		type("zoho_accountname_input_xpath", data.get("AccountName"));
		click("zoho_potential_closingDate_xpath");
		SelectDate(data.get("ClosingDate"));
		type("zoho_stage_select_xpath", data.get("Stage"));
		//selectElement("zoho_stage_select_xpath", data.get("Stage"));
		click("zoho_deal_save_button_xpath");
		
		//validate
		click("zoho_back_to_deals_link_xpath");
		
		int rNum = getDealRowNum(data.get("PotentialName"));
		
		if(rNum == -1){
			takeScreenshot();
			reportFailure("Create Potential Test Failed");
		}
		
		reportPass("Create Potential Test Passed");
	}
	
	@Test(priority=2)
	public void deletePotentialAccountTest(){
		test = rep.startTest("Delete Potential Test");
		reportPass("Delete Potential Test Passed");
	}
	
	@BeforeTest
	public void initialize(){
		init();
	}
	
	@BeforeMethod
	public void create(){
		softAssert = new SoftAssert();
	}
	
	@AfterMethod
	public void tearDown(){
		
		try {
			softAssert.assertAll();
		} catch (Error e) {
			test.log(LogStatus.FAIL, e.getMessage());
		}
		
		if(rep != null){
			rep.endTest(test);
			rep.flush();
		}
		
		if(driver != null){
			driver.quit();
		}
		
	}
	
	@DataProvider(parallel=false)
	public Object[][] getData(){
		xls = new Xls_Reader(prop.getProperty("xls_data_path"));
		return DataUtil.getData(xls, "CreatePotentialTest");
	}
	
}
