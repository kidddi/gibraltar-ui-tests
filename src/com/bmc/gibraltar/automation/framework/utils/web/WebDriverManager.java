package com.bmc.gibraltar.automation.framework.utils.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

/**
 * Created by energetic on 08.06.2016.
 */
public class WebDriverManager {

    private static WebDriver driver;
    private static String baseUrl = "http://www.bmc.com";


    public static WebDriver getWebDriver() {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get(baseUrl + "/");
        return driver;
    }
}
