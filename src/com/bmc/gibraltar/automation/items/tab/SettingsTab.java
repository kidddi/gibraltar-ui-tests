package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import com.bmc.gibraltar.automation.items.element.PaletteGroup;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SettingsTab extends Tab {
    private String groupName = "//treecontrol[contains(@class, 'tree-classic')]/ul/li/div[contains(@class, 'tree-label')]";
    private String checkboxForDefault = "//div[@custom-view='defaultCustomView']/label";
    private String checkboxForFavorites = "//div[@custom-view='favoriteCustomView']/label";
    private String innerPathToGroup = "xpath=" + groupName + "//div[contains(@class, 'rx-tree-node-leaf') and .//span[.='%s']]";
    private String pathToCheckboxForElement = "//treeitem//div[.//span[.='%s']]//label[contains(@ng-class,'%s')]";
    private String checkboxForSelectingAll = "xpath=//div[@class='show-all']";
    private String solidStarIcon = "icon ng-scope __icon-star";
    private String checkedCheckbox = "icon ng-scope __icon-check_square_o";
    private String elementName = "//treecontrol/ul/li[.//div[.='%s']]//li[contains(@class, 'tree-leaf')]//span";
    private String closedGroupPath = "//treecontrol[contains(@class, 'tree-classic')]/ul/li[contains(@class, " +
            "'tree-collapsed')]/div[contains(@class, 'tree-label')]//span";

    public SettingsTab(WebDriver driver, String container, String tabLink, String panelXpath) {
        super(driver, container, tabLink, panelXpath);
    }

    @Override
    public String getGroupPath() {
        return panelXpath + groupName + "//span";
    }

    @Override
    public String getClosedGroupPath() {
        return panelXpath + closedGroupPath;
    }

    @Override
    public Set<String> getGroupsSet() {
        expandAllGroups();
        return getListOfElements(getGroupPath())
                .stream().map(group -> group.getText().toUpperCase()).collect(Collectors.toSet());
    }

    public Set<String> getElementsForGroup(PaletteGroup group) {
        String elementNames = String.format(elementName, WordUtils.capitalizeFully(group.getGroupName()));
        List<WebElement> allElements = getListOfElements(panelXpath + elementNames);
        Set<String> elementsSet = allElements.stream().filter(WebUtils::isElementDisplayed)
                .map(WebElement::getText).collect(Collectors.toSet());
        //TODO verify if the group expanded
        if (elementsSet.size() == 0) {
            log.warn("There are no elements on Palette under such group: " + group.getGroupName());
        }
        return elementsSet;
    }

    //TODO: workaround about Manage Palette "Show All" checkbox. Should be remove after defect fix. ( ||isElementPresent..)
    private boolean isCheckBoxForAllChecked(Palette tab) {
        if (tab.equals(Palette.DEFAULT_TAB)) {
            return isElementPresent(checkboxForSelectingAll + checkboxForDefault + "[@class='" + checkedCheckbox + "']")
                    || isElementPresent("xpath=" + checkboxForDefault + "[@class='" + checkedCheckbox + "']");
        } else if (tab.equals(Palette.FAVORITES_TAB)) {
            return isElementPresent(checkboxForSelectingAll + checkboxForFavorites + "[@class='" + solidStarIcon + "']");
        }
        return false;
    }

    /**
     * Select all elements to be shown in the {@param tab}
     * This method clicks on the 'All' button, that selects all elements at once.
     *
     * @param tab should be Palette.DEFAULT_TAB or Palette.FAVORITES_TAB
     * @return SettingsTab
     */
    @Step
    public SettingsTab selectAllForTab(Palette tab) {
        log.info("Selecting all groups and elements to be shown in the " + tab);
        if (!isCheckBoxForAllChecked(tab)) {
            click(checkboxToSelectAllElements(tab));
        }
        return this;
    }

    //TODO: workaround about Manage Palette "Show All" checkbox. Should be remove after defect fix. (deselectAllForTab(tab);)
    @Step
    public SettingsTab deselectAllForTab(Palette tab) {
        log.info("Deselecting all groups and elements to be not shown in the " + tab);
        if (isCheckBoxForAllChecked(tab)) {
            click(checkboxToSelectAllElements(tab));
            deselectAllForTab(tab);
        }
        return this;
    }

    @NotNull
    private String checkboxToSelectAllElements(Palette tab) {
        if (tab.equals(Palette.DEFAULT_TAB)) {
            return checkboxForSelectingAll + checkboxForDefault;
        } else if (tab.equals(Palette.FAVORITES_TAB)) {
            return checkboxForSelectingAll + checkboxForFavorites;
        }
        return "";
    }

    @Step
    public SettingsTab verifyCheckboxForAllSelected(Palette tab) {
        verifyTrue(isCheckBoxForAllChecked(tab), "The checkbox for all is shown as selected for the " + tab);
        return this;
    }

    @Step
    public SettingsTab verifyCheckboxForAllNotSelected(Palette tab) {
        verifyFalse(isCheckBoxForAllChecked(tab), "The checkbox for all is shown as NOT selected for the " + tab);
        return this;
    }

    /**
     * Verifies if the group marked to be shown in the {@param tab}
     *
     * @param tab   should be Palette.DEFAULT_TAB or Palette.FAVORITES_TAB
     * @param group palette group
     * @return true - if icon shows that group selected to be shown,
     * false - if icon not solid (selected to be not shown)
     */
    private boolean isGroupDisplayedInTab(Palette tab, PaletteGroup group) {
        if (tab.equals(Palette.DEFAULT_TAB)) {
            return isGroupDisplayed(group, checkboxForDefault, checkedCheckbox);
        } else if (tab.equals(Palette.FAVORITES_TAB)) {
            return isGroupDisplayed(group, checkboxForFavorites, solidStarIcon);
        }
        return false;
    }

    private boolean isGroupDisplayed(PaletteGroup group, String checkboxPath, String checkboxClass) {
        String pathToCheckbox = String.format(innerPathToGroup, WordUtils.capitalizeFully(group.getGroupName())) + checkboxPath;
        return waitForElementPresent(pathToCheckbox, 5) && getAttr(pathToCheckbox, "class").equals(checkboxClass);
    }

    private String getCheckboxPath(Palette tab) {
        if (tab.equals(Palette.DEFAULT_TAB)) {
            return checkboxForDefault;
        } else if (tab.equals(Palette.FAVORITES_TAB)) {
            return checkboxForFavorites;
        }
        return "";
    }

    @Step
    public SettingsTab selectGroupForTab(Palette tab, PaletteGroup[] groups) {
        selectGroups(tab, groups, false);
        return this;
    }

    @Step
    public SettingsTab deselectGroupForTab(Palette tab, PaletteGroup[] groups) {
        selectGroups(tab, groups, true);
        return this;
    }

    private void selectGroups(Palette tab, PaletteGroup[] groups, boolean isDisplayed) {
        String checkbox = getCheckboxPath(tab);
        List<WebElement> designerGroups = Arrays.asList(groups).stream()
                .filter(group -> isGroupDisplayedInTab(tab, group) == isDisplayed)
                .map(group -> getElement(String.format(innerPathToGroup,
                        WordUtils.capitalizeFully(group.getGroupName())) + checkbox)).collect(Collectors.toList());
        for (WebElement gr : designerGroups) {
            executeJS("arguments[0].scrollIntoView(true);", gr);
            gr.click();
        }
    }

    @Step
    public void verifyGroupsSelectedForTab(Palette tab, PaletteGroup[] groups) {
        Arrays.asList(groups).stream().forEach(group ->
                verifyTrue(isGroupDisplayedInTab(tab, group), group + " is not displayed in the tab:" + tab));
    }

    @Step
    public void verifyGroupsNotSelectedForTab(Palette tab, PaletteGroup[] groups) {
        Arrays.asList(groups).stream().forEach(group ->
                verifyFalse(isGroupDisplayedInTab(tab, group), group + " is displayed in the tab:" + tab));
    }

    private boolean isElementDisplayed(String element, String tabName, String className) {
        String elementInTab = "xpath=" + String.format(pathToCheckboxForElement, element, tabName);
        return waitForElementPresent(elementInTab, 5) && getAttr(elementInTab, "class").equals(className);
    }

    private boolean isElementDisplayedInTab(Palette tab, String element) {
        return (tab.equals(Palette.DEFAULT_TAB) || tab.equals(Palette.FAVORITES_TAB)) && isElementDisplayed(element, tab.getClassName(), checkedCheckbox);
    }

    @Step
    public void verifyElementsSelectedForTab(Palette tab, String[] elements) {
        Arrays.asList(elements).stream().forEach(el -> verifyTrue(isElementDisplayedInTab(tab, el),
                el + " is not present in the tab: " + tab));
    }

    @Step
    public void verifyElementsNotSelectedForTab(Palette tab, String[] elements) {
        Arrays.asList(elements).stream().forEach(el -> verifyFalse(isElementDisplayedInTab(tab, el),
                el + " is present in the tab: " + tab));
    }

    private String pathToElementCheckbox(String elName, String tabName) {
        return "xpath=" + String.format(pathToCheckboxForElement, elName, tabName);
    }

    @Step
    public SettingsTab selectElementForTab(Palette tab, String[] elements) {
        log.info("Selecting " + Arrays.asList(elements) + " elements to be shown in the " + tab);
        Arrays.asList(elements).stream()
                .filter(el -> !isElementDisplayedInTab(tab, el))
                .forEach(el -> click(pathToElementCheckbox(el, tab.getClassName())));
        return this;
    }

    @Step
    public SettingsTab deselectElementForTab(Palette tab, String[] elements) {
        log.info("Selecting " + Arrays.asList(elements) + " elements to be NOT shown in the " + tab);
        Arrays.asList(elements).stream()
                .filter(el -> isElementDisplayedInTab(tab, el))
                .forEach(el -> click(pathToElementCheckbox(el, tab.getClassName())));
        return this;
    }
}