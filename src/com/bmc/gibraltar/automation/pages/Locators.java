package com.bmc.gibraltar.automation.pages;

import static java.lang.String.format;

public interface Locators {
    // NODS
    String INPUT = "input";
    String TEXTAREA = "textarea";

    //common
    String xpath = "xpath=";
    String last = xpath + "(%s)[last()]";
    String attribute = "attribute";
    String field = "field";
    String label = "label";
    // Forms windows(e.g.: DataDictionary; Warnings)
    String closeFormBtn = "xpath=//button[@ng-click='cancel()'][text() ='\u00D7']";
    // Grids
    String entityNamesInCardView = "xpath=//h3//a[contains(@ui-sref,'data.name')]";
    // I N S P E C T O R
    String commonPath = "%s//*[contains(@data-%s, '%s')]";
    String shortestPath = "%s//%s[contains(@data-%s, '%s')]";
    /*next 3 locators related to web-element 'Angular select2' (field with Input @ Selector)*/
    String angularSelect2Toggle = "/..//span[contains(@class,'ui-select-toggle')]";
    String angularSelect2Input = "/..//input[@aria-label='Select box']";
    String angularSelect2Optns = "/..//a [@class='ui-select-choices-row-inner']//div[contains(.,'%s')]";

    /**
     * Returns XPath via @data-attribute teg
     *
     * @param attributeContains must be contained in @data-attribute teg
     * @return XPath for some input field (best fit)
     */
    default String getFieldPath(String attributeContains) {
        return format(commonPath, xpath, attribute, attributeContains);
    }

    default String getFieldPath(String prefix, String node, String attributeContains) {
        return format(shortestPath, prefix, node, attribute, attributeContains);
    }

    /**
     * Converts eny (full or particular) XPath to correct XPath with LAST attribute.
     *
     * @param XPath eny xpath. Example //*[class='a'] or xpath=//*[class='a']
     * @return Example xpath=(//*[class='a'])[last()]
     */
    default String getLast(String XPath) {
        if (XPath.startsWith(xpath))
            XPath = XPath.replace(xpath, "");

        return format(last, XPath);
    }
}