package com.bmc.gibraltar.automation.items.record;

import com.bmc.gibraltar.automation.items.parameter.DataType;

import static com.bmc.gibraltar.automation.items.parameter.DataType.*;

public enum CoreRecordFields {
    DISPLAY_ID("Display ID", TEXT, RecordField.Option.SYSTEM, ""),
    SUBMITTER("Submitter", TEXT, RecordField.Option.REQUIRED, "$USER$"),
    SUBMIT_DATE("Submit Date", DATE_TIME, RecordField.Option.SYSTEM, ""),
    ASSIGNEE("Assignee", TEXT, RecordField.Option.OPTIONAL, ""),
    LAST_MODIFIED_BY("Last Modified By", TEXT, RecordField.Option.SYSTEM, ""),
    LAST_MODIFIED_DATE("Last Modified Date", DATE_TIME, RecordField.Option.SYSTEM, ""),
    STATUS("Status", SELECTION, RecordField.Option.REQUIRED, "New"),
    DESCRIPTION("Description", TEXT, RecordField.Option.SYSTEM, ""),
    ID("ID", TEXT, RecordField.Option.OPTIONAL, "");

    private String name;
    private DataType type;
    private RecordField.Option required;
    private String defaultValue;

    //TODO: extend this enum possibilities;
    CoreRecordFields(String name, DataType type, RecordField.Option required, String defaultValue) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    public RecordField getRecordField() {
        return new RecordField(name, type, required.isRequired(), defaultValue);
    }

    public String getName() {
        return name;
    }
}