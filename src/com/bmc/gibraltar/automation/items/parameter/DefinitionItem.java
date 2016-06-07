package com.bmc.gibraltar.automation.items.parameter;

/**
 * Interface of common items in Process/Record definitions. Namely:
 * - in Process Definitions this item is  Process Parameter (later INPUT or OUTPUT)
 * - in Record Definitions this item is Record Field
 */
public interface DefinitionItem {

    String getName();

    DefinitionItem setName(String name);

    DataType getDataType();

    DefinitionItem setDataType(DataType dataType);

    boolean isRequired();

    DefinitionItem setRequired(boolean isRequired);

    String getDescription();

    DefinitionItem setDescription(String description);

    String getDefaultValue();

    DefinitionItem setDefaultValue(String defaultValue);
}