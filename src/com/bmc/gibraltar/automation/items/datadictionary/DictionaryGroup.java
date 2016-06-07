package com.bmc.gibraltar.automation.items.datadictionary;

import java.util.HashMap;
import java.util.Map;

public enum DictionaryGroup {

    OPTIONS("Options"),
    PROCESS_VARIABLES("Process Variables"),
    //  GENERAL("General"),
    ACTIVITIES("Activities"),
    COMPONENTS("Components"),
    VIEW_PARAMETERS("View Parameters");

    private static final Map<String, DictionaryGroup> lookup = new HashMap<>();

    static {
        for (DictionaryGroup gr : DictionaryGroup.values())
            lookup.put(gr.getName(), gr);
    }

    private String name;

    DictionaryGroup(String name) {
        this.name = name;
    }

    public static DictionaryGroup get(String groupName) {
        return lookup.get(groupName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return String.format("xpath=//treecontrol//li[.//label[.='%s']]", getName());
    }

    public enum GENERAL {

        CURRENT_USER("Current User", "$USER$"),
        CURRENT_DATE("Current Date", "$DATE$"),
        CURRENT_TIME("Current Time", "$TIME$"),
        CURRENT_DATE_AND_TIME("Current Date and Time", "$TIMESTAMP$"),
        PROCESS_CORRELATION_ID("Process Correlation ID", "$PROCESSCORRELATIONID$");

        public String name;
        public String value;

        GENERAL(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Legend {
        /**
         * Description from https://bmc.invisionapp.com/share/8W38GNQR4#/screens/84919085
         * <p/>
         * PROCESS_INPUT_PARAMETER - var, created by adding an Input parameter in the Process
         * PROCESS_OUTPUT_PARAMETER - var, created by adding an Output parameter in the Process
         * OUTPUT_MAP_VARIABLE - existed "Output" variable, is located in some elements (Activity type)
         * KEYWORD - existed default variables from "GENERAL" group
         * <p/>
         * Examples:  dd.doubleClick{Legend.PROCESS_INPUT_PARAMETER, "aaa"); dd.dragAndDrop{Legend.PROCESS_OUTPUT_PARAMETER, "aaa");
         */
        PROCESS_INPUT_PARAMETER("icon __icon-arrow_right_square_input"),
        PROCESS_OUTPUT_PARAMETER("icon __icon-arrow_right_square_o"),
        OUTPUT_MAP_VARIABLE("icon __icon-arrows_right"),
        ACTIVITY_OUTPUT("icon __icon-arrow_chart"),
        KEYWORD("icon __icon-dollar");

        public String icon;

        Legend(String icon) {
            this.icon = icon;
        }

        public String getIconClass() {
            return icon;
        }
    }
}
