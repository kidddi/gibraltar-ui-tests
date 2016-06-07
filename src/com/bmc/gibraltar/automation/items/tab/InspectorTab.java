package com.bmc.gibraltar.automation.items.tab;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InspectorTab extends Tab {
    private static String inspectorPanelContainer = "xpath=//div[contains(concat('', @class, ''), 'rx-blade-dock-right')]";
    protected String emptyElementPropertyPanel = "//div[contains(concat('', @class, ''), 'rx-inspector-empty-text')]";
    protected String tabContent = "//div[@class='inspector']";
    protected String editBtnForPropery = "xpath=//label[text()='%s']/../a";
    protected String label = "//label[.='%s']";
    protected String requiredLocator = "[contains(concat('', @class, ''), 'required')]";
    protected String excludeEditLink = "/following-sibling::*[not(@class='edit-link __icon-left-pencil')]";
    private String fieldLabel = "//div[@class='field']//label";
    private String labelForPosition = "//label[contains(text(),'%s')]/../input";
    private String linkToAngularDropDownOption = "/..//a[@class='ui-select-choices-row-inner']";
    private String angularDropDownIcon = "/..//div[contains(concat('', @class, ''), 'ui-select-match')]/span";

    public InspectorTab(WebDriver driver, String tabLink, String panelXpath) {
        super(driver, inspectorPanelContainer, tabLink, panelXpath);
    }

    /**
     * This method gets thr list of options present in the dropdown of the {@param propertyName} property
     *
     * @param group        InspectorGroup
     * @param propertyName propertyName
     * @return if the dropdown is present it will return the list with options,
     * otherwise it will return the list with size 0 if the list either empty or is not present
     */
    public List<String> getAvailableOptionsFromDropdown(InspectorGroup group, String propertyName) {
        log.info("Get the list of properties present in the '" + propertyName + "' dropdown.");
        String pathToLabel = "xpath=" + group.getLocator() + String.format(label, propertyName);
        String pathToSelectElement = pathToLabel + excludeEditLink;
        List<String> dropDownOptions = new ArrayList<>();
        List<WebElement> allOptions = new ArrayList<>();
        try {
            WebElement selectElement = getElement(pathToSelectElement);
            if (!"select".equals(selectElement.getTagName())) {
                // interaction with an angular dropdown
                click(pathToLabel + angularDropDownIcon);
                allOptions = getElements(pathToLabel + linkToAngularDropDownOption);
            } else {
                allOptions = new Select(selectElement).getOptions();
            }
        } catch (NoSuchElementException e) {
            log.info("No select drop-downs are found.");
        }
        for (WebElement el : allOptions) {
            String elText = el.getText().replaceAll("\"", "");
            if (!elText.equals("")) {
                dropDownOptions.add(elText);
            }
        }
        return dropDownOptions;
    }

    @Step
    public void verifyDropDownIsNotPresentForProperty(InspectorGroup group, String propertyName) {
        log.info("Verifying that the dropdown for the '" + propertyName + "' property is not present.");
        String groupPath = "xpath=//" + group.getLocator();
        String labelPath = String.format(label, propertyName);
        verifyFalse(isElementPresent(groupPath + labelPath + angularDropDownIcon));
    }

    @Step
    public void verifyTabContainsProperties() {
        verifyTrue(isElementPresent(panelXpath + tabContent));
    }

    @Step
    public List<String> getPropertiesList(InspectorGroup inspectorGroup) {
        List<WebElement> properties = getListOfElements(panelXpath + inspectorGroup.getLocator() + fieldLabel);
        List<String> propertiesInGroup = new ArrayList<>();
        properties.stream().filter(WebElement::isDisplayed).forEach(singleProperty -> {
            String propertyName = singleProperty.getText();
            if (propertyName.contains("Label Position")) {
                propertiesInGroup.add("Label Position:");
            }
            propertiesInGroup.add(propertyName);
        });
        return propertiesInGroup;
    }

    @Step
    public void verifyPropertiesCount(InspectorGroup inspectorGroup, int expectedPropertiesCount) {
        int propertiesNumber = getPropertiesList(inspectorGroup).size();
        verifyEquals(propertiesNumber, expectedPropertiesCount, "Group " + inspectorGroup.getGroupName()
                + " doesn't contain " + expectedPropertiesCount + " properties.");
    }

    @Step
    public void verifyPropertiesPresence(InspectorGroup inspectorGroup, String[] propertiesNames) {
        log.info("Verifying that the " + inspectorGroup.getGroupName() + " group has "
                + Arrays.asList(propertiesNames) + " properties.");
        List<String> labels = getPropertiesList(inspectorGroup);
        for (String propertiesName : propertiesNames) {
            verifyTrue(labels.contains(propertiesName) || labels.contains(propertiesName + ":"),
                    "Group " + inspectorGroup.getGroupName() + "doesn't contain property: " + propertiesName);
        }
    }

    /**
     * This method gets value of the {@param propertyName} that is in {@param inspectorGroup}
     *
     * @param inspectorGroup group that contains the {@param propertyName}
     * @param propertyName   property name
     * @return value of the property
     */
    public String getPropertyValue(InspectorGroup inspectorGroup, String propertyName) {
        switchToTab();
        String groupPath = panelXpath + inspectorGroup.getLocator();
        String fieldPath = groupPath + String.format(label, propertyName) + excludeEditLink;
        if (propertyName.contains("Label Position")) {
            fieldPath = groupPath + String.format(labelForPosition, propertyName);
        }
        return getValue(fieldPath);
    }

    @Step
    public void verifyPropertyValue(InspectorGroup inspectorGroup, String controlName, String expectedPropertyValue) {
        String actualValue = getPropertyValue(inspectorGroup, controlName);
        verifyEquals(actualValue, expectedPropertyValue,
                "Expected: " + expectedPropertyValue + ",\n but found: " + actualValue);
    }

    @Step
    public boolean isPropertyMarkedAsRequired(InspectorGroup inspectorGroup, String propertyName) {
        return waitForElementPresent(panelXpath + inspectorGroup.getLocator()
                + String.format(label, propertyName) + requiredLocator, 2);
    }
}