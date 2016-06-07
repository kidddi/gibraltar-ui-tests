package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.record.RecordField;
import com.bmc.gibraltar.automation.items.tab.RecordFieldPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.RecordPropertiesTab;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

public class RecordDefinitionEditorPage extends BaseEditorPage implements HasValidator {
    /*TOOL BAR*/
    private static String addNewField = "xpath=//a[.='New Field']";
    private static String deleteBtn = "xpath=//button[contains(@ng-click,'deleteField')]";
    private static String undoBtn = "xpath=//button[contains(@ng-click,'undo')]";
    private static String redoBtn = "xpath=//button[contains(@ng-click,'redo')]";
    private String recFieldRow = "xpath=//div[contains(@class, 'ng-scope ngRow')]//span[.='%s']";
    private String checkBoxOfRecField = "xpath=//label[contains(concat(' ', @class, ' '), 'cellSelectionLabel') " +
            "and ancestor-or-self::div[contains(@class, 'ng-scope ngRow')]//span[.='%s']]";
    private String selectDataTypeFromDropDown = "xpath=//ul[contains(@class,'dropdown-menu')]//a[.='%s']";
    /* INSPECTOR TABs LOCATORS */
    private String recordPropertiesTabLink = "//a[contains(concat('', @class, ''), 'rx-blade-record-property-tab')]";
    private String fieldPropertiesTabLink = "//a[contains(concat('', @class, ''), 'rx-blade-field-property-tab')]";
    private String recordPropertiesTabPath = "xpath=//*[contains(@class, 'rx-record-inspector')]";
    private String fieldPropertiesTabPath = "xpath=//*[contains(@class, 'rx-field-inspector')]";

    public RecordDefinitionEditorPage(WebDriver driver) {
        this(driver, "task-manager");
    }

    public RecordDefinitionEditorPage(WebDriver driver, String bundle) {
        super(driver, bundle);
    }

    @Override
    protected void checkSuccessMessageWhileSavingDisplayed() {
        assertTrue(isSucessMessageDisplayed("Record Definition saved successfully."));
    }

    @Override
    public String getPageUrl() {
        return APP_MANAGER_URL + String.format("/app/application/%s/record/", bundle);
    }

    @Step
    //TODO: remove usages of this method and use saveDefinition() instead
    public RecordDefinitionEditorPage saveRecord() {
        saveDefinition();
        return this;
    }

    @Step
    public RecordDefinitionsPage closeRecordDefinition() {
        return closeEditor(new RecordDefinitionsPage(wd));
    }

    public RecordPropertiesTab toRecordPropertiesTab() {
        RecordPropertiesTab tab = new RecordPropertiesTab(wd, this, recordPropertiesTabLink, recordPropertiesTabPath);
        tab.switchToTab();
        return tab;
    }

    public RecordFieldPropertiesTab toFieldPropertiesTab(RecordField recField) {
        clickOnRecFieldRow(recField.getName());
        return new RecordFieldPropertiesTab(wd, this, fieldPropertiesTabLink, fieldPropertiesTabPath);
    }

    public RecordFieldPropertiesTab toFieldPropertiesTab() {
        RecordFieldPropertiesTab tab = new RecordFieldPropertiesTab(wd, this, fieldPropertiesTabLink, fieldPropertiesTabPath);
        tab.switchToTab();
        return tab;
    }

    @Step
    public RecordDefinitionEditorPage setRecordName(String recordName) {
        toRecordPropertiesTab().fillRecordName(recordName);
        return this;
    }

    @Step
    public boolean isRecordFieldPresent(String recField) {
        return waitForElementPresent(String.format(recFieldRow, recField), 5);
    }

    @Step
    public RecordDefinitionEditorPage clickOnRecFieldRow(String recField) {
        click(getElement(String.format(recFieldRow, recField)));
        return this;
    }

    /**
     * Selects a record field by clicking on its checkbox
     */
    @Step
    public RecordDefinitionEditorPage selectRecField(String recField) {
        log.info("Selecting " + recField + " field.");
        String checkBoxLocator = String.format(checkBoxOfRecField, recField);
        if (!isRecFieldSelected(recField) && waitForElementPresent(checkBoxLocator, 5)) {
            click(checkBoxLocator);
        }
        return this;
    }

    /**
     * Deselects a record field by clicking on its checkbox
     */
    @Step
    public RecordDefinitionEditorPage deSelectRecField(String recField) {
        log.info("Deselecting the " + recField + " field.");
        if (isRecFieldSelected(recField)) {
            String checkBoxLocator = String.format(checkBoxOfRecField, recField);
            click(checkBoxLocator);
        }
        return this;
    }

    @Step
    public boolean isRecFieldSelected(String recField) {
        String checkBoxLocator = String.format(checkBoxOfRecField, recField);
        return getElement(checkBoxLocator).getAttribute("class").contains("check_circle");
    }

    @Step
    public RecordDefinitionEditorPage deleteRecField(String recField) {
        selectRecField(recField);
        if (!isDeleteButtonActive()) {
            fail("Delete Button is not active.");
        }
        clickToolBarItem(ToolBarItem.DELETE_FIELD);
        confirmModalDialog(true);
        return this;
    }

    public RecordDefinitionEditorPage clickDelete() {
        click(deleteBtn);
        return this;
    }

    @Step
    public void verifyDeleteBtnInactive() {
        verifyFalse(isDeleteButtonActive(), "Delete button was active");
    }

    @Step
    public void verifyDeleteBtnActive() {
        verifyTrue(isDeleteButtonActive(), "Delete button was NOT active");
    }

    private boolean isDeleteButtonActive() {
        return getElement(deleteBtn).isEnabled();
    }

    /**
     * Adds a Record Field, fills into Inspector tab all values of Field Properties,
     * like Name/Is Required/Default Value (instance of RecordField should have these values)
     *
     * @param field
     */
    @Step
    public RecordDefinitionEditorPage addRecordField(RecordField field) {
        try {
            clickToolBarItem(ToolBarItem.NEW_FIELD);
            selectDataTypeWhenAddingRecField(field);
            RecordFieldPropertiesTab fieldTab = toFieldPropertiesTab();
            fieldTab
                    .fillName(field.getName())
                    .fillDescription(field.getDescription())
                    .setIsRequired(field.isRequired())
                    .fillOptions(field.getOptions())
                    .fillDefaultValue(field, field.getDefaultValue());
            if (field.getDataType().equals(DataType.DECIMAL) || field.getDataType().equals(DataType.FLOATING)) {
                fieldTab.fillPrecision(field.getPrecision())
                        .fillMaximum(field.getMaximum())
                        .fillMinimum(field.getMinimum());
            }
        } catch (Throwable e) {
            e.printStackTrace();
            fail("\n Could NOT add Record Field: " + field.getName());
        }
        return this;
    }

    @Step
    public RecordDefinitionEditorPage createRecordField(RecordField field) {
        try {
            clickToolBarItem(ToolBarItem.NEW_FIELD);
            selectDataTypeWhenAddingRecField(field);
            RecordFieldPropertiesTab fieldTab = toFieldPropertiesTab();
            fieldTab.fillName(field.getName())
                    .fillOptions(field.getOptions());
            log.info(field.getName() + "Has just been added");
        } catch (Throwable e) {
            e.printStackTrace();
            fail("\n Could NOT add Record Field: " + field.getName());
        }
        return this;
    }

    /**
     * Only clicks "Add New Field" and selects DataType (no properties are filled)
     */
    @Step
    public RecordDefinitionEditorPage addRecordFieldDraft(RecordField field) {
        try {
            click(addNewField);
            selectDataTypeWhenAddingRecField(field);
            log.info(field.getDataType() + "Has just been added");
        } catch (Throwable e) {
            e.printStackTrace();
            fail("\n Could NOT add Record Field: " + field.getName());
        }
        return this;
    }

    private void selectDataTypeWhenAddingRecField(RecordField field) {
        click(String.format(selectDataTypeFromDropDown, field.getDataType().getName()));
    }

    @Step
    public RecordDefinitionEditorPage undoLastChange(boolean confirm) {
        clickToolBarItem(ToolBarItem.UNDO);
        confirmModalDialog(confirm);
        return this;
    }

    @Step
    public RecordDefinitionEditorPage redoLastChange(boolean confirm) {
        clickToolBarItem(ToolBarItem.REDO);
        confirmModalDialog(confirm);
        return this;
    }

    @Step("Verify Toolbar item: {0} if active: {1}")
    public RecordDefinitionEditorPage verifyToolBarItemActive(ToolBarItem item, boolean isActive) {
        String isDisabledSfx = isActive ? "" : "[@disabled='disabled']";
        verifyTrue(isElementPresent(item.locator + isDisabledSfx));
        return this;
    }

    @Step("Click ToolBar item: {0}")
    public RecordDefinitionEditorPage clickToolBarItem(ToolBarItem item) {
        click(item.locator);
        return this;
    }

    public enum ToolBarItem {
        NEW_FIELD(addNewField),
        DELETE_FIELD(deleteBtn),
        UNDO(undoBtn),
        REDO(redoBtn);

        String locator;

        ToolBarItem(String itemLocator) {
            this.locator = itemLocator;
        }
    }
}