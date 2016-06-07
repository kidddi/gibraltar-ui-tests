package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.parameter.ProcessParameter;
import com.bmc.gibraltar.automation.pages.EditorPage;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class ProcessPropertiesTab extends PropertyTab {
    private static final String locatorByText = "xpath=//div[@class='rx-blade-tab-pane ng-scope active']//*[contains(.,'%s')]";
    private EditorPage page;
    private String addParamIcon = "//button[@class='btn-list-add']";
    private String listItem = "//div[@class='list-item']";
    private String processParam = "//div[@class='list-items']" + listItem;
    private String processParameter = "//div[@class='list-items']/div[@class='list-item']";
    // TODO: verify the xpath of the delete button
    private String deleteParamButton = "//button[@class='btn-list-del']";
    private String paramPropertiesLabel = "//div[@class='object-properties']//label";
    private String procParamName = listItem + "//div[@data-property='name']//input";
    private String procParamDataType = listItem + "//div[@data-property='resourceType']//select";
    private String procParamRequired = listItem + "//div[@data-property='required']//input";
    private String procParamDescription = listItem + "//div[@data-property='description']//textarea";
    private String procOptions = listItem + "//div[@data-property='options']//textarea";
    private String procParamRecordDef = listItem + "//div[@data-property='recordDefinitionName']//select";
    private String procParamUseSampleData = listItem + "//div[@data-property='useSampleData']//label";
    private String procParamDefaultValue = listItem + "//div[@data-property='defaultValue']//input";
    private String datePicker = procParamDefaultValue + "[@datepicker-options='datepickerOptions']";
    private String timeHrPicker = procParamDefaultValue + "[@ng-model='hours']";
    private String timeMinPicker = procParamDefaultValue + "[@ng-model='minutes']";
    private String timeAmPmMarker = procParamDefaultValue.replace("input", "div[@ng-click='toggleMeridian()']");
    private String meridianInput = "//input[contains(@class, 'd-timepicker__input')]";
    private String increaseTimeAmPmMarker = "//span[contains(@class, 'd-timepicker__increase')]";
    private String permittedGroups = listItem + "//div[@data-property='permittedGroup']//select";
    private String permissionTypes = listItem + "//div[@data-property='permissionType']//select";
    private String processName = "//div[@data-field='rxData/name']//input";
    private String enableSwitchForPD = "xpath=//input[@data-attribute='rxData/isEnabled']";
    private String sampleRecordDefinition = "xpath=//rx-inspector-record-instance-definition-name//label[.='Sample Record Definition:']";

    public ProcessPropertiesTab(WebDriver driver, EditorPage editorPage) {
        super(driver, "process");
        this.page = editorPage;
    }

    /**
     * Returns a locator of any place from Process Information tab.
     * Example: you need to verify some picture/icon/mark on place with some title.
     *
     * @param visualTextOnTab a visual title on Process Information tab.
     * @return
     */
    public static String getLocatorByTitle(String visualTextOnTab) {
        return format(locatorByText, visualTextOnTab);
    }

    public String getProcessName() {
        switchToTab();
        return getElement(panelXpath + processName).getAttribute("value");
    }

    /**
     * Set a new process name via the process property tab.
     *
     * @param newProcessName
     */
    public void setProcessName(String newProcessName) {
        log.info("Process name is " + newProcessName);
        typeKeys(panelXpath + processName, newProcessName + "\n");
    }

    /* NEXT METHODS RELATE TO PROCESS PARAMETERS*/

    /**
     * This method will count parameters that are present in the {@inspectorGroup}
     *
     * @param inspectorGroup in the Process Properties Tab
     * @param expectedCount  of the parameters in the{@inspectorGroup}
     */
    public void verifyParametersCount(InspectorGroup inspectorGroup, int expectedCount) {
        List<WebElement> list = getElements("xpath=" + inspectorGroup.getLocator() + processParameter);
        verifyEquals(list.size(), expectedCount);
    }

    /**
     * @param group - INPUT_PARAMETERS or OUTPUT_PARAMETERS
     * @param order - can be such value: "1", "2",.. or "last()" - order of ProcParameter
     * @return
     */
    public List<String> getListOfPropertiesForParameter(InspectorGroup group, String order) {
        //TODO: get rid of String and use a concrete type like Properties or some extended from ElementField
        List<String> properties = new ArrayList<>();
        List<WebElement> webElements = getElements("xpath=(" + group.getLocator() + processParam + ")[" + order + "]" + paramPropertiesLabel);
        properties.addAll(webElements.stream().filter(WebElement::isDisplayed).map(WebElement::getText).collect(Collectors.toList()));
        return properties;
    }

    /**
     * Adds a parameter (process inputs/outputs) to the specified group
     *
     * @param inspectorGroup Should be Input Parameters or Output Parameters
     * @param param          Properties of the process input/output parameters
     */
    @Step
    public ProcessPropertiesTab addProcessParameter(InspectorGroup inspectorGroup, ProcessParameter param) {
        switchToTab();
        String groupLocator = "xpath=(" + inspectorGroup.getLocator();
        expandPanelGroup(inspectorGroup);
        clickAddParameter(inspectorGroup);
        try {
            fillProcParamName(inspectorGroup, "last()", param.getName());
            selectDataTypeForProcParameter(inspectorGroup, "last()", param.getDataType());
            fillOptions(param, inspectorGroup, "last()", param.getOptions());
            fillRecordInstanceInfo(param, inspectorGroup, "last()");
            if (!inspectorGroup.equals(InspectorGroup.OUTPUT_PARAMETERS) &&
                    !(getElement(groupLocator + procParamRequired + ")[last()]").isSelected() == param.isRequired())) {
                click((groupLocator + procParamRequired + ")[last()]"));
            }
            typeKeys(groupLocator + procParamDescription + ")[last()]", param.getDescription());
            if (param.getDefaultValue() != "")
                fillDefaultValueForProcParam(inspectorGroup, param, param.getDefaultValue());
            log.info(" Added Process Parameter into group '" + inspectorGroup.getGroupName() +
                    "':\n Name=" + param.getName() + "; data type=" + param.getDataType() + "; isRequired="
                    + param.isRequired() + "; default value=" + param.getDefaultValue());
        } catch (Exception e) {
            log.error("! Could NOT add process Parameter into group" + inspectorGroup.getGroupName() + ": "
                    + param.getName() + e.getMessage());
        }
        sleep(1);
        return this;
    }

    @Step
    public ProcessPropertiesTab addPermissionEntry(ProcessParameter param) {
        switchToTab();
        InspectorGroup permission = InspectorGroup.PERMISSIONS;
        expandPanelGroup(permission);
        clickAddParameter(permission);
        try {
            String group = "xpath=(" + permission.getLocator() + permittedGroups + ")[last()]";
            String type = "xpath=(" + permission.getLocator() + permissionTypes + ")[last()]";
            selectDropDown(group, param.getPermissionGroup());
            selectDropDown(type, param.getPermissionType());
        } catch (Exception e) {
            log.error("! Could NOT add permission Entry:" + e.getMessage());
        }
        return this;
    }

    private void fillRecordInstanceInfo(ProcessParameter param, InspectorGroup inspectorGroup, String order) {
        String recordDefLocator = "xpath=(" + inspectorGroup.getLocator() + procParamRecordDef + ")[" + order + "]";
        String useSampleData = "xpath=(" + inspectorGroup.getLocator() + procParamUseSampleData + ")[" + order + "]";
        if (param.getDataType().equals(DataType.RECORD_INSTANCE)) {
            selectDropDown(recordDefLocator, param.getRecordDefinition());
            if (param.isUseSampleData() && !getAttr(useSampleData, "class").contains("__icon-check_circle")) {
                click(useSampleData);
                verifyTrue(isElementPresent(sampleRecordDefinition));
            }
            verifyFalse(isElementPresent("xpath=(" + inspectorGroup.getLocator() + procParamDefaultValue + ")[" + order + "]"));
        }
    }

    private void fillOptions(ProcessParameter param, InspectorGroup inspectorGroup, String order, String[] options) {
        if (param.getDataType().equals(DataType.SELECTION)) {
            String locator = "xpath=(" + inspectorGroup.getLocator() + procOptions + ")[" + order + "]";
            for (String option : options) {
                typeKeys(locator, option, false, false).sendKeys(Keys.ENTER);
            }
        }
    }

    public void fillProcParamName(InspectorGroup group, String order, String name) {
        typeKeys("xpath=(" + group.getLocator() + procParamName + ")[" + order + "]", name + "\n");
    }

    /**
     * Select DataType for any Proc Parameter, checking tha DataType is editable
     *
     * @param group    - INPUT_PARAMETERS or OUTPUT_PARAMETERS
     * @param order    of param - can be "1", "2",.. or "last()" - order of ProcParameter (for cases when param name is unknown) and used for other methods
     * @param dataType
     */
    public void selectDataTypeForProcParameter(InspectorGroup group, String order, DataType dataType) {
        String datTypeLocator = "xpath=(" + group.getLocator() + procParamDataType.replace("//select", ")[" + order + "]//select");
        if (isElementReadOnly(datTypeLocator)) {
            log.warn("DataType field IS READ ONLY for Proc Parameter with order" + order);
            return;
        }
        selectDropDown(datTypeLocator, dataType.getName());
    }

    /**
     * @param group     - INPUT_PARAMETERS or OUTPUT_PARAMETERS
     * @param paramName
     * @returт order of param - can be "1", "2",.. or "last()" - order of ProcParameter (for cases when param name is unknown) and used for other methods
     */
    @Step
    public String getOrderOfParamFromName(InspectorGroup group, String paramName) {
        List<ProcessParameter> allParams = getListOfProcessParams(group);
        int order = 1;
        for (ProcessParameter param : allParams) {
            if (param.getName().equals(paramName))
                return String.valueOf(order);
            order++;
        }
        return "last()";
    }

    /**
     * Finds a process input/output with the {@param paramName}
     *
     * @param group     should be InspectorGroup.INPUT_PARAMETERS or InspectorGroup.OUTPUT_PARAMETERS
     * @param paramName name of a process parameter
     * @return Data type of the process parameter with the {@param paramName} name
     */
    @Step
    public DataType getDataTypeForParameterByParamName(InspectorGroup group, String paramName) {
        String order = getOrderOfParamFromName(group, paramName);
        return getDataTypeForParamByOrder(group, order);
    }

    /**
     * Clicks on '+Add' icon in ProcessParameters group (INPUT_PARAMETERS or OUTPUT_PARAMETERS)
     *
     * @param inspectorGroup
     */
    @Step
    public void clickAddParameter(InspectorGroup inspectorGroup) {
        click(panelXpath + inspectorGroup.getLocator() + addParamIcon);
    }

    /**
     * @param group -INPUT_PARAMETERS or OUTPUT_PARAMETERS
     * @return - list of all possible DataTypes to select for Proc Parameters
     */
    public List<DataType> getListDataTypesForProcParameters(InspectorGroup group) {
        List<DataType> allDataTypes = new ArrayList<>();
        List<WebElement> webElementsOfDataTypes = getElements("xpath=(" + group.getLocator() + procParamDataType + ")[last()]/*");
        allDataTypes.addAll(webElementsOfDataTypes.stream().map(
                we -> DataType.getDataTypeByResourceType(getValue(we))).collect(Collectors.toList()));
        return allDataTypes;
    }

    /**
     * Set the needed default value for parameter (for any Data Type)
     * Value of @param defaultValue SHOULD be in required format!
     * Especially IMPORTANT for types DATE /DATE_TIME /TIME , should follow date patterns accordingly: 'yyyy-MM-dd' /'yyyy-MM-dd hh:mm ' /'hh:mm a'
     *
     * @param group        - INPUT_PARAMETERS or OUTPUT_PARAMETERS
     * @param param
     * @param defaultValue -
     */
    @Step
    public void fillDefaultValueForProcParam(InspectorGroup group, ProcessParameter param, String defaultValue) {
        if (param.getDataType().equals(DataType.PERSON))
            return;
        String paramName = param.getName();
        String order = getOrderOfParamFromName(group, paramName);
        DataType dataType = param.getDataType();
        log.info("Filling DefValue:'" + defaultValue + "' for proc Parameter:'" + paramName + "' with data type:'" + dataType);
        switch (dataType) {
            case DATE:
                fillDateFromPicker(group, order, defaultValue);
                break;
            case DATE_TIME: {
                fillDateFromPicker(group, order, defaultValue.substring(0, 10));
                fillTimeFromPicker(group, order, defaultValue.substring(11));
            }
            break;
            case DECIMAL:
            case FLOATING:
            case INTEGER:
            case TEXT:
                String inputLocator = "xpath=(" + group.getLocator() + procParamDefaultValue.replace("//input", ")[" + order + "]//input");
                typeKeys(inputLocator, defaultValue);
                break;
            case TIME:
                fillTimeFromPicker(group, order, defaultValue);
                break;
            case BOOLEAN:
            case CONNECTION_INSTANCE:
            case SELECTION:
                String selectorLocator = "xpath=(" + group.getLocator() + procParamDefaultValue.replace("//input", ")[" + order + "]//select");
                selectDropDown(selectorLocator, defaultValue);
                break;
        }
    }

    /**
     * Types date value into the LAST datePicker (input field for  data type'Date')
     *
     * @param inspectorGroup - INPUT_PARAMETERS or OUTPUT_PARAMETERS
     * @param date           - value, ONLY in SimpleDateFormat 'yyyy-MM-dd'
     */
    @Step
    public void fillDateFromPicker(InspectorGroup inspectorGroup, String date) {
        fillDateFromPicker(inspectorGroup, "last()", date);
    }

    /**
     * Types date value by ProcessParam order into datePicker (input field for  data type'Date')
     *
     * @param inspectorGroup - INPUT_PARAMETERS or OUTPUT_PARAMETERS
     * @param order          - can be "1", "2",.. or "last()" - order of ProcParameter (for cases when param name is unknown)
     * @param date           - value, ONLY in SimpleDateFormat 'yyyy-MM-dd'
     */
    @Step
    public void fillDateFromPicker(InspectorGroup inspectorGroup, String order, String date) {
        typeKeys("xpath=(" + inspectorGroup.getLocator() + datePicker.replace("//input", ")[" + order + "]//input"),
                date);
    }

    /**
     * Types time value into the LAST timePickers (2 input fields for dataType 'Time') + set needed AM/PM marker
     */
    @Step
    public void fillTimeFromPicker(InspectorGroup inspectorGroup, String time) {
        fillTimeFromPicker(inspectorGroup, "last()", time);
    }

    /**
     * Types time value by ProcessParam order into  timePickers (2 input fields for dataType 'Time') + set needed AM/PM marker
     *
     * @param inspectorGroup - INPUT_PARAMETERS or OUTPUT_PARAMETERS
     * @param order          - can be "1", "2",.. or "last()" - order of ProcParameter
     * @param time           - value, ONLY in format of 12-hours Time Pattern 'hh:mm a'
     */
    @Step
    public void fillTimeFromPicker(InspectorGroup inspectorGroup, String order, String time) {
        String hours = time.split(":")[0];
        String minutes = time.split(":")[1].split(" ")[0];
        String amPmMarker = time.split(" ")[1];
        String groupLocator = "xpath=(" + inspectorGroup.getLocator();
        String hoursLocator = groupLocator + timeHrPicker.replace("//input", ")[" + order + "]//input");
        String minutesLocator = groupLocator + timeMinPicker.replace("//input", ")[" + order + "]//input");
        String markerLocator = "xpath=" + inspectorGroup.getLocator() + timeAmPmMarker.replace(listItem, listItem + "[" + order + "]");

        try {
            typeKeys(hoursLocator, hours);
            typeKeys(minutesLocator, minutes);
            if (getValue(markerLocator) != amPmMarker)
                click(markerLocator + increaseTimeAmPmMarker);
            log.info(String.format("Time '%s' was set for new added proc %s", time, inspectorGroup.getGroupName()));
        } catch (Exception e) {
            log.error(String.format("Could NOT set Time '%s' for new added proc %s\n" + e, time, inspectorGroup.getGroupName()));
        }
    }

    /**
     * Deletes the specified process param (if it exists)
     *
     * @param inspectorGroup    Should be Input Parameters or Output Parameters
     * @param paramName
     * @param deleteParamOption the option that will be chosen on the confirmation dialog
     */
    @Step
    public void deleteProcessParam(InspectorGroup inspectorGroup, String paramName, boolean deleteParamOption) {
        switchToTab();
        if (isProcessParamPresent(inspectorGroup, paramName)) {
            clickDeleteButton(inspectorGroup, paramName);
            page.confirmModalDialog(deleteParamOption);
        }
    }

    public void clickDeleteButton(InspectorGroup inspectorGroup, String paramName) {
        List<String> parametersNames = new ArrayList<>();
        getListOfProcessParams(inspectorGroup).stream().forEach(param -> parametersNames.add(param.getName()));
        int index = parametersNames.indexOf(paramName);
        if (index != -1) {
            String pathToParam = "xpath=" + inspectorGroup.getLocator()
                    + processParam + String.format("[%s]", ++index) + deleteParamButton;
            click(pathToParam);
        }
    }

    /**
     * Checks if the parameter exists in the inspector group
     *
     * @param inspectorGroup Should be Input Parameters or Output Parameters
     * @param paramName
     * @return exists or not
     */
    @Step
    public boolean isProcessParamPresent(InspectorGroup inspectorGroup, String paramName) {
        switchToTab();
        for (int i = 0; i < getListOfProcessParams(inspectorGroup).size(); i++) {
            if (getListOfProcessParams(inspectorGroup).get(i).getName().equals(paramName)) {
                return true;
            }
        }
        log.info(inspectorGroup.getGroupName() + " group does not contain parameter with name: '" + paramName + "'.");
        return false;
    }

    /**
     * Collects process params in the specified group
     *
     * @param inspectorGroup Should be Input Parameters or Output Parameters
     * @return list of the process params
     */
    public List<ProcessParameter> getListOfProcessParams(InspectorGroup inspectorGroup) {
        switchToTab();
        int numberParams = getNumberOfProcParamsInGroup(inspectorGroup);
        List<ProcessParameter> paramsList = new ArrayList<>();
        String groupLocator = "xpath=(" + inspectorGroup.getLocator();
        for (int i = 1; i <= numberParams; i++) {
            String order = String.valueOf(i);
            String paramName = getParamNameByOrder(inspectorGroup, order);
            DataType paramType = getDataTypeForParamByOrder(inspectorGroup, order);
            boolean isParamRequired = getElement(groupLocator + procParamRequired + ")[" + order + "]").isSelected();
            String description = getValue(groupLocator + procParamDescription + ")[" + order + "]");
            String defaultValue = getDefaultValueForParamByOrder(inspectorGroup, order);
            paramsList.add(new ProcessParameter(paramName, paramType, isParamRequired, description, defaultValue));
        }
        return paramsList;
    }

    public int getNumberOfProcParamsInGroup(InspectorGroup group) {
        return getElements("xpath=" + group.getLocator() + processParam).size();
    }

    /**
     * @param group
     * @param order - can be "1", "2",.. or "last()"
     * @return
     */
    public String getParamNameByOrder(InspectorGroup group, String order) {
        return getValue("xpath=(" + group.getLocator() + procParamName + ")[" + order + "]");
    }

    /**
     * @param group - InspectorGroup
     * @param order - can be "1", "2",.. or "last()"
     * @return DateType for a particular param
     */
    public DataType getDataTypeForParamByOrder(InspectorGroup group, String order) {
        return DataType.getDataTypeByResourceType(getValue("xpath=(" + group.getLocator() + procParamDataType + ")[" + order + "]"));
    }

    /**
     * Verifies that DataType value for process parameter is non-editable
     */
    @Step
    public void verifyDataTypeReadOnly(InspectorGroup group, String order) {
        verifyTrue(isElementReadOnly("xpath=(" + group.getLocator() + procParamDataType + ")[" + order + "]"),
                "DataType is NOT read only");
    }

    /**
     * Gets full Default Value.
     * For types Date,Time,Date/Time  the DefaultValue will be in Simple DateTime format (yyyy-MM-dd hr:min AM/PM)
     *
     * @param group - InspectorGroup -(INPUT_PARAMETERS or OUTPUT_PARAMETERS)
     * @param order - can be "1", "2",.. or "last()" - order or ProcParameter (for cases when param name is unknown)
     * @return
     */
    public String getDefaultValueForParamByOrder(InspectorGroup group, String order) {
        String defLocatorForInputField = "xpath=(" + group.getLocator() + procParamDefaultValue.replace("//input", ")[" + order + "]//input");
        String defLocatorForSelectorField = "xpath=(" + group.getLocator() + procParamDefaultValue.replace("//input", ")[" + order + "]//select");
        String hrsPicker = "xpath=(" + group.getLocator() + timeHrPicker.replace("//input", ")[" + order + "]//input");
        String minsPicker = "xpath=(" + group.getLocator() + timeMinPicker.replace("//input", ")[" + order + "]//input");
        String defLocatorForTimeMarker = "xpath=" + timeAmPmMarker.replace(listItem, listItem + "[" + order + "]") + meridianInput;

        StringBuffer defaultValues = new StringBuffer();

        DataType dt = getDataTypeForParamByOrder(group, order);
        switch (dt) {
            case DATE:
            case DECIMAL:
            case FLOATING:
            case INTEGER:
            case TEXT:
                defaultValues.append(getValue(defLocatorForInputField));
                break;
            case BOOLEAN:
            case SELECTION:
                defaultValues.append(getValue(defLocatorForSelectorField));
                break;
            case DATE_TIME:
                defaultValues
                        .append(getValue("xpath=(" + group.getLocator() + datePicker.replace("//input", ")[" + order + "]//input")))
                        .append(" ")
                        .append(getValue(hrsPicker) + ":").append(getValue(minsPicker) + " ")
                        .append(getValue(defLocatorForTimeMarker));
                break;
            case TIME:
                defaultValues
                        .append(getValue(hrsPicker) + ":").append(getValue(minsPicker) + " ")
                        .append(getValue(defLocatorForTimeMarker));
                break;
        }
        return defaultValues.toString();
    }

    public void verifyProcessDefinitionEnabled(boolean b) {
        log.info("The process definition state is [" + isProcessDefinitionEnabled() + "] and expected is [" + b + "].");
        verifyEquals(isProcessDefinitionEnabled(), b);
    }

    /**
     * Get current state of the process definition.
     *
     * @return true if isEnabled = true;
     * false if isEnabled = false;
     */
    public boolean isProcessDefinitionEnabled() {
        return getAttr(enableSwitchForPD, "checked") != null;
    }

    /**
     * @param isEnabled true - process definition will be set to isEnabled = true;
     *                  false - process definition will be set to isEnabled = false;
     */
    public void setProcessDefinitionState(boolean isEnabled) {
        if (isProcessDefinitionEnabled() != isEnabled) {
            click(enableSwitchForPD);
        }
        log.info("The state of the process definition was changed to: " + isProcessDefinitionEnabled());
    }
}