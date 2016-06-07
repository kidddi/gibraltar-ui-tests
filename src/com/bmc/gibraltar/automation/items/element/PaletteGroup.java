package com.bmc.gibraltar.automation.items.element;

import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes groups in the Palette
 */
public enum PaletteGroup implements StencilGroup {
    /* Process Designer */
    EVENT("EVENTS"),
    ACTIVITY("ACTIVITIES"),
    APPROVAL("APPROVALS"),
    BASECAMP("BASECAMP"),
    TASKS("TASKS"),
    USER_MESSAGES("USER MESSAGE"),
    GATEWAY("GATEWAYS"),
    ANNOTATIONS("ANNOTATIONS"),
    ASSOCIATION("ASSOCIATION"),
    EXPRESSIONS("EXPRESSIONS"),
    RECORDS("RECORDS");

    private static final Map<String, PaletteGroup> lookup = new HashMap<>();
    private static String groupLocator = "//div[@class='group']/h3[.='%s'][contains(@class,'group-label')]";

    static {
        for (PaletteGroup gr : PaletteGroup.values())
            lookup.put(gr.getGroupName(), gr);
    }

    private String groupName;

    PaletteGroup(String groupName) {
        this.groupName = groupName;
    }

    public static PaletteGroup get(String groupName) {
        return lookup.get(groupName);
    }

    public String getGroupName() {
        return groupName;
    }

    public String getExpandedGroupPath() {
        return format(groupLocator, WordUtils.capitalizeFully(groupName));
    }
}