package com.bmc.gibraltar.automation.items;

import com.bmc.gibraltar.automation.framework.utils.Bindings;
import com.bmc.gibraltar.automation.pages.BasePage;
import com.bmc.gibraltar.automation.pages.HasValidator;

/**
 * This is an abstract class for activated elements present on Canvas of designers (e.g.: Elements/Actions for Process Designer,
 * Components for ViewDesigner)
 */
public abstract class DesignerElement extends Bindings {
    public abstract <T extends BasePage & HasValidator> T getPage();

    /**
     * Full unique locator on corresponding Designer Canvas
     *
     * @return locator
     */
    public abstract String getXPath();
}