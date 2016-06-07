package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.List;
import java.util.stream.Collectors;

public class ViewDefinitionsPage extends TaskManagerHomePage {
    @FindBy(xpath = "//span[text()='View Definition Name']")
    private WebElement viewDefinitionNameColumn;

    @FindBy(css = ".is-checked .__icon-left-plus_circle")
    private WebElement newButton;

    public ViewDefinitionsPage(WebDriver driver) {
        super(driver, "task-manager");
        tabName = "View Definitions";
    }

    public ViewDefinitionsPage(WebDriver driver, String bundle) {
        super(driver, bundle);
        tabName = "View Definitions";
    }

    @Override
    public String getPageUrl() {
        return APP_MANAGER_URL + String.format("/app/application/%s/definitions?tab=view-definitions", bundle);
    }

    @Override
    public boolean isPageLoaded() {
        return WebUtils.isElementDisplayed(viewDefinitionNameColumn);
    }

    @Step
    public ViewDefinitionEditorPage initiateNewView() {
        log.info("Initiate new View.");
        newButton.click();
        ViewDefinitionEditorPage editorPage = new ViewDefinitionEditorPage(wd);
        editorPage.waitForPageLoaded();
        return editorPage;
    }

    @Step
    public ViewDefinitionEditorPage openView(String viewName) {
        log.info("Opening a view with the name: " + viewName);
        String viewLink = "link=" + viewName;
        waitForElementNotPresent(loadingSpinner, 10);
        try {
            click(viewLink);
        } catch (NoSuchElementException e) {
            Assert.fail("Could not find the View: " + viewName, e);
        }
        ViewDefinitionEditorPage editorPage = new ViewDefinitionEditorPage(wd);
        editorPage.waitForPageLoaded();
        return editorPage;
    }

    /**
     * @return the list of saved View Definitions Names,
     * that are present in the 'Configure' tab
     */
    public List<String> getNamesOfViewsDefinitions() {
        List<WebElement> webElementsOfNames;
        if (isElementPresent(gridContainer)) {
            webElementsOfNames = getElements(entityNamesInGridView);
        } else {
            webElementsOfNames = getElements(entityNamesInCardView);
        }
        return webElementsOfNames.stream().map(WebElement::getText).collect(Collectors.toList());
    }
}
