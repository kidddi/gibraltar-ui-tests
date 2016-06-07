package com.bmc.gibraltar.automation.pages;

import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

/**
 * This page describes common methods for designers (Process Designer, Rule Designer, Record Designer, View Designer)
 */
public abstract class BaseEditorPage extends BasePage {
    //TODO: update string locators be page factory elements
    //TODO: if 'Locators' interface has these locators, remove them from interface
    protected String header = "xpath=//div[contains(@class,'rx-core-editor-header')]";
    protected String headerTitle = "xpath=//span[contains(@class,'rx-core-editor-header__title')]";
    protected String toggleButton = "xpath=//*[@ng-click='toggleDesignMode()']";
    protected String saveButton = "xpath=//button[contains(@ng-click, 'save')]";
    protected String closeButton = "xpath=//button[contains(@ng-click, 'close')]";
    protected String editorContainer = "xpath=//div[@class='rx-core-editor-content']";
    protected String designerUiContainer = editorContainer + "/div[not(contains(@class, 'json-editor-container'))]";
    protected String designerJsonContainer = "xpath=//div[contains(@class, 'json-editor-container')]";
    protected String designerJsonTextarea = designerJsonContainer + "//textarea";
    protected String disabledSaveButton = saveButton + "[@disabled]";
    protected String bundle;

    protected BaseEditorPage(WebDriver driver) {
        super(driver);
    }

    public BaseEditorPage(WebDriver driver, String bundle) {
        super(driver);
        this.bundle = bundle;
    }

    /**
     * Should be overridden with assert that success message is displayed
     * e.g.  assertTrue(isSuccessMessageDisplayed("Record Definition saved successfully."), "Record definition is not saved.");
     */
    protected abstract void checkSuccessMessageWhileSavingDisplayed();

    @Override
    public boolean isPageLoaded() {
        return waitForElementPresent(headerTitle, 10);
    }

    @Step
    public String getEditorTitle() {
        return getText(headerTitle);
    }

    @Step
    public <T extends BaseEditorPage> T saveDefinition() {
        if (isElementPresent(saveButton)) {
            click(saveButton);
            checkSuccessMessageWhileSavingDisplayed();
        }
        waitForElementPresent(disabledSaveButton, 5);
        return (T) this;
    }

    @Step
    public BaseEditorPage clickBtnToggle() {
        if (isElementPresent(toggleButton)) {
            click(toggleButton);
        }
        return this;
    }

    @Step
    public BaseEditorPage clickSave() {
        if (isElementPresent(saveButton) && getAttr(saveButton, "disabled") == null) {
            click(saveButton);
        }
        return this;
    }

    @Step
    public BaseEditorPage clickClose() {
        if (isElementPresent(closeButton)) {
            click(closeButton);
        }
        return this;
    }

    @Step
    public JsonEditorPage goToJsonEditor() {
        if (waitForElementPresent(designerUiContainer, 5)) {
            clickBtnToggle();
        }
        if (waitForElementPresent(designerJsonContainer, 5)) {
            JsonEditorPage jsonEditorPage = new JsonEditorPage(wd);
            jsonEditorPage.waitForPageLoaded();
            return jsonEditorPage;
        } else {
            fail("There is no json editor present.");
            return null;
        }
    }

    @Step
    public <T extends BasePage> T closeEditor(T expectedTabPage) {
        waitForElementPresent(closeButton, 10);
        click(closeButton);
        confirmModalDialog(true);
        expectedTabPage.waitForPageLoaded();
        return expectedTabPage;
    }
}