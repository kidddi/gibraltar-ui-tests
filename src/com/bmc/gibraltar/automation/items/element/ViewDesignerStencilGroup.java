package com.bmc.gibraltar.automation.items.element;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Groups present in the View Designer in Palette (Stencils, the left blade).
 */
public enum ViewDesignerStencilGroup implements StencilGroup {
    BASIC_COMPONENTS("Basic Components"),
    DEMO_COMPONENTS("Demo Components");
    private static String groupLocator = "//div[@class='group']/h3[.='%s'][contains(@class,'group-label')]";
    private String groupName;

    ViewDesignerStencilGroup(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getExpandedGroupPath() {
        return format(groupLocator, WordUtils.capitalizeFully(groupName));
    }
}