package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;

public class ElementPropertiesTab extends PropertyTab {
    private ProcessDefinitionEditorPage page;

    public ElementPropertiesTab(WebDriver driver, ProcessDefinitionEditorPage procDesignPage) {
        super(driver, "element");
        this.page = procDesignPage;
    }

    @Step
    public void verifyElementPropertyTabIsEmpty() {
        switchToTab();
        verifyElementPresent(panelXpath + emptyElementPropertyPanel, "The Element Properties tab is not empty");
    }

    /**
     * This method sets a new property value for the {@param elementOnCanvas}.
     *
     * @param elementOnCanvas element on Canvas
     * @param inspectorGroup  group in the Inspector in the Elements Properties Tab
     * @param controlName     name of the property
     * @param newValue        new value of the element's property
     */
    public void setPropertyValue(ActiveElement elementOnCanvas, InspectorGroup inspectorGroup, String controlName,
                                 String newValue) {
        page.clickOnElement(elementOnCanvas);
        String elementPath = panelXpath + inspectorGroup.getLocator() + String.format(label, controlName) + excludeEditLink;
        if (getElement(elementPath).getTagName().equals("select")) {
            selectDropDown(getElement(elementPath), newValue);
        } else {
            expandPanelGroup(inspectorGroup);
            page.typeKeysWithEnter(elementPath, newValue);
        }
        sleep(1); // need to wait for loading some dates
    }

    public void setPropertyValue(ActiveElement elementOnCanvas, InspectorGroup inspectorGroup,
                                 ElementProperties controlName, String newValue) {
        setPropertyValue(elementOnCanvas, inspectorGroup, controlName.getName(), newValue);
    }

    @Step
    public void verifyPropertyMarkedAsRequired(ActiveElement elementOnCanvas, InspectorGroup inspectorGroup,
                                               boolean isRequired, String... propertyToVerify) {
        page.clickOnElement(elementOnCanvas);
        expandAllGroups();
        Arrays.asList(propertyToVerify).forEach(property ->
                verifyEquals(isElementPresent(panelXpath + inspectorGroup.getLocator()
                                + String.format(label, property) + requiredLocator),
                        isRequired));
    }
}