package com.bmc.gibraltar.automation.items.component;

import com.bmc.gibraltar.automation.items.CommonEnumInterface;
import org.apache.log4j.Logger;

/**
 * Enum for working with info for Components: on UI AppDev -> View Definitions -> deployed Components.
 */
public enum Component implements CommonEnumInterface {
    RECORD_INSTANCE_EDITOR("Record Instance Editor", "rx-record-instance-editor"),
    ATTACHMENTS("Attachment Panel", "rx-attachment-panel"),
    ACTIVITY_FEED("Activity Feed", "rx-activity-feed"),
    CHARACTER_FIELD("Text Field", "rx-character-field"),
    SELECTION_FIELD("Dropdown", "rx-selection-field");
    protected static Logger log = Logger.getLogger(Component.class.getName());

    private String name;
    private String type;
    private String canvas = "xpath=//*[contains(local-name(), 'rx-view-designer-canvas')]";

    Component(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public static Component getComponentByType(String componentType) {
        for (Component component : Component.values()) {
            if (componentType.equals(component.getType()) || componentType.equals(component.getName()))
                return component;
        }
        log.error("\n Could not get Component for componentType: " + componentType);
        return null;
    }

    public static String getNameByType(String componentType) {
        return getComponentByType(componentType).getName();
    }

    public static String getTypeByName(String componentName) {
        for (Component component : Component.values()) {
            if (componentName.equals(component.getName()) || componentName.equals(component.toString()))
                return component.getType();
        }
        log.error("\n Could not get componentType for componentName: " + componentName);
        return null;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLocatorOnCanvas() {
        return String.format(canvas + "//*[contains(local-name(), '%s')]", type + "-design");
    }

    public String getHeaderLocatorOnCanvas() {
        return getLocatorOnCanvas() + "//h2";
    }

    public String getLocatorInRuntime() {
        return String.format("//*[contains(local-name(), '%s')]", type);
    }

    public String getXpathOnPalette() {
        return "xpath=//*[contains(text(),'" + name + "')]/..";
    }
}