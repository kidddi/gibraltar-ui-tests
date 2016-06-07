package com.bmc.gibraltar.automation.pages;

import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

public abstract class ProcessDesignerViewPage extends EditorPage {

    protected ProcessDesignerViewPage(WebDriver driver) {
        this(driver, "task-manager");
    }

    public ProcessDesignerViewPage(WebDriver driver, String bundle) {
        super(driver, bundle);
    }

    public String getProcessName() {
        return getHeaderName();
    }

    @Step
    public void verifyCurrentProcessName(String processName) {
        verifyEquals(getProcessName(), processName);
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(headerTitle);
    }

    @Override
    public String getPageUrl() {
        return APP_MANAGER_URL + String.format("/app/application/%s/process/new", bundle);
    }

    @Step
    public ProcessDesignerViewPage clickBtnToggle() {
        sleep(3);
        click(toggleButton);
        sleep(1);
        return this;
    }

    @Step
    /**
     * Clicks Save and verifies process is valid and saved successfully
     */
    public ProcessDesignerViewPage saveProcess() {
        log.info("Saving the process definition.");
        click(saveButton);
        assertTrue(isSucessMessageDisplayed("Process Definition saved successfully."), "Process Definition was not saved.");
        waitForElementPresent(disabledSaveButton, 5);
        return this;
    }

    /**
     * Checks if current process valid and captures ScreenShot of ValidationIssues Tab
     *
     * @return isValid
     */
    @Step
    public boolean checkIfProcessValid() {
        boolean isValid = !isErrorMessageDisplayed("The Process Definition is not valid");
        if (this instanceof ProcessDefinitionEditorPage) {
            isValid = !((ProcessDefinitionEditorPage) this)
                    .toValidationTab(wd)
                    .clickToValidationTab()
                    .isValidationTabContainErrors();
        }
        takeScreenshot();
        if (!isValid)
            log.warn("\n Process was NOT valid ! SEE SCREENSHOT");
        return isValid;
    }

    @Step
    public ProcessDesignerViewPage clickSaveButton() {
        click(saveButton);
        sleep(7);
        return this;
    }

    @Step
    public ProcessDefinitionsTabPage closeProcess() {
        return closeEditor(new ProcessDefinitionsTabPage(wd));
    }
}