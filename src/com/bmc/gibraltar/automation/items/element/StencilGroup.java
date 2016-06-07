package com.bmc.gibraltar.automation.items.element;

/**
 * In order to unite all groups that are present in the Stencil of Designers:
 * Process Designer, Rule Designer, View Designer
 */
public interface StencilGroup {
    String getGroupName();

    String getExpandedGroupPath();
}