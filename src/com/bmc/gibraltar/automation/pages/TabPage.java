package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.items.ActionBar;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Optional;

import static com.bmc.gibraltar.automation.items.ActionBar.Action;
import static java.lang.String.format;

public abstract class TabPage extends BasePage {
    protected static String rowLocator = "xpath=//div[contains(@class, 'ngCellText')][contains(., '%s')]";
    protected static String checkboxForRow = "xpath=//label[contains(concat(' ', @class, ' '), 'cellSelectionLabel') " +
            "and ancestor-or-self::div[contains(@class, 'ng-scope ngRow')]//a[.='%s']]";
    private static String headerRowLocator = "xpath=//div[contains(@class, 'ngHeaderText')][contains(., '%s')]";
    private static String tabNameLocator = "xpath=//h1[text()='%s']";
    protected String tabName = "TabPage superclass";
    protected String bundle;
    private ActionBar actionBar;

    protected TabPage(WebDriver driver) {
        super(driver);
    }

    public TabPage(WebDriver driver, String bundle) {
        super(driver);
        this.bundle = bundle;
    }

    public String getTabName() {
        return tabName;
    }

    @Override
    public boolean isPageLoaded() {
        boolean loaded = isElementPresent(format(tabNameLocator, tabName));
        if (loaded) {
            log.info(tabName + " is loaded");
        } else {
            log.info(tabName + "  - not  loaded");
        }
        return loaded;
    }

    /**
     * @param cellName Example some definition name.
     *                 Test sample: getCellLocator("Request Approval")
     * @return a cell locator (from the table) by cell name.
     */
    public String getRowLocator(String cellName) {
        return getLast(format(rowLocator, cellName));
    }

    /**
     * Easy way to get Action Bar and use it`s services. This logic save time on initialization process.
     *
     * @param itemBar any instance from ActionBar.Action enum.
     */
    public ActionBar actionBar(Action itemBar) {
        actionBar = Optional.ofNullable(actionBar).orElse(new ActionBar(wd));
        return actionBar.forAction(itemBar);
    }

    public ActionBar actionBar() {
        return actionBar = Optional.ofNullable(actionBar).orElse(new ActionBar(wd));
    }

    protected boolean isRowSelected(String rowName) {
        String checkBoxLocator = String.format(checkboxForRow, rowName);
        return getElement(checkBoxLocator).getAttribute("class").contains("check_circle");
    }

    protected void selectRow(String rowName) {
        log.info(format("Selected %s in %s.", rowName, tabName));
        if (!isRowSelected(rowName)) {
            String checkBoxLocator = format(checkboxForRow, rowName);
            click(checkBoxLocator);
        }
    }

    protected boolean isRowExistent(String rowName) {
        return isElementPresent(getRowLocator(rowName));
    }

    /**
     * Clicks on header of column.
     *
     * @param columnName Name of column
     */
    @Step
    public void sortTableBy(String columnName) {
        click(format(headerRowLocator, columnName));
    }
}
