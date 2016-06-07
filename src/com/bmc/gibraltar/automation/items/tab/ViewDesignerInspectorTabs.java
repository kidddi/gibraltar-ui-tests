package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.items.component.ActiveComponent;
import com.bmc.gibraltar.automation.items.datadictionary.ViewDictionary;
import com.bmc.gibraltar.automation.items.element.Property;
import com.bmc.gibraltar.automation.pages.ViewDefinitionEditorPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.List;

public class ViewDesignerInspectorTabs extends InspectorTab {
    protected ViewDefinitionEditorPage page;
    private String field = "//div[@class='list-item']//div[@class='field']";

    /**
     * This constructor can be used for any tab of Inspector for ViewDesigner Page
     */
    public ViewDesignerInspectorTabs(WebDriver driver, ViewDefinitionEditorPage viewDesignerPage, ViewDesignerInspectorTabs.Tab tab) {
        super(driver, tab.getTabLink(), tab.getTabPath());
        this.page = viewDesignerPage;
    }

    /**
     * Opens View Dictionary for component's property by clicking Edit button and waits for Dictionary header appearance
     *
     * @param component Active Component on Canvas
     * @param property  Property of editable Active Component
     * @return instance of ViewDictionary
     */
    @Step
    public ViewDictionary openViewDictionaryForProperty(ActiveComponent component, Property property) {
        click(String.format(editBtnForPropery, property.getName()));
        ViewDictionary viewDictionary = new ViewDictionary(wd, component, property);
        if (!viewDictionary.isDisplayed()) {
            fail("Could NOT open View Dictionary for Component: '" + component.getName() + "', " +
                    "Property: '" + property.getName() + "'");
        }
        return viewDictionary;
    }

    @Step
    public List<String> getListOfParameters(InspectorGroup inspectorGroup) {
        List<WebElement> properties = getElements(panelXpath + inspectorGroup.getLocator() + field);
        List<String> fields = new ArrayList<>();
        for (WebElement we : properties) {
            WebElement selectLocator = we.findElement(By.tagName("select"));
            if (selectLocator != null) {
                Select select = new Select(selectLocator);
                fields.add(select.getFirstSelectedOption().getText());
            } else {
                fields.add(we.getText());
            }
        }
        return fields;
    }

    @Step
    public int getParametersCount(InspectorGroup inspectorGroup) {
        List<WebElement> list = getElements(panelXpath + inspectorGroup.getLocator() + field);
        return list.size();
    }

    public enum Tab {
        VIEW_INFORMATION("View Information", "viewInformation", "rx-view-inspector"),
        COMPONENT_INFORMATION("Component Information", "componentInformation", "rx-view-component"),
        VALIDATION_ISSUES("Validation Issues", "validationIssues", "activityResults");
        String name;
        String tabLink;
        String tabPath;

        Tab(String name, String tabLink, String tabPath) {
            this.name = name;
            this.tabLink = tabLink;
            this.tabPath = tabPath;
        }

        public String getName() {
            return name;
        }

        public String getTabLink() {
            return "//a[contains(@tab-name,'" + tabLink + "')]";
        }

        public String getTabPath() {
            return "xpath=//rx-designer-inspector[contains(@class,'" + tabPath + "')]";
        }
    }
}