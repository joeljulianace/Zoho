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

public class LeadTest extends BaseTest{

	SoftAssert softAssert = null;
	Xls_Reader xls = null;
	//String testCaseName = "DeleteLeadAccountTest";
	
	@Test(priority=1, dataProvider="getData")
	public void createLeadTest(Hashtable<String, String> data){
		test = rep.startTest("Create Lead Test");
		test.log(LogStatus.INFO, data.toString());
		
		if(!DataUtil.isRunnable("CreateLeadTest", xls)){
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
		click("zoho_lead_tab_link_xpath");
		click("zoho_new_lead_button_xpath");
		type("zoho_lead_company_xpath", data.get("LeadCompany"));
		type("zoho_lead_last_name_xpath", data.get("LeadLastName"));
		click("zoho_lead_save_button_xpath");
		wait(2);
		click("zoho_back_to_lead_link_xpath");

		//validate
		int rNum = getLeadRowNum(data.get("LeadLastName"));
		
		if(rNum == -1){
			takeScreenshot();
			reportFailure("Lead Not Found in Lead Table: " + data.get("LeadLastName"));
		}
		reportPass("Lead Found in Lead Table: " + data.get("LeadLastName"));
	}
	
	@Test(priority=2, dataProvider="getData")
	public void convertLeadTest(Hashtable<String, String> data){

		//1. While converting lead from an existing organization 
		//a. Add to the existing account
		//b. Create a new account
		//c. Click Convert/Cancel button (Read from xls file)
		
		//2. While converting an existing lead from an existing organization
		//a. Add to the existing contact
		//b. Create a new contact
		//c. Click Convert/Cancel button (Read from xls file)
		
		//3. While converting a new lead from a new (non-existing) organization
		//a. Click Covert/Cancel button (Read from xls file)
		
		
		//Assert.fail();
		System.out.println("convertLeadTest");
		//testCaseName = "ConvertLeadTest";
		test = rep.startTest("Convert Lead Test");
		test.log(LogStatus.INFO, data.toString());
		
		if(!DataUtil.isRunnable("ConvertLeadTest", xls)){
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
		click("zoho_lead_tab_link_xpath");
		boolean status = clickOnLead(data.get("LeadLastName"));
		
		if(status){
			click("zoho_convert_lead_button_xpath");
			click("zoho_convert_lead_save_button_xpath");
			click("zoho_go_back_to_leads_button_xpath");
			
			//Check if lead is not present in the leads table
			int rNum = getLeadRowNum(data.get("LeadLastName"));
			
			if(rNum == -1){
				reportPass("Lead: " + data.get("LeadLastName") + " converted successfully");
			}else{
				takeScreenshot();
				reportFailure("Lead: " + data.get("LeadLastName") + " counld not be converted successfully");
			}	
		}else{
			softAssert.assertTrue(false, "Unable to convert lead: " + data.get("LeadLastName"));
		}
	}
	
	@Test(priority=3, dataProvider="getDeleteLeadData")
	public void deleteLeadAccountTest(Hashtable<String, String> data){
		
		//After deleting check if the lead is deleted from the leads table based on the following condition
		//1. Lead with the same name and company name does not exist in the table
		//2. Only then the lead is considered deleted
		
		System.out.println("deleteLeadAccountTest");
		
		test = rep.startTest("Delete Lead Test");
		test.log(LogStatus.INFO, data.toString());
		
		if(!DataUtil.isRunnable("DeleteLeadAccountTest", xls)){
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
		click("zoho_lead_tab_link_xpath");
		if(clickOnLead(data.get("LeadLastName"))){
			click("zoho_lead_options_buttons_xpath");
			wait(2);
			test.log(LogStatus.INFO, "Deleting Lead: " + data.get("LeadLastName"));
			click("zoho_lead_delete_button_xpath");
			wait(3);
			click("zoho_lead_confirm_delete_button_xpath");
			wait(3);
			click("zoho_back_to_lead_link_xpath");
			
			int rNum = getLeadRowNum(data.get("LeadLastName"));
			
			if(rNum == -1){
				reportPass("Lead: " + data.get("LeadLastName") + " deleted successfully");
			}else{
				takeScreenshot();
				reportFailure("Lead: " + data.get("LeadLastName") + " could not be deleted");
			}
		}else{
			softAssert.assertTrue(false, "Could not find lead: " + data.get("LeadLastName"));
		}
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
		return DataUtil.getData(xls, "CreateLeadTest");
	}
	
	@DataProvider(parallel=false)
	public Object[][] getDeleteLeadData(){
		xls = new Xls_Reader(prop.getProperty("xls_data_path"));
		return DataUtil.getData(xls, "DeleteLeadAccountTest");
	}
}
