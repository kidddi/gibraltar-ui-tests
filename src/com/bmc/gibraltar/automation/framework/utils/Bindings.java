package com.bmc.gibraltar.automation.framework.utils;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by energetic on 08.06.2016.
 */
public class Bindings {
    protected final Logger log = Logger.getLogger(getClass());
    protected WebDriver wd;
    protected Select select;

    protected WebElement getElement(String locator) {
        WebElement element = wd.findElement(By.xpath(locator));

        return element;
    }

    protected List<WebElement> getListOfElements(String locator) {
        return wd.findElements(By.xpath(locator));
    }

    protected boolean isElementPresent(String locator) {
        if (wd.findElement(By.xpath(locator)).isDisplayed()) {
            return true;
        }
        return false;
    }

    protected WebElement typeKeys(String locator, String value) {
        WebElement element = null;
        wd.findElement(By.xpath(locator)).sendKeys(value);
        return element;
    }

    protected void click(String locator) {
        wd.findElement(By.xpath(locator)).click();
    }

    protected void click(WebElement locator) {
        locator.click();
    }

    protected String getText(String locator) {
        String text = wd.findElement(By.xpath(locator)).getText();
        return text;
    }

    protected List<WebElement> getElements(String locator) {
        List<WebElement> list = wd.findElements(By.xpath(locator));
        return list;
    }

    protected void verifyFalse(Boolean bool, String msg) {
        if (bool == true) {
            log.info("Verifying is FALSE: " + msg);
        }
    }

    protected void verifyFalse(Boolean bool) {
        if (bool == false) {
            log.info("Verifying is FALSE");
        }
    }

    protected void verifyContains(String str1, String str2) {
        if (str1.equals(str2)) {
            log.info("Contains is Verifyed");
        }
    }

    protected void verifyTrue(Boolean bool) {
        if (bool == true) {
            log.info("Verifying is TRUE");
        }
    }

    protected void verifyTrue(Boolean bool, String msg) {
        if (bool == true) {
            log.info("Verifying is TRUE: " + msg);
        }
    }

    protected Select getSelect(WebElement element) {
        Select select = new Select(element);
        return select;
    }

    protected String getAttr(String locator, String value) {
        String msg = null;
        wd.findElement(By.xpath(locator)).getAttribute(value);

        return msg;
    }

    protected List<String> getListOfWebElementsTextByLocator(String text) {
//        List<WebElement> elements = wd.findElements(By.xpath(text));
//        WebElement element = null;
//        List<String> list = null;
//        for (int i = 0; i < elements.size(); i++){
//            list.add(i, element.getText());
//        }
        return getElements(text).stream().map(WebElement::getText).collect(Collectors.toList());
    }

}
