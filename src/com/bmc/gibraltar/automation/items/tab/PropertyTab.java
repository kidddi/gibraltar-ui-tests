package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.items.element.Property;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

import static java.lang.String.format;

public class PropertyTab extends InspectorTab {
    private static String propertiesTabLink = "//a[contains(concat('', @class, ''), 'rx-blade-%s-property-tab')]";
    private static String propertiesTabPath = "xpath=//*[contains(concat('', @class, ''), 'rx-%s-inspector')]";

    public PropertyTab(WebDriver driver, String tabTag) {
        super(driver, format(propertiesTabLink, tabTag), format(propertiesTabPath, tabTag));
    }

    @Step
    public PropertyTab setProperty(Property property, String value) {
        switchToTab();
        typeKeys(property.getLocator(), value);
        return this;
    }

    public void setProperty(InspectorGroup inspectorGroup, String controlName, String newValue) {
        String elementPath = panelXpath + inspectorGroup.getLocator() + String.format(label, controlName) + excludeEditLink;
        if (getElement(elementPath).getTagName().equals("select")) {
            selectDropDown(getElement(elementPath), newValue);
        } else {
            expandPanelGroup(inspectorGroup);
            typeKeys(elementPath, newValue + "\n");
        }
        //TODO: use wait instead of sleep
        sleep(1); // need to wait for loading some dates
    }
}