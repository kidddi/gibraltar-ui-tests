package com.bmc.gibraltar.automation.items.element;

import com.bmc.gibraltar.automation.items.CommonHandlers;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.pages.Locators;
import org.apache.commons.lang3.text.WordUtils;

/**
 * classes are implemented by: DynamicProperty, ElementProperties, PropertiesBlock
 */
public interface Property extends CommonHandlers, Locators {

    String bttn = "//button[@class='btn-list-%s']";
    String inputFieldByLabel = "xpath=//label[contains(text(), '%s')]//../input";

    /**
     * Converts 'wordsInCamelCase' to the 'Words In Camel Case'
     *
     * @param s string to be converted
     * @return
     */
    static String splitCamelCase(String s) {
        return WordUtils.capitalizeFully(s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        ));
    }

    default String getClearName() {
        return getName().replace(":", "");
    }

    InspectorGroup getGroup();

    String getDefault();

    String getLocator();

    String getButton();

    String getName();

    String getFieldNameLocator();

    /**
     * Verify, is current property exists in parameters range.
     */
    default boolean equals(Property... props) {
        for (Property prop : props) {
            if (this == prop) {
                return true;
            }
        }
        return false;
    }

    /**
     * Part-suffix of locator xpath, that identifies the property (nedded for generating the full locator)
     */
    String getLocatorDataAttrib();

    /**
     * Part-suffix of locator xpath, that identifies the property (IF IT HAS BUTTON) (nedded for generating the full locator)
     */
    String getAddButtonLocator();

    /**
     * Convert from "helloWorld" to "Hello World". It is used for converting json field name to UI field name
     *
     * @param value value for converting, should be format: "helloWorld"
     */
    default String convertFromCamelToFieldName(String value) {
        value = splitCamelCase(value);
        return WordUtils.capitalizeFully(value);
    }

    /**
     * Convert from "Hello World:" to "helloWorld". It is used for converting UI field name to json field name.
     *
     * @param value value for converting, should be format: "Hello World:"
     */
    default String convertToCamel(String value) {
        value = value.substring(0, 1).toLowerCase() + value.replaceAll(" ", "").substring(1);
        value = value.substring(0, value.length() - 1);
        return value;
    }

    boolean isAngular();

    void setAngular(boolean isAngular);
}