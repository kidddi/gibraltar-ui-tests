package com.bmc.gibraltar.automation.pages;

import org.openqa.selenium.WebDriver;

public class ApprovalsPage extends TaskManagerHomePage {
    public String pageName = "xpath=//h1[text()='My Approvals']";

    public ApprovalsPage(WebDriver driver) {
        super(driver);
        tabName = "Approvals";
    }

    @Override
    public String getPageUrl() {
        return TASK_MANAGER_URL + "/tms/approvals";
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(pageName);
    }
}