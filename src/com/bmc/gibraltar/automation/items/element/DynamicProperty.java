package com.bmc.gibraltar.automation.items.element;

import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.pages.BasePage;

public class DynamicProperty implements Property {
    private static final String defaultValue = "1";
    //TODO: make locator private
    String locator;
    private InspectorGroup group = InspectorGroup.INPUT_MAP;
    private ElementProperties blockProperty;
    private String name;
    private String locatorDataAttrib;
    private String button;
    private boolean isAngular;
    private BasePage page = ElementProperties.page;

    public DynamicProperty(String name) {
        this.name = name;
        this.locatorDataAttrib = convertToCamel(name);
        this.locator = getLast(getFieldPath(locatorDataAttrib));
    }

    public DynamicProperty(ElementProperties blockProperty) {
        isAngular = blockProperty.isAngular();
        this.blockProperty = blockProperty;
        name = blockProperty.getName();
        locatorDataAttrib = blockProperty.getLocatorDataAttrib();
        locator = blockProperty.getLocator();
        group = blockProperty.getGroup();
        button = blockProperty.getButton();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InspectorGroup getGroup() {
        return group;
    }

    @Override
    public String getDefault() {
        return defaultValue;
    }

    @Override
    public String getFieldNameLocator() {
        return "xpath=//div[@data-name='" + group.getDataNameValue() + "']" + ElementProperties.getFieldNameLocator(name);
    }

    @Override
    public String getLocator() {

        if (isAngular) {
            page.click(getLast(locator + angularSelect2Toggle));
            return getLast(locator + angularSelect2Input);
        }
        return locator;
    }

    @Override
    public String getButton() {
        return button;
    }

    public String getLocatorDataAttrib() {
        return locatorDataAttrib;
    }

    @Override
    public String getAddButtonLocator() {
        return String.format(bttn, button);
    }

    @Override
    public boolean isAngular() {
        return isAngular;
    }

    @Override
    public void setAngular(boolean isAngular) {
        this.isAngular = isAngular;
    }
}
