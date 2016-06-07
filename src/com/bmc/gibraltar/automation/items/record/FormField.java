package com.bmc.gibraltar.automation.items.record;

import com.bmc.gibraltar.automation.items.CommonEnumInterface;

public enum FormField implements CommonEnumInterface {
    DATE_FIELD("Date Field"), DATE_TIME_FIELD("Date/Time Field"), DECIMAL_FIELD("Decimal Field"), DROPDOWN("Dropdown"),
    FLOAT_FIELD("Floating Field"), INTEGER_FIELD("Integer Field"), TEXT_AREA("Text Area"), TEXT_FIELD("Text Field"),
    TIME_FIELD("Time Field");

    private String name;

    FormField(String name) {
        this.name = name;
    }

    //TODO update
    public static String getNameXpathOnCanvas() {
        return "//span[@class='rx-view-designer-item-name ng-binding']";
    }

    @Override
    public String getName() {
        return name;
    }

    public String getXpathOnPalette() {
        return "//div[@class='rx-blade-content']//*[contains(text(),'" + name + "')]/..";
    }

    public String getXpathOnCanvas() {
        return "//div[@class='rx-view-designer-item-halo']//span[text()='" + name + "']/..";
    }
}
