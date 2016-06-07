package com.bmc.gibraltar.automation.items.rule;

/**
 * Trigger events that can be chosen for a "Trigger" element
 */
public class TriggerEvent {
    public static final TriggerEvent ON_CREATE = new TriggerEvent("On Create");
    public static final TriggerEvent ON_UPDATE = new TriggerEvent("On Update");
    public static final TriggerEvent ON_DELETE = new TriggerEvent("On Delete");
    public static final TriggerEvent EMPTY = new TriggerEvent("");
    private String event;

    private TriggerEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}