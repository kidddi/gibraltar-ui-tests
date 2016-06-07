package com.bmc.gibraltar.automation.items.processdesigner;

public enum ToolbarItem {
    UNDO("//button[contains(concat('', @class, ''), 'undo')]"),
    REDO("//button[contains(concat('', @class, ''), 'redo')]"),
    ZOOMIN("//button[contains(concat('', @class, ''), 'zoom-in')]"),
    ZOOMOUT("//button[contains(concat('', @class, ''), 'zoom-out')]"),
    PRINT("//button[contains(concat('', @class, ''), 'print')]"),
    CLEAR_CANVAS("//button[contains(concat('', @class, ''), 'clear')]"),
    EXPORT_TO_PNG("//button[contains(concat('', @class, ''), 'export-to-png')]"),
    GRID_SIZE_MINUS("//button/span[contains(concat('', @class, ''), '__icon-minus_circle')]"),
    GRID_SIZE_PLUS("//button/span[contains(concat('', @class, ''), '__icon-plus_circle')]"),
    SNAPLINES("//div[contains(concat('', @class, ''), 'snaplines-checkbox')]");
    private String canvasToolbar = "xpath=//div[contains(@class, 'rx-designer-canvas-toolbar')]";
    private String iconClass;

    ToolbarItem(String iconClass) {
        this.iconClass = iconClass;
    }

    public String getIconPath() {
        return canvasToolbar + iconClass;
    }
}
