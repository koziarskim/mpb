package com.noovitec.mpb.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Logging {

	public static void main(String[] args) throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\kozia\\eclipse\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.get("https://marcin.noovitec.com/mpb/static/login");
		driver.manage().window().maximize();
		driver.findElement(By.id("login-username")).sendKeys("autotest");
		driver.findElement(By.id("login-password")).sendKeys("i57600k");
		driver.findElement(By.id("login-button")).click();
		String alogin = driver.getCurrentUrl();
		String elogin = "https://marcin.noovitec.com/mpb/static/login";
		if (alogin.equalsIgnoreCase(elogin)) {

			System.out.println("Test Successful-Log in");
		} else {
			System.out.println("Test Failure-Log in");
		}
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.findElement(By.id("nav-supplier")).click();
		String anavsupplier = driver.getCurrentUrl();
		String enavsupplier = "https://marcin.noovitec.com/mpb/static/supplierList";
		if (anavsupplier.equalsIgnoreCase(enavsupplier)) {
			System.out.println("Test Successful-Nav Supplier");
		} else {
			System.out.println("Test Failure- Nav Supplier");
		}
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.findElement(By.id("supplierlist-button")).click();
		String asupplierbutton = driver.getCurrentUrl();
		String esupplierbutton = "https://marcin.noovitec.com/mpb/static/supplier";
		if (asupplierbutton.contains(esupplierbutton)) {
			System.out.println("Test Successful-Supplier Button");
		} else {
			System.out.println("Test Failure- Supplier Button");
		}
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.findElement(By.id("supplieredit-name")).sendKeys("Test");
		driver.findElement(By.id("supplieredit-savebutton")).click();
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		String asuppliername = driver.getCurrentUrl();
		String esuppliername = "https://marcin.noovitec.com/mpb/static/supplier";
		if (asuppliername.contains(esuppliername)) {
			System.out.println("Test Successful- Supplier Tab");
		} else {
			System.out.println("Test Failure- Supplier Tab");
		}
//TODO: Not sure why below fails.
/*
		driver.findElement(By.id("nav-component")).click();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		String anavcomponent = driver.getCurrentUrl();
		String enavcomponent = "https://marcin.noovitec.com/mpb/static/component";
		if (anavcomponent.contains(enavcomponent)) {
			System.out.println("Test Successful-Nav Component");
		} else {
			System.out.println("Test Failure-Nav Component");
		}
		driver.findElement(By.id("componentlist-newbtn")).click();
		driver.findElement(By.id("componentedit-name")).sendKeys("Test");
		driver.findElement(By.id("componentedit-number")).sendKeys("123");
		driver.findElement(By.id("componentedit-number")).sendKeys(Keys.ENTER);
		driver.findElement(By.id("componentedit-category")).sendKeys("Other");
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div/div[1]/div[3]/div/div[2]/div[1]")).click();
		driver.findElement(By.id("componentedit-type")).click();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div/div[1]/div[4]/div/div[2]/div[3]")).click();
		driver.findElement(By.id("componentedit-supplier")).sendKeys("Test");
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		Thread.sleep(200);
		driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div/div[2]/div/div/div[1]/div/div[2]/div[1]")).click();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.findElement(By.id("componentedit-savecomponent")).click();
		driver.findElement(By.id("nav-component")).click();
		String anewcomponent = driver.getCurrentUrl();
		String enewcomponent = "https://marcin.noovitec.com/mpb/static/component";
		if (anewcomponent.contains(enewcomponent)) {
			System.out.println("Test Successful-New Component");
		} else {
			System.out.println("Test Failure-New Component");
		}
		driver.findElement(By.id("nav-supplier")).click();
		driver.findElement(By.id("supplierlist-searchname")).sendKeys("Test");
		driver.findElement(By.id("supplierlist-searchname")).sendKeys(Keys.ENTER);

		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		Thread.sleep(200);
		driver.findElement(By.xpath("//*[@id=\"__BVID__469\"]/tbody/tr/td[1]/a")).click();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.findElement(By.id("supplieredit-name")).sendKeys(Keys.CONTROL, "a", Keys.DELETE);
		String asupplieredit = driver.getCurrentUrl();
		String esupplieredit = "https://marcin.noovitec.com/mpb/static/supplier";
		if (asupplieredit.contains(esupplieredit)) {
			System.out.println("Test Successful-Supplier edit");
		} else {
			System.out.println("Test Failure- Supplier edit");
		}
		driver.findElement(By.id("supplieredit-savebutton")).click();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		Thread.sleep(500);
		driver.findElement(By.id("nav-component")).click();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.findElement(By.id("componentlist-name")).sendKeys("Test");
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		driver.findElement(By.id("componentlist-name")).sendKeys(Keys.ENTER);
		driver.findElement(By.xpath("//*[@id=\"__BVID__804\"]/tbody/tr/td[10]/input")).click();
		driver.findElement(By.id("componentlist-newpo")).click();
		driver.findElement(By.id("C")).sendKeys("Test");
		driver.findElement(By.xpath("//*[@id=\"__BVID__1130\"]/tbody/tr/td[3]/div/input")).sendKeys("1");
		driver.findElement(By.xpath("//*[@id=\"__BVID__1130\"]/tbody/tr/td[4]/input")).sendKeys("10");
		driver.findElement(By.id("purchaseedit-save")).click();

//		driver.findElement(By.id("nav-purchse")).click();

		driver.findElement(By.id("nav-component")).click();
		driver.findElement(By.id("componentlist-name")).sendKeys("Test");
		driver.findElement(By.id("componentlist-name")).sendKeys(Keys.ENTER);
		driver.findElement(By.xpath("//*[@id=\"__BVID__1163\"]/tbody/tr/td[1]/div/a")).click();
		driver.findElement(By.id("componentedit-delete")).click();
		driver.findElement(By.cssSelector("#__BVID__1508___BV_modal_footer_ > button.btn.btn-primary")).click();
		// change to id instead of xpath
*/

		driver.close();
		System.out.println("MPB Selenium Test completed!");

	}

}
