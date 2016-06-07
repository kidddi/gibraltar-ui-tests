package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.items.CommonEnumInterface;

import java.util.HashMap;
import java.util.Map;

public enum InspectorGroup implements CommonEnumInterface {
    PROPERTIES("PROPERTIES", "properties"),
    INPUT_PARAMETERS("INPUT PARAMETERS", "inputParams"),
    OUTPUT_PARAMETERS("OUTPUT PARAMETERS", "outputParams"),
    PERMISSIONS("PERMISSIONS", "permissions"),
    GEOMETRY("GEOMETRY", "geometry"),
    INPUT_MAP("INPUT MAP", "inputMap"),
    SIGNAL_PARAMETERS("SIGNAL_PARAMETERS", "signalParams"),
    OUTPUT_MAP("OUTPUT MAP", "outputMap"),
    MULTI_INSTANCE_LOOP("MULTI INSTANCE LOOP", "multiInstance"),
    CANCEL_TASK_INPUT_MAP("CANCEL TASK INPUT MAP", "taskCancellationInputMap"),
    VISIBLE_FIELDS("VISIBLE FIELDS", "visibleFields"),
    NON("NON@", "NON@");

    private static final Map<String, InspectorGroup> lookup = new HashMap<String, InspectorGroup>();

    static {
        for (InspectorGroup gr : InspectorGroup.values())
            lookup.put(gr.getGroupName(), gr);
    }

    private String groupName;
    private String dataNameValue;

    InspectorGroup(String groupName, String dataNameValue) {
        this.groupName = groupName;
        this.dataNameValue = dataNameValue;
    }

    public static InspectorGroup get(String groupName) {
        return lookup.get(groupName);
    }

    public static boolean hasGroup(String dataNameValue) {
        for (InspectorGroup group : values()) {
            if (dataNameValue.contains(group.dataNameValue))
                return true;
        }
        return false;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getDataNameValue() {
        return dataNameValue;
    }

    public String getLocator() {
        return "//div[@data-name='" + dataNameValue + "']";
    }

    public String getName() {
        return groupName;
    }
}