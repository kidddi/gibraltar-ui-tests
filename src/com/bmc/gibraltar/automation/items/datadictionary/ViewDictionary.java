package com.bmc.gibraltar.automation.items.datadictionary;

import com.bmc.gibraltar.automation.items.component.ActiveComponent;
import com.bmc.gibraltar.automation.items.element.Property;
import org.openqa.selenium.WebDriver;

public class ViewDictionary extends Dictionary {
    private ActiveComponent component;

    public ViewDictionary(WebDriver driver, ActiveComponent component, Property property) {
        super(driver, component, property);
        this.component = component;
    }

    @Override
    protected <T> String getLocatorOfVariable(T... vars) {
        return "";
    }
}
