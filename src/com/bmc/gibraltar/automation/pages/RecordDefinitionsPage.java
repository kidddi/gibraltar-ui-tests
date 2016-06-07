package com.bmc.gibraltar.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.List;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.ActionBar.Action.DELETE;
import static com.bmc.gibraltar.automation.items.ActionBar.Action.NEW;

public class RecordDefinitionsPage extends TaskManagerHomePage {
    private String pageHeaderName = "xpath=//h1[text()='Record Definitions']";

    public RecordDefinitionsPage(WebDriver driver) {
        super(driver, "task-manager");
        tabName = "Record Definitions";
    }

    public RecordDefinitionsPage(WebDriver driver, String bundleName) {
        super(driver, bundleName);
    }

    @Override
    public String getPageUrl() {
        return APP_MANAGER_URL + String.format("/app/application/%s/definitions?tab=record-definitions", bundle);
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(pageHeaderName);
    }

    @Step
    public RecordDefinitionEditorPage initiateNewRecordDef() {
        actionBar(NEW).click();
        RecordDefinitionEditorPage recordDefinitionEditorPage = new RecordDefinitionEditorPage(wd);
        if (!recordDefinitionEditorPage.isPageLoaded()) {
            recordDefinitionEditorPage.clickBtnToggle();
        }
        return recordDefinitionEditorPage;
    }

    /**
     * @return the list of saved record Names,
     * that are present in the 'Record Definitions' tab
     */
    @Step
    public List<String> getNamesOfExistingRecords() {
        return getElements(entityNamesInGridView).stream().map(WebElement::getText).collect(Collectors.toList());
    }

    /**
     * Verifies if the specific record definition with the {@param recordDefinitionName} present/not present on the page.
     *
     * @param recordDefinitionName record definition name
     * @param expected             true - should be present, false - should not be present
     * @return current page
     */
    @Step
    public RecordDefinitionsPage verifyRecordDefinitionPresence(String recordDefinitionName, boolean expected) {
        verifyEquals(isRecordExistent(recordDefinitionName), expected);
        return this;
    }

    @Step("Verifying if the {0} record definition exists.")
    public boolean isRecordExistent(String recordDefinitionName) {
        return getNamesOfExistingRecords().contains(recordDefinitionName);
    }

    @Step
    public RecordDefinitionEditorPage openRecordDefinition(String recordDefinitionName) {
        waitForElementPresent("link=" + recordDefinitionName, 5);
        click("link=" + recordDefinitionName);
        RecordDefinitionEditorPage designerPage = new RecordDefinitionEditorPage(wd);
        designerPage.waitForPageLoaded();
        return designerPage;
    }

    /**
     * Selects a Record Definition by clicking on its checkbox
     */
    @Step
    public RecordDefinitionsPage selectRecord(String recordDefinitionName) {
        log.info("Selecting the '" + recordDefinitionName + "' record definition.");
        if (!isRecordSelected(recordDefinitionName)) {
            String checkBoxLocator = String.format(checkboxForRow, recordDefinitionName);
            click(checkBoxLocator);
        }
        return this;
    }

    /**
     * Deselects a Record Definition by clicking on its checkbox
     */
    @Step
    public RecordDefinitionsPage deSelectRecord(String recordDefinitionName) {
        log.info("Deselecting  the '" + recordDefinitionName + "' record definition.");
        if (isRecordSelected(recordDefinitionName)) {
            String checkBoxLocator = String.format(checkboxForRow, recordDefinitionName);
            click(checkBoxLocator);
        }
        return this;
    }

    @Step
    public boolean isRecordSelected(String recordDefinitionName) {
        String checkBoxLocator = String.format(checkboxForRow, recordDefinitionName);
        return getElement(checkBoxLocator).getAttribute("class").contains("check_circle");
    }

    @Step
    public RecordDefinitionsPage deleteRecord(String recordDefinitionName) {
        selectRecord(recordDefinitionName);
        if (!isDeleteButtonActive()) {
            fail("Delete Button is not active.");
        }
        clickDelete();
        confirmModalDialog(true);
        return this;
    }

    public RecordDefinitionsPage clickDelete() {
        actionBar(DELETE).click();
        return this;
    }

    @Step
    public void verifyDeleteBtnActive() {
        verifyTrue(isDeleteButtonActive(), "Delete button was not active.");
    }

    private boolean isDeleteButtonActive() {
        return actionBar(DELETE).isActionEnabled();
    }
}