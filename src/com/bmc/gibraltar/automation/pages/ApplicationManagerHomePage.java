package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.allure.annotations.Step;

import static java.lang.String.format;

public class ApplicationManagerHomePage extends BasePage {
    private static String headerTabLocator = "xpath=//%s[text() = '%s']";
    private String accessDeniedMessage = "xpath=//div[contains(@class, 'alert-danger')]//div[contains(text(), " +
            "'You do not have access to %s. Please contact your System Administrator.')]";

    @FindBy(css = "[ui-sref = 'ax.home.applications()']")
    private WebElement myApplicationsButton;

    @FindBy(className = "d-n-droprdown__name_rx")
    private WebElement headerProfileArea;

    @FindBy(className = "d-n-droprdown__link")
    private WebElement signOutLink;

    public ApplicationManagerHomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String getPageUrl() {
        return APP_MANAGER_URL + "/app/home/applications?type=all";
    }

    @Override
    public boolean isPageLoaded() {
        return WebUtils.isElementDisplayed(myApplicationsButton);
    }

    @Step
    public void verifyUserHasNoAccessToAppManager() {
        verifyTrue(waitForElementPresent(String.format(accessDeniedMessage, "Application Manager"), 10));
    }

    public ApplicationManagerHomePage clickOnTaskManagerIfAvailable() {
        String taskManager = format(headerTabLocator, "a", "Task Manager");
        if (isElementPresent(taskManager)) {
            click(taskManager);
        } else {
            verifyTrue(isPageLoaded());
        }
        return this;
    }

    /**
     * Easy tab switching
     *
     * @param tabName Just name, one from existed 3. Example View Definitions.
     */
    // TODO DELETE
    @Step
    public ApplicationManagerHomePage navigateToTab(String tabName) {
        clickOnTaskManagerIfAvailable();
        log.info("Navigating to: " + tabName);
        click(format(headerTabLocator, "a", tabName));
        waitForElement(format(headerTabLocator, "h1", tabName, 3));
        return this;
    }

    /**
     * Executes navigating to TaskManager -> ViewDefinitions -> opens {@param viewDefinitionName} View.
     * Useful for using in tests class in @BeforeTests or @BeforeGroups
     *
     * @param viewDefinitionName view definition name to be opened
     * @return instance of ViewDefinitionEditorPage
     */
    @Step
    public ViewDefinitionEditorPage goInitiallyToView(String viewDefinitionName) {
        if (!isPageLoaded()) {
            navigateToPage();
        }
        navigateToTab("View Definitions");
        ViewDefinitionsPage viewsPage = new ViewDefinitionsPage(wd);
        viewsPage.waitForPageLoaded();
        ViewDefinitionEditorPage viewDesignerPage = viewsPage.openView(viewDefinitionName);
        viewDesignerPage.waitForPageLoaded();
        return viewDesignerPage;
    }

    @Step
    public LoginPage logout() {
        headerProfileArea.click();
        signOutLink.click();
        alertAcceptIfPresent();
        LoginPage loginPage = new LoginPage(wd);
        loginPage.waitForPageLoaded();
        return loginPage;
    }
}
