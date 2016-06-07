package com.bmc.gibraltar.automation.items.element;

import com.bmc.gibraltar.automation.items.parameter.ProcessParameter;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;

import java.util.ArrayList;
import java.util.List;

public class ContainerProperty {

    public InspectorGroup groupName;
    public String fieldName;
    public String TextValue;
    public boolean isRequired;
    List<ProcessParameter> parametersMap;
    private List<Object> params = new ArrayList<Object>();

    public ContainerProperty() {
    }

    /**
     * Constructor, get Objects array and assignes them to cerain sequence of fields.
     *
     * @param args The sequence: groupName(String), fieldName(String), TextValue(String), isRequired(boolean)
     */
    public ContainerProperty(Object... args) {
        try {
            groupName = (InspectorGroup) args[0];
            fieldName = "" + args[1];
            TextValue = "" + args[2];
            isRequired = (Boolean) args[3];
        } catch (Exception e) {
            // System.out.println("!!!2" + e.getStackTrace());
        }
    }

    public InspectorGroup getGroupName() {
        return groupName;
    }

    public void setGroupName(InspectorGroup groupName) {
        this.groupName = groupName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getTextValue() {
        return TextValue;
    }

    public void setTextValue(String textValue) {
        TextValue = textValue;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public List<ProcessParameter> getParametersMap() {
        return parametersMap;
    }

    public void setParametersMap(List<ProcessParameter> parametersMap) {
        this.parametersMap = parametersMap;
    }
}
