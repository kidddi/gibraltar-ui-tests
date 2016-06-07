package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.items.tab.ValidationIssuesTab;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

/**
 * This is for pages of designers (like Process/Record/View Designer)
 */
public interface HasValidator {

    @Step
    default ValidationIssuesTab toValidationTab(WebDriver driver) {
        ValidationIssuesTab tab = new ValidationIssuesTab(driver, this);
        tab.switchToTab();
        return tab;
    }
}
