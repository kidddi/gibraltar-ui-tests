package com.bmc.gibraltar.automation.items.tab;

/**
 * Describes tabs properties (order in Palette, link to tab and path to tab)
 */
public enum Palette {
    /*For Process Designer:*/
    DEFAULT_TAB(1, "default", "default", "[stencil-configuration=\"stencilConfiguration.default\"] div.content"),
    FAVORITES_TAB(2, "favorite", "favorite", "[stencil-configuration=\"stencilConfiguration.favorite\"] div.content"),
    RECENT_TAB(3, "recent", "recent", ".recentStencil div.content"),
    SETTINGS_TAB(4, "rx-blade-stencil-tab-settings", "rx-designer-stencil-settings", "[stencil-configuration=\"stencilConfiguration.settings\"]"),
    /*For View Designer:*/
    DEFAULT_VIEW_DESIGNER_TAB(1, "rx-blade-tab", "rx-view-designer-stencil", "");

    public final String cssPath;
    private int orderInPalette;
    private String tabLink;
    private String tabPath;

    Palette(int orderNumber, String tabLink, String tabPath, String cssPath) {
        this.orderInPalette = orderNumber;
        this.tabLink = tabLink;
        this.tabPath = tabPath;
        this.cssPath = cssPath;
    }

    public int getOrderInPalette() {
        return orderInPalette;
    }

    public String getClassName() {
        return tabLink;
    }

    public String getTabLink() {
        return "//a[" + orderInPalette + "][contains(concat('', @class, ''), '" + tabLink + "')]";
    }

    public void setTabLink(String tabLink) {
        this.tabLink = tabLink;
    }

    /**
     * @return locator to Palette's tab ( looks as a bookmark)
     */
    public String getTabPath() {
        switch (this) {
            case SETTINGS_TAB:
            case DEFAULT_VIEW_DESIGNER_TAB:
                return "xpath=//" + tabPath;
            default:
                return "xpath=//*[@stencil-configuration='stencilConfiguration." + tabPath + "']";
        }
    }

    public void setTabPath(String tabPath) {
        this.tabPath = tabPath;
    }
}