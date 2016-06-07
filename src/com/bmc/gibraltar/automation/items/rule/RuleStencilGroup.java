package com.bmc.gibraltar.automation.items.rule;

import com.bmc.gibraltar.automation.items.element.StencilGroup;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes groups in the Palette in the Rule Designer
 */
public enum RuleStencilGroup implements StencilGroup {
    QUALIFICATION("Qualification"),
    APPROVALS("Approvals"),
    BASECAMP("Basecamp"),
    EXPRESSIONS("Expressions"),
    RECORDS("Records"),
    TASKS("Tasks"),
    USER_MESSAGES("User Message"),
    PLATFORM_ACTIONS("Platform Actions"),
    ASSOCIATION("Association");

    private static final Map<String, RuleStencilGroup> lookup = new HashMap<>();
    private static String groupLocator = "//div[@class='group']/h3[.='%s'][contains(@class,'group-label')]";

    static {
        for (RuleStencilGroup gr : RuleStencilGroup.values())
            lookup.put(gr.getGroupName(), gr);
    }

    private String groupName;

    RuleStencilGroup(String groupName) {
        this.groupName = groupName;
    }

    public static RuleStencilGroup get(String groupName) {
        return lookup.get(groupName);
    }

    public String getGroupName() {
        return groupName;
    }

    public String getExpandedGroupPath() {
        return format(groupLocator, WordUtils.capitalizeFully(groupName));
    }
}