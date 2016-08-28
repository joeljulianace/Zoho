package com.qtpselenium.zoho.project.base;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import com.qtpselenium.zoho.project.util.ExtentManager;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

//All test cases will extend this class
//This class will have all reusable functions

public class BaseTest {
	
	public WebDriver driver = null;
	public Properties prop = null;
	public Properties envProp = null;
	public ExtentReports rep = ExtentManager.getInstance();
	public ExtentTest test = null;
	boolean gridRun = false;
	
	public void init(){
		if(prop == null){
			prop = new Properties();
			envProp = new Properties();
			try {
				FileInputStream fs = new FileInputStream(System.getProperty("user.dir") + "//src//test//resources//projectconfig.properties");
				prop.load(fs);
				String env = prop.getProperty("env");
				fs = new FileInputStream(System.getProperty("user.dir") + "//src//test//resources//"+env+".properties");
				envProp.load(fs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void openBrowser(String browserType){
		if(!gridRun){
			if(browserType.equals("Mozilla")){
				test.log(LogStatus.INFO, "Opening Mozilla Browser");
				driver = new FirefoxDriver();
			}else if(browserType.equals("Chrome")){
				test.log(LogStatus.INFO, "Opening Chrome Browser");
				System.setProperty("webdriver.chrome.driver", prop.getProperty("chromedriver_exe"));
				driver = new ChromeDriver();
			}
		}else{
			//grid run
			DesiredCapabilities cap = null;
			
			if(browserType.equals("Chrome")){
				cap = DesiredCapabilities.chrome();
				cap.setBrowserName("chrome");
				cap.setPlatform(Platform.WINDOWS);
			}
			
			try {
				driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
		test.log(LogStatus.INFO, "Browser Opened Successfully");
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}
	
	public void navigate(String urlKey){
		test.log(LogStatus.INFO, "Navigating to URL: " + envProp.getProperty(urlKey));
		driver.get(envProp.getProperty(urlKey));
	}
	
	public void click(String locatorKey){
		test.log(LogStatus.INFO, "Clicking on: " + locatorKey);
		getElement(locatorKey).click();
		test.log(LogStatus.INFO, "Clicking successfully on: " + locatorKey);
	}
	
	public void type(String locatorKey, String text){
		test.log(LogStatus.INFO, "Typing in locator: " + locatorKey + ". Data: " + text);
		getElement(locatorKey).sendKeys(text);
	}
	
	public void selectElement(String locatorKey, String text){
		
		test.log(LogStatus.INFO, "Selecting From: " + locatorKey + " Value: " + text);
		Select select = new Select(getElement(locatorKey));
		//select.selectByValue(valuetoSelect);
		//select.selectByVisibleText(text);
		List<WebElement> options = select.getOptions();
		
		for(int i = 0; i < options.size(); i++){
			System.out.println(options.get(i).getText());
		}
		
		test.log(LogStatus.INFO, "Value selected successfully from: " + locatorKey);
	}
	
	//finding the element on the page and returning it
	public WebElement getElement(String locatorKey){
		
		WebElement element = null;
		
		try{
			if(locatorKey.endsWith("_id")){
				element = driver.findElement(By.id(prop.getProperty(locatorKey)));
			}else if(locatorKey.endsWith("_name")){
				element = driver.findElement(By.name(prop.getProperty(locatorKey)));
			}else if(locatorKey.endsWith("_xpath")){
				element = driver.findElement(By.xpath(prop.getProperty(locatorKey)));
			}else{
				reportFailure("Locator not found: " + locatorKey);
				Assert.fail("Locator not found: " + locatorKey);
			}
			
		}catch(Exception e){
			reportFailure(e.getMessage());
			e.printStackTrace();
			Assert.fail("Failed the test: " + e.getMessage());
		}
		
		return element;
	}
	
	/********************************VALIDATIONS*****************************/
	
	public boolean verifyTitle(){
		
		return false;
	}
	
	public boolean isElementPresent(String locatorKey){
		
		List<WebElement> elementList = null;
		
		if(locatorKey.endsWith("_id")){
			elementList = driver.findElements(By.id(prop.getProperty(locatorKey)));
		}else if(locatorKey.endsWith("_name")){
			elementList = driver.findElements(By.name(prop.getProperty(locatorKey)));
		}else if(locatorKey.endsWith("_xpath")){
			elementList = driver.findElements(By.xpath(prop.getProperty(locatorKey)));
		}else{
			reportFailure("Locator not found: " + locatorKey);
			Assert.fail("Locator not found: " + locatorKey);
		}
		
		if(elementList.size() == 0){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean verifyText(String locatorKey, String expectedTextKey){
		
		String actualText = getElement(locatorKey).getText();
		String expectedText = prop.getProperty(expectedTextKey);
		
		if(actualText.trim().equals(expectedText.trim())){
			return true;
		}else{		
			return false;
		}
	}
	
	public void waitForPageToLoad(){
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String state = (String) js.executeScript("return document.readyState");
		
		while(!state.equals("complete")){
			wait(2);
			state = (String) js.executeScript("return document.readyState");
		}
	}
	
	public void wait(int timeToWaitInSec){
		try {
			Thread.sleep(timeToWaitInSec * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getText(String locatorKey){
		test.log(LogStatus.INFO, "Getting text from locator: " + locatorKey);
		return getElement(locatorKey).getText();
	}
	
	
	/*****************************Reporting***************************************/
	
	public void reportPass(String msg){
		test.log(LogStatus.PASS, msg);
	}
	
	public void reportFailure(String msg){
		test.log(LogStatus.FAIL, msg);
		takeScreenshot();
		Assert.fail(msg);
	}
	
	public void takeScreenshot(){
		
		Date d = new Date();
		String fileName = d.toString().replace(":", "_").replace(" ", "_") + ".jpg";
		String filePath = System.getProperty("user.dir") + "//screenshots//" + fileName;
		
		//fileName of the screenshot
		File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		try {
				FileUtils.copyFile(srcFile, new File(filePath));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		//adding screenshot in the report
		test.log(LogStatus.INFO, "Taking Screenshot: " + test.addScreenCapture(filePath));
	}
	
	/************************************App Functions*********************************/
	
	public boolean doLogin(String username, String password){
		
		test.log(LogStatus.INFO, "Logging in with " + username + ", " + password);
		waitForPageToLoad();
		wait(1);
		//switching to the frame
		driver.switchTo().frame(0);
		//System.out.println(driver.findElements(By.tagName("iframe")).size());
		type("zoho_username_input_xpath", username);
		type("zoho_password_input_xpath", password);
		click("zoho_signin_button_xpath");
		
		if(isElementPresent("zoho_welcome_title_xpath")){
			test.log(LogStatus.INFO, "Login Success");
			return true;
		}else{
			test.log(LogStatus.INFO, "Login Failed");
			return false;
		}
	}
	
	public int getLeadRowNum(String leadName){
		
		test.log(LogStatus.INFO, "Finding the lead: " + leadName);
		
		List<WebElement> leadNames = driver.findElements(By.xpath(prop.getProperty("zoho_lead_names_table_xpath")));
		
		for(int i = 0; i < leadNames.size(); i++){
			if(leadNames.get(i).getText().trim().equals(leadName)){
				test.log(LogStatus.INFO, "Lead Found In Row Num: " + (i+1));
				return (i+1);
			}
		}
		
		test.log(LogStatus.INFO, "Lead Not Found");
		return -1;
	}
	
	public int getDealRowNum(String potentialName){
		
		test.log(LogStatus.INFO, "Finding the potential: " + potentialName);
		
		List<WebElement> potentialNames = driver.findElements(By.xpath(prop.getProperty("zoho_potential_names_table_xpath")));
		
		for(int i = 0; i < potentialNames.size(); i++){
			if(potentialNames.get(i).getText().trim().equals(potentialName)){
				test.log(LogStatus.INFO, "Potential Found On Row Num: " + (i+1));
				return (i+1);
			}
		}
		
		return -1;
	}
	
	public boolean clickOnLead(String leadName){
		test.log(LogStatus.INFO, "Clicking on lead: " + leadName);
		int rNum = getLeadRowNum(leadName);
		
		if(rNum != -1){
			driver.findElement(By.xpath(prop.getProperty("zoho_lead_name_table_part1_xpath")+(rNum+1)+prop.getProperty("zoho_lead_name_table_part2_xpath"))).click();
			test.log(LogStatus.INFO, "Successfully clicked on lead: " + leadName);
			return true;
		}
		
		test.log(LogStatus.INFO, "Lead Not Present in the List");
		return false;
	}
	
	public void SelectDate(String d){
		test.log(LogStatus.INFO, "Selecting the date: " + d);
		//convert string date to date object
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		try{
			Date datetoBeSelected = sdf.parse(d);
			Date currentDate = new Date();
			sdf = new SimpleDateFormat("MMMM");
			String monthToBeSelected = sdf.format(datetoBeSelected);
			System.out.println("Month to Be Selected: " + monthToBeSelected);
			sdf = new SimpleDateFormat("yyyy");
			String yearToBeSelected = sdf.format(datetoBeSelected);
			System.out.println("Year To Be Selected: " + yearToBeSelected);
			sdf = new SimpleDateFormat("d");
			String dayToBeSelected = sdf.format(datetoBeSelected);
			System.out.println("Day To Be Selected: " + dayToBeSelected);
			
			String monthYearToBeSelected = monthToBeSelected + " " + yearToBeSelected;
			System.out.println("Month & Year To Be Selected: " + monthYearToBeSelected);
			
			while(true){
				if(currentDate.compareTo(datetoBeSelected) == 1){
					//back
					click("zoho_deal_previousMonth_xpath");
				}else if(currentDate.compareTo(datetoBeSelected) == -1){
					//front
					click("zoho_deal_nextMonth_xpath");
				}
				
				if(monthYearToBeSelected.equals(getText("zoho_deal_monthYearDisplayed_xpath"))){
					System.out.println("Month & Year on Calendar: " + getText("zoho_deal_monthYearDisplayed_xpath"));
					break;
				}
				
				/*
				wait(3);
				System.out.println(driver.findElement(By.xpath("//td[text()='6']")).getText());
				driver.findElement(By.xpath("//td[text()='"+dayToBeSelected+"']")).click();
				test.log(LogStatus.PASS, "Date Selected Successfully");
				*/
			}

			driver.findElement(By.xpath("//td[text()='"+dayToBeSelected+"']")).click();
			test.log(LogStatus.INFO, "Date Selected Successfully");
		}catch(Exception e){
			test.log(LogStatus.INFO, e.getMessage());
		}
	}
}
