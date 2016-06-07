package com.bmc.gibraltar.automation.items.parameter;

import java.util.Arrays;

public enum DataType {
    BOOLEAN("com.bmc.arsys.rx.standardlib.record.BooleanFieldDefinition", "Boolean", true, "True"),
    DATE("com.bmc.arsys.rx.standardlib.record.DateOnlyFieldDefinition", "Date", true, "2015-04-08"),
    DATE_TIME("com.bmc.arsys.rx.standardlib.record.DateTimeFieldDefinition", "Date/Time", true, "2014-01-01 01:23 PM"),
    DECIMAL("com.bmc.arsys.rx.standardlib.record.DecimalFieldDefinition", "Decimal", true, "-1.25"),
    FLOATING("com.bmc.arsys.rx.standardlib.record.RealFieldDefinition", "Floating", true, "2.5e-4"),
    IMAGE("om.bmc.arsys.rx.standardlib.record.ImageFieldDefinition", "Image", true, ""),
    INTEGER("com.bmc.arsys.rx.standardlib.record.IntegerFieldDefinition", "Integer", true, "99"),
    SELECTION("com.bmc.arsys.rx.standardlib.record.SelectionFieldDefinition", "Selection", true, "1"),
    TEXT("com.bmc.arsys.rx.standardlib.record.CharacterFieldDefinition", "Text", true, "Some text"),
    TIME("com.bmc.arsys.rx.standardlib.record.TimeOnlyFieldDefinition", "Time", true, "12:55 PM"),
    CONNECTION_INSTANCE("com.bmc.arsys.rx.services.connector.domain.ConnectionInstanceFieldDefinition", "Connection Instance", false, ""),
    PERSON("com.example.taskmanager.domain.PersonFieldDefinition", "Person", false, ""),
    RECORD_INSTANCE("com.bmc.arsys.rx.services.process.domain.record.RecordInstanceFieldDefinition", "Record Instance", false, ""),
    NAN("", "", false, "");

    boolean isRecord;
    String defValueToTest;
    private String resourceType;//value according to BPMN standards
    private String name;

    DataType(String resourceType, String name, boolean isRecord, String defValueToTest) {
        this.resourceType = resourceType;
        this.name = name;
        this.isRecord = isRecord;
        this.defValueToTest = defValueToTest;
    }

    /**
     * This static methods helps to get instance of enum (namely -concrete data type) from text
     *
     * @param text - the String of data type (ignoring case)
     * @return DataType
     */
    public static DataType fromString(String text) {
        if (text != null) {
            for (DataType b : values()) {
                if (text.equalsIgnoreCase(b.getName())) {
                    return b;
                }
            }
        }
        return null;
    }

    public static DataType getDataTypeByResourceType(String text) {
        if (text != null) {
            for (DataType b : values()) {
                if (text.equalsIgnoreCase(b.getResourceType())) {
                    return b;
                }
            }
        }
        return null;
    }

    /**
     * @return Only those Data types that are applicable for Record Fields
     */
    public static DataType[] getAllRecordsTypes() {
        DataType[] arr = new DataType[DataType.values().length];
        int i = 0;
        for (DataType dt : DataType.values()) {
            if (dt.isRecord)
                arr[i++] = dt;
        }
        arr = Arrays.copyOfRange(arr, 0, i);
        return arr;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getName() {
        return name;
    }

    /**
     * @return Value for a particular Data Type, that can be filled into field as a default test value
     */
    public String getDefValueToTest() {
        return defValueToTest;
    }

    public String[] propertiesForInputParameter() {
        return new String[]{
                "Name:",
                "Data Type:",
                "Required:",
                "Description:",
                "Default Value:"
        };
    }
}