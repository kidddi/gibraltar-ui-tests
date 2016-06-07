package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.pages.Locators;
import com.bmc.gibraltar.automation.pages.RecordDefinitionEditorPage;
import org.openqa.selenium.WebDriver;

public class RecordPropertiesTab extends InspectorTab implements Locators {
    protected RecordDefinitionEditorPage page;
    private String recordName = getFieldPath(panelXpath, INPUT, "rxData/name");
    private String recordDescription = getFieldPath(panelXpath, TEXTAREA, "rxData/description");

    public RecordPropertiesTab(WebDriver driver, RecordDefinitionEditorPage recordDesignPage, String tabLink, String panelXpath) {
        super(driver, tabLink, panelXpath);
        this.page = recordDesignPage;
    }

    public String getRecordName() {
        switchToTab();
        return getElement(recordName).getAttribute("value");
    }

    /**
     * Type record's name into field 'Name'
     */
    public void fillRecordName(String recName) {
        typeKeys(recordName, recName + "\n");
    }

    /**
     * Type text into field 'Help Text'
     */
    public void setRecordDescription(String text) {
        typeKeys(recordDescription, text + "\n");
    }
}
