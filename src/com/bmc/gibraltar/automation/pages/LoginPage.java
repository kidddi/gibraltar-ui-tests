package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.items.Application;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

public class LoginPage extends BasePage {
    public static String loginPageTitle = "xpath=//div[@class='login']";
    public String fldUserName = "xpath=//input[@placeholder='User Name']";
    public String fldUserPass = "xpath=//input[@placeholder='Password']";
    public String btnLogIn = "xpath=//form[@name='loginForm']//button";
    public String accessDeniedMessage = "xpath=//div[contains(@class, 'alert-danger')]//div[contains(text(), " +
            "'You do not have access to %s. Please contact your System Administrator.')]";
    private Application application;

    protected LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage(WebDriver driver, Application application) {
        super(driver);
        this.application = application;
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(btnLogIn);
    }

    @Override
    public String getPageUrl() {
        if (application == Application.TASK_MANAGER) {
            return TASK_MANAGER_URL;
        } else {
            return APP_MANAGER_URL;
        }
    }

    @Step
    public TasksTabPage loginTaskManager(String login, String password) {
        login(login, password);
        TasksTabPage homePage = new TasksTabPage(wd);
        homePage.waitForPageLoaded();
        return homePage;
    }

    @Step
    public ApplicationManagerHomePage loginToApplicationManager(String login, String password) {
        login(login, password);
        ApplicationManagerHomePage applicationManagerPage = new ApplicationManagerHomePage(wd);
        applicationManagerPage.waitForPageLoaded();
        return applicationManagerPage;
    }

    @Step
    public void login(String login, String password) {
        log.info(String.format("Typed credentials: login='%s', password='%s'", login, password));
        getElement(fldUserName).clear();
        getElement(fldUserName).sendKeys(login);
        getElement(fldUserPass).clear();
        getElement(fldUserPass).sendKeys(password);
        verifyTrue(getAttr(btnLogIn, "disabled") == null);
        click(btnLogIn, 5);
    }

    @Step
    public LoginPage verifyUserHasNoAccess() {
        verifyTrue(waitForElementPresent(String.format(accessDeniedMessage, "Task Manager"), 10));
        return this;
    }
}
