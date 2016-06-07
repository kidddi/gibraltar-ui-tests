package com.bmc.gibraltar.automation.items.parameter;

public class ProcessParameter implements DefinitionItem {
    private boolean required = true;
    private String name = "";
    private DataType dataType;
    private String description = "";
    private String defaultValue = "";
    private String[] options;
    private boolean useSampleData;
    private String recordDefinition;
    private String permissionType;
    private String permissionGroup;

    public ProcessParameter(String name, DataType dataType, boolean required, String description, String defaultValue) {
        this.name = name;
        this.dataType = dataType;
        this.required = required;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public ProcessParameter(String name, DataType dataType, String description) {
        this.name = name;
        this.dataType = dataType;
        this.description = description;
    }

    public ProcessParameter(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    /**
     * For permission entry
     *
     * @param permittedGroup user group : e.g. Process Manager, Process Designer
     * @param permissionType : Read or Execute
     */
    public ProcessParameter(String permittedGroup, String permissionType) {
        this.permissionGroup = permittedGroup;
        this.permissionType = permissionType;
    }

    /**
     * Applicable for process inputs with type SELECTION only
     */
    public ProcessParameter(String name, DataType dataType, String[] options, boolean required, String description, String defaultValue) {
        this(name, dataType, required, description, defaultValue);
        if (dataType.equals(DataType.SELECTION))
            this.options = options;
    }

    /**
     * Applicable for process inputs with type RECORD_INSTANCE only
     */
    public ProcessParameter(String name, DataType dataType, boolean useSampleData, String recordDefinition, boolean required, String description) {
        this(name, dataType, required, description, "");
        if (dataType.equals(DataType.RECORD_INSTANCE))
            this.useSampleData = useSampleData;
        this.recordDefinition = recordDefinition;
    }


    public String[] getOptions() {
        return dataType.equals(DataType.SELECTION) ? options : null;
    }

    public void setOptions(String[] options) {
        if (dataType.equals(DataType.SELECTION))
            this.options = options;
    }

    public String getName() {
        return name;
    }

    public ProcessParameter setName(String name) {
        this.name = name;
        return this;
    }

    public DataType getDataType() {
        return dataType;
    }

    public ProcessParameter setDataType(DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ProcessParameter setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public ProcessParameter setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public ProcessParameter setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public boolean isUseSampleData() {
        return useSampleData;
    }

    public ProcessParameter setUseSampleData(boolean useSampleData) {
        this.useSampleData = useSampleData;
        return this;
    }

    public String getRecordDefinition() {
        return recordDefinition;
    }

    public ProcessParameter setRecordDefinition(String recordDefinition) {
        this.recordDefinition = recordDefinition;
        return this;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public ProcessParameter setPermissionType(String permissionType) {
        this.permissionType = permissionType;
        return this;
    }

    public String getPermissionGroup() {
        return permissionGroup;
    }

    public ProcessParameter setPermissionGroup(String permissionGroup) {
        this.permissionGroup = permissionGroup;
        return this;
    }
}