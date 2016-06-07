package com.bmc.gibraltar.automation.pages;

import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

public class ProcessDefinitionJsonEditorPage extends ProcessDesignerViewPage {

    public ProcessDefinitionJsonEditorPage(WebDriver driver) {
        super(driver);
        definitionEditorName = "Process Definition JSON Editor";
    }

    @Override
    protected void checkSuccessMessageWhileSavingDisplayed() {
    }
    // TODO: implement mandatory methods + add needed methods for working with current page;

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(designerJsonTextarea);
    }

    @Step
    public String getJsonText() {
        return getValue(designerJsonTextarea);
    }

    /**
     * In Firefox Json textarea has attribute 'ng-readonly', but in Chrome it's 'readonly'
     */
    @Step
    public void verifyJsonEditorIsEnabled() {
        verifyTrue(!isElementReadOnly(designerJsonTextarea) || null == getAttr(designerJsonTextarea, "ng-readoly"));
    }

    @Step
    public ProcessDefinitionJsonEditorPage verifyJsonEditorIsReadonly() {
        verifyTrue(isElementReadOnly(designerJsonTextarea));
        return this;
    }

    @Step
    public ProcessDefinitionJsonEditorPage verifyJsonEditorViewEnabled() {
        verifyElementPresent(designerJsonTextarea);
        return this;
    }

    @Step
    public ProcessDefinitionJsonEditorPage verifyNameIsPresent(String expectedName) {
        verifyTrue(getJsonText().contains(expectedName), "Json Editor does not contain a definition name");
        return this;
    }

    @Step
    public void switchToEditorView() {
        click(toggleButton);
    }
}