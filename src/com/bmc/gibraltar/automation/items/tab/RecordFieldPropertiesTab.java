package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.record.RecordField;
import com.bmc.gibraltar.automation.pages.RecordDefinitionEditorPage;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

public class RecordFieldPropertiesTab extends InspectorTab {
    protected RecordDefinitionEditorPage page;
    /* LOCATORS for properties */
    private String fieldName = "//div[@data-field='rxData/name']//input";
    private String fieldDescription = "//div[@data-field='rxData/description']//textarea";
    private String fieldPrecision = "//div[@data-field='rxData/precision']//input";
    private String fieldMaximum = "//div[@data-field='rxData/maxValue']//input";
    private String fieldMinimum = "//div[@data-field='rxData/minValue']//input";
    private String drpDwnDataType = "//div[@data-field='rxData/resourceType']//select";
    private String toggleIsRequired = "//div[@data-field='rxData/required']//input";
    private String inputOptios = "//div[@data-field='rxData/options']//textarea";
    /* LOCATORS for property of Default Value */
    private String inputDefValue = "//div[@data-field='rxData/defaultValue']//input";
    private String drpDwnDefValue = "//div[@data-field='rxData/defaultValue']//select";
    private String datePicker = "//input[@datepicker-options='datepickerOptions']";
    private String timeHrPicker = "//input[@ng-model='hours']";
    private String timeMinPicker = "//input[@ng-model='minutes']";
    private String timeAmPmMarker = "//div[@ng-click='toggleMeridian()']//input";
    private String pickerIncrease = "//span[contains(@class, 'd-timepicker__increase')]";

    public RecordFieldPropertiesTab(WebDriver driver, RecordDefinitionEditorPage recordDesignPage, String tabLink, String panelXpath) {
        super(driver, tabLink, panelXpath);
        this.page = recordDesignPage;
    }

    @Step
    public RecordFieldPropertiesTab fillName(String name) {
        typeKeys(panelXpath + fieldName, name + "\n");
        return this;
    }

    @Step
    public RecordFieldPropertiesTab fillDescription(String description) {
        typeKeys(panelXpath + fieldDescription, description + "\n");
        return this;
    }

    @Step
    public RecordFieldPropertiesTab setIsRequired(boolean isRequired) {
        String locator = panelXpath + toggleIsRequired;
        if (!(getElement(locator).isSelected() == isRequired)) {
            click(locator);
        }
        return this;
    }

    @Step
    public RecordFieldPropertiesTab fillOptions(String[] options) {
        if (getDataType().equals(DataType.SELECTION)) {
            String locator = panelXpath + inputOptios;
            for (int i = 0; i < options.length; i++) {
                typeKeys(locator, options[i], false, false).sendKeys(Keys.ENTER);
            }
        }
        return this;
    }

    @Step
    public RecordFieldPropertiesTab fillDefaultValue(RecordField field, String defValue) {
        switch (field.getDataType()) {
            case DATE:
                typeKeys(panelXpath + datePicker, defValue);
                break;
            case DATE_TIME: {
                typeKeys(panelXpath + datePicker, defValue.substring(0, 10));
                fillTime(defValue);
            }
            break;
            case DECIMAL:
            case FLOATING:
            case INTEGER:
            case TEXT:
                typeKeys(panelXpath + inputDefValue, defValue);
                break;
            case TIME:
                fillTime(defValue);
                break;
            case BOOLEAN:
            case SELECTION:
                selectDropDown(panelXpath + drpDwnDefValue, defValue);
                break;
        }
        return this;
    }

    private void fillTime(String defValue) {
        String hours = defValue.split(":")[0];
        String minutes = defValue.split(":")[1].split(" ")[0];
        String amPmMarker = defValue.split(" ")[1];

        typeKeys(panelXpath + timeHrPicker, hours);
        typeKeys(panelXpath + timeMinPicker, minutes);
        if (getValue(panelXpath + timeAmPmMarker) != amPmMarker) {
            click(panelXpath + pickerIncrease);
        }
    }

    public DataType getDataType() {
        return DataType.fromString(getText(panelXpath + drpDwnDataType));
    }

    public RecordFieldPropertiesTab fillPrecision(String precision) {
        typeKeys(panelXpath + fieldPrecision, precision);
        return this;
    }

    public RecordFieldPropertiesTab fillMaximum(String maximum) {
        typeKeys(panelXpath + fieldMaximum, maximum);
        return this;
    }

    public RecordFieldPropertiesTab fillMinimum(String minimum) {
        typeKeys(panelXpath + fieldMinimum, minimum);
        return this;
    }
}