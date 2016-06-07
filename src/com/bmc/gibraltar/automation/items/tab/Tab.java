package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.framework.utils.Bindings;
import com.bmc.gibraltar.automation.items.element.PaletteGroup;
import com.bmc.gibraltar.automation.items.element.StencilGroup;
import org.apache.commons.lang3.text.WordUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Tab extends Bindings {
    protected static String palettePanelContainer = "xpath=//div[contains(concat(' ', @class, ' '), 'rx-blade-dock-left')]";
    protected String panelXpath;
    protected String container;
    protected String tabLink;
    private String groupPath = "//div[contains(@class,'group') and not (contains (@class,'closed'))]" +
            "/*[contains(@class,'group-label') or contains(@class,'group-title')]";
    private String closedGroupPath = "//div[contains(@class,'group') and " +
            "(contains (@class,'closed'))]/*[contains(@class,'group-label') or contains(@class,'group-title')]";

    public Tab(WebDriver driver, String container, String tabLink, String panelXpath) {
        this.container = container;
        this.tabLink = tabLink;
        this.panelXpath = panelXpath;
        wd = driver;
    }

    /**
     * This part describes methods that will work with a Panel (container) that contains tabs
     */
    public boolean isPanelActive() {
        return getElement(container).getAttribute("class").contains("rx-blade-expanded");
    }

    @Step
    public void verifyPanelIsExpanded() {
        log.info("Verifying if the Blade is expanded.");
        verifyTrue(isPanelActive());
    }

    @Step
    public void expandPanel() {
        if (!isPanelActive()) {
            click(container + tabLink);
        }
    }

    @Step
    public void collapsePanel() {
        switchToTab();
        click(container + tabLink);
    }

    /**
     * This part describes methods that will work with a tab
     */
    public boolean isTabActive() {
        return getElement(container + tabLink).getAttribute("class").contains("active");
    }

    public void verifyTabActive() {
        log.info("Verifying if the tab is active.");
        verifyTrue(isTabActive());
    }

    public void switchToTab() {
        if (!isTabActive()) {
            click(container + tabLink);
        }
    }

    //TODO: Make Tab class as Generic
    public Tab switchToTab(Palette tab) {
        click(tab.getTabPath());
        return this;
    }

    /**
     * This part describes methods that will work with groups in the tab
     */
    protected String getGroupPath() {
        return panelXpath + groupPath;
    }

    protected String getClosedGroupPath() {
        return panelXpath + closedGroupPath;
    }

    /**
     * This method collects all groups presented in the Tab
     *
     * @return set of groups
     */
    // TODO: remove this method and use getGroupsList instead
    protected Set<String> getGroupsSet() {
        return getGroupsList().stream().collect(Collectors.toSet());
    }

    public List<String> getGroupsList() {
        expandAllGroups();
        return getListOfElements(getGroupPath()).stream()
                .filter(WebElement::isDisplayed).map(WebElement::getText)
                .map(String::toUpperCase).collect(Collectors.toList());
    }

    @Step
    public List<InspectorGroup> getInspectorGroupsList() {
        expandAllGroups();
        return getListOfElements(getGroupPath())
                .stream().map(we -> InspectorGroup.get(we.getText())).collect(Collectors.toList());
    }

    public int getGroupsCount() {
        return getGroupsSet().size();
    }

    @Step
    public void verifyGroupsPresence(StencilGroup[] groups) {
        Set<String> groupsInInspector = getGroupsSet();
        Arrays.asList(groups).stream().forEach(group -> verifyTrue(groupsInInspector.contains(group.getGroupName().toUpperCase()),
                "Group " + group + " is not present!"));
    }

    @Step
    public void verifyGroupsPresence(InspectorGroup[] groups) {
        log.info("Verifying that " + Arrays.asList(groups) + " groups are present.");
        Set<String> groupsInInspector = getGroupsSet();
        Arrays.asList(groups).stream().forEach(group -> verifyTrue(groupsInInspector.contains(group.getGroupName()),
                "Group " + group + " is not present!"));
    }

    @Step
    public void expandAllGroups() {
        getElements(getClosedGroupPath()).forEach(this::click);
    }

    @Step
    public void collapseAllGroups() {
        getElements(getGroupPath()).forEach(this::click);
    }

    @Step
    public void expandPanelGroup(InspectorGroup inspectorGroup) {
        String collapsedGroupXpath = getClosedGroupPath() + "[.='"
                + WordUtils.capitalizeFully(inspectorGroup.getGroupName()) + "']";
        if (waitForElementPresent(collapsedGroupXpath, 2)) {
            click(collapsedGroupXpath);
        }
    }

    @Step
    public void expandPanelGroup(PaletteGroup paletteGroup) {
        String collapsedGroupXpath = getClosedGroupPath() + "[.='"
                + WordUtils.capitalizeFully(paletteGroup.getGroupName()) + "']";
        if (isElementPresent(collapsedGroupXpath))
            click(collapsedGroupXpath);
        waitForElementNotPresent(collapsedGroupXpath);
    }

    @Override
    //TODO: check where it's used and remove this method
    protected WebElement typeKeys(String locator, String value) {
        return super.typeKeys(this.getElement(locator), value + "\n");
    }
}