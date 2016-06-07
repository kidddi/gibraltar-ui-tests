package com.bmc.gibraltar.automation.items.record;

import com.bmc.gibraltar.automation.items.CommonHandlers;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.pages.Locators;

import static com.bmc.gibraltar.automation.items.parameter.DataType.*;
import static java.lang.String.format;

public enum RecordProperties implements Locators {
    NAME("Name", true, "rxData/name", "", RecordField.Alltypes),
    DESCRIPTION("Description", false, "rxData/description", "", RecordField.Alltypes),
    REQUIRED("Required", false, "rxData/required", "", RecordField.Alltypes),
    DEFAULT_VALUE("Default Value", false, "rxData/defaultValue", "", CommonHandlers.<DataType>remove(IMAGE, RecordField.Alltypes)),
    MAXIMUM("Maximum", false, "rxData/max", "", DECIMAL, INTEGER, FLOATING),
    MINIMUM("Minimum", false, "rxData/minValue", "", DECIMAL, INTEGER, FLOATING),
    LENGTH("Length", false, "rxData/maxLength", "", TEXT),
    SIZE("Maximum Size (MB)", false, "rxData/maxSize", "", IMAGE),
    PRECISION("Precision", true, "rxData/precision", "", DECIMAL, FLOATING),
    OPTIONS("Options", true, "rxData/options", "", SELECTION),
    TYPE("Data Type", true, "rxData/resourceType", "", RecordField.Alltypes),
    OWNER("Owner", false, "rxData/owner", "", NAN),
    LAST_MODIFIED_DATE("Last Modified Date", false, "rxData/lastUpdateTime", "", NAN),
    LAST_MODIFIED_BY("Last Modified By", false, "rxData/lastChangedBy", "", NAN);

    private String name;
    private boolean isRequiredProperty;
    private String dataAttribute;
    private String value;
    private DataType[] types;

    RecordProperties(String name, boolean isRequiredProperty, String dataAttribute, String value, DataType... types) {
        this.name = name;
        this.isRequiredProperty = isRequiredProperty;
        this.dataAttribute = dataAttribute;
        this.value = value;
        this.types = types;
    }

    public boolean isPresent(DataType type) {
        if (types == RecordField.Alltypes)
            return true;

        return CommonHandlers.contains(type, types);
    }

    public RecordProperties set(String value) {
        this.value = value;
        return this;
    }

    public String getAttr() {
        return dataAttribute;
    }

    public DataType[] getTypes() {
        return types;
    }

    public String getValue() {
        return value;
    }

    public String getLocator() {
        String locator = "xpath=//div[//label[contains(text(), '%s')]]//input[@data-attribute='%s']";
        return getLast(format(locator, name, dataAttribute));
    }
}
