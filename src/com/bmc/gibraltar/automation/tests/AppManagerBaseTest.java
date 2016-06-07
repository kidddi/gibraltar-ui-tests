package com.bmc.gibraltar.automation.tests;

import com.bmc.gibraltar.automation.items.Application;
import com.bmc.gibraltar.automation.pages.ApplicationManagerHomePage;
import com.bmc.gibraltar.automation.pages.LoginPage;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class AppManagerBaseTest extends BaseTest {
    protected ApplicationManagerHomePage appManager;

    @BeforeClass
    protected void initiateClass() {
        log.info("BeforeClass start");
        LoginPage loginPage = new LoginPage(wd, Application.APPLICATION_MANAGER).navigateToPage();
        appManager = loginPage.loginToApplicationManager(user.getUsername(), user.getPassword());
        log.info("Before class end");
    }

    @AfterClass(alwaysRun = true)
    protected void quitClass() {
        log.info("AfterClass start");
        appManager.navigateToPage();
        appManager.logout();
        log.info("AfterClass end");
    }
}
