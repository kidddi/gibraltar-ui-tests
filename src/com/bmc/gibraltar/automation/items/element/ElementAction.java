package com.bmc.gibraltar.automation.items.element;

import com.bmc.gibraltar.automation.items.tab.Palette;

/**
 * In order to unite all elements present in Palette of Designers:
 * Process Designer, Rule Designer, View Designer
 */
//TODO: rename to ElementOfDesigner, when enum ElementOfDesigner renamed to ProcessElement
public interface ElementAction {
    String getName();

    String getNameOnInspector();

    StencilGroup getPaletteGroup();

    String getXpathOnPalette();

    String getXpathOnPalette(Palette palette);

    String getType();

    String locatorOnCanvas();
}