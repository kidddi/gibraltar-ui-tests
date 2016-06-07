package com.bmc.gibraltar.automation.items.record;

import com.bmc.gibraltar.automation.items.CommonEnumInterface;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.parameter.DefinitionItem;
import com.bmc.gibraltar.automation.pages.Locators;
import com.bmc.gibraltar.automation.pages.RecordDefinitionEditorPage;
import org.apache.commons.lang3.RandomUtils;
import ru.yandex.qatools.allure.annotations.Step;

public class RecordField implements DefinitionItem, Locators, CommonEnumInterface {
    protected final static DataType[] Alltypes = DataType.getAllRecordsTypes();
    private static String elementPath = "xpath=(//span[contains(text(), '%s')])[last()]";
    // inner needs
    private RecordDefinitionEditorPage action;
    //always present
    private String name = "New Field";
    private String description;
    private DataType dataType;
    private Option required;
    private String defaultValue;
    //present for some types
    private String maximum;
    private String minimum;
    private String length;
    private String precision;
    private String[] options;

    public RecordField(String name, DataType dataType, boolean required, String defaultValue) {
        this.name = name;
        this.dataType = dataType;
        setRequired(required);
        this.defaultValue = defaultValue;
    }

    public RecordField(DataType type) {
        this.dataType = type;
    }

    /**
     * Applicable for record fields with type SELECTION only
     */
    public RecordField(String name, DataType dataType, boolean required, String defaultValue, String description, String[] options) {
        this(name, dataType, required, defaultValue);
        if (dataType.equals(DataType.SELECTION))
            this.options = options;
    }

    public RecordField(String name, DataType dataType, boolean required, String precision, String minimum, String maximum, String defaultValue) {
        this(name, dataType, required, defaultValue);
        if (dataType.equals(DataType.FLOATING) || dataType.equals(DataType.DECIMAL)) {
            this.precision = precision;
            this.minimum = minimum;
            this.maximum = maximum;
        }
    }

    /**
     * Sets and types properties with its values. Example  new RecordField(TEXT, NAME.set("Cat"), LENGTH.set("'"777"))
     *
     * @param props should be specified style. Like  OPTIONS.set("tabby, black, monochrome"), REQUIRED.set("true"), MINIMUM.set("123") ..
     */
    public RecordField fillProperty(RecordProperties... props) {
        for (RecordProperties prop : props) {
            if (prop.isPresent(dataType))
                switch (prop) {
                    case NAME:
                        fillName(prop.getValue());
                        break;
                    case DESCRIPTION:
                        fillDescription(prop.getValue());
                        break;
                    case REQUIRED:
                        setPropRequired(prop.getValue().equals("true"));
                        break;
                    case DEFAULT_VALUE:
                        fillDefaultValue(prop.getValue());
                        break;
                    case MAXIMUM:
                        fillMaximum(prop.getValue());
                        break;
                    case MINIMUM:
                        fillMinimum(prop.getValue());
                        break;
                    case LENGTH:
                        fillLength(prop.getValue());
                        break;
                    case PRECISION:
                        fillPrecision(prop.getValue());
                        break;
                    case OPTIONS:
                        fillOptions(prop.getValue().split(", "));
                        break;
                }
        }
        return this;
    }

    public RecordField setAllByDefault() {
        name = dataType.getName() + RandomUtils.nextInt(0, 1000);
        options = new String[]{"First", "Second", "Third"};
        return this;
    }

    /**
     * Safety method! Verifies is this type has such property, and then read the property and returns the value.
     *
     * @param props
     * @return current value of any property, besides "Required"(always returns "on").
     */
    @Step
    public String readProperty(RecordProperties props) {
        if (props.isPresent(dataType))
            return action.getValue(getLast(getFieldPath(props.getAttr())));
        return "";
    }

    @Step
    public RecordField fillName(String name) {
        action.toFieldPropertiesTab(this).fillName(name);
        this.name = name;
        return this;
    }

    @Step
    public RecordField fillDescription(String description) {
        action.toFieldPropertiesTab(this).fillDescription(description);
        this.description = description;
        return this;
    }

    @Step
    public RecordField setPropRequired(boolean required) {
        action.toFieldPropertiesTab(this).setIsRequired(required);
        this.required = Option.setOption(required);
        return this;
    }

    @Step
    public RecordField fillMaximum(String max) {
        type(RecordProperties.MAXIMUM, max);
        this.maximum = max;
        return this;
    }

    @Step
    public RecordField fillMinimum(String min) {
        type(RecordProperties.MINIMUM, min);
        this.minimum = min;
        return this;
    }

    @Step
    public RecordField fillLength(String length) {
        type(RecordProperties.LENGTH, length);
        this.length = length;
        return this;
    }

    @Step
    public RecordField fillPrecision(String precision) {
        type(RecordProperties.PRECISION, precision);
        this.precision = precision;
        return this;
    }

    @Step
    public RecordField fillOptions(String... options) {
        action.toFieldPropertiesTab(this).fillOptions(options);
        this.options = options;
        return this;
    }

    @Step
    public RecordField fillDefaultValue(String defValue) {
        action.toFieldPropertiesTab(this).fillDefaultValue(this, defValue);
        this.defaultValue = defValue;
        return this;
    }

    public String[] getOptions() {
        return dataType.equals(DataType.SELECTION) ? options : null;
    }

    public RecordField setOptions(String... options) {
        if (dataType.equals(DataType.SELECTION))
            this.options = options;
        return this;
    }

    public RecordField setAction(RecordDefinitionEditorPage page) {
        action = page;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public RecordField setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public RecordField setRequired(boolean isRequired) {
        required = Option.setOption(isRequired);
        return this;
    }

    public String getName() {
        return name;
    }

    public RecordField setName(String name) {
        this.name = name;
        return this;
    }

    public DataType getDataType() {
        return dataType;
    }

    @Override
    public RecordField setDataType(DataType dataType) {
        return this;
    }

    public boolean isRequired() {
        return required.isRequired();
    }

    public RecordField setRequired(Option required) {
        this.required = required;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RecordField setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getMaximum() {
        return maximum;
    }

    public RecordField setMaximum(String maximum) {
        this.maximum = maximum;
        return this;
    }

    public String getMinimum() {
        return minimum;
    }

    public RecordField setMinimum(String minimum) {
        this.minimum = minimum;
        return this;
    }

    public String getLength() {
        return length;
    }

    public RecordField setLength(String length) {
        this.length = length;
        return this;
    }

    public String getPrecision() {
        return precision;
    }

    public RecordField setPrecision(String precision) {
        this.precision = precision;
        return this;
    }

    private void type(RecordProperties prop, String text) {
        action.toFieldPropertiesTab(this);
        action.typeKeysWithEnter(getFieldPath(prop.getAttr()), text);
    }

    public String getElementPath() {
        return String.format(elementPath, name);
    }

    public enum Option {
        SYSTEM(true),
        REQUIRED(true),
        OPTIONAL(false);

        private boolean isRequired;

        Option(boolean isRequired) {
            this.isRequired = isRequired;
        }

        public static Option setOption(boolean isRequired) {
            return isRequired ? REQUIRED : OPTIONAL;
        }

        public boolean isRequired() {
            return isRequired;
        }
    }
}