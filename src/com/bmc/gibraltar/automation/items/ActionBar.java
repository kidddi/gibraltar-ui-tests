package com.bmc.gibraltar.automation.items;

import com.bmc.gibraltar.automation.framework.utils.Bindings;
import com.bmc.gibraltar.automation.pages.Locators;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

/**
 * Action Bar on every Tab pg
 */
public class ActionBar extends Bindings {

    private Action action;

    public ActionBar(WebDriver driver) {
        wd = driver;
    }

    @Step
    public void verifyPresent(Action... actions) {
        Arrays.stream(actions)
                .forEach(i -> verifyTrue(isElementPresent(i.locator), format("Element %s is not present", i.name)));
    }

    /**
     * Example: actonBar(FILTER).present()
     */
    @Step
    public boolean isActionPresent() {
        return isElementPresent(action.locator);
    }

    /**
     * Allows to switch betwene actions, without initiating a new instance.
     * Example:
     * forAction(COPY).verifyEnabled()
     * .forAction(DELETE).verifyEnabled()
     */
    public ActionBar forAction(Action action) {
        this.action = action;
        return this;
    }

    public boolean isActionEnabled() {
        return getElement(action.locator).isEnabled();
    }

    /**
     * Waits for current action.
     * example: actionBar(NEW).waitForActionPresent(5000).click();
     *
     * @param timeOut in milliseconds
     */
    public ActionBar waitForActionPresent(int timeOut) {
        waitForElementPresent(action.locator, timeOut);
        return this;
    }

    @Step
    public ActionBar verifyActionEnabled() {
        verifyTrue(isActionEnabled(), format("Button %s must be ENABLED, but it Disabled", action.name));
        return this;
    }

    @Step
    public ActionBar verifyActionDisabled() {
        verifyTrue(!isActionEnabled(), format("Button %s must be DISABLED, but it Enabled", action.name));
        return this;
    }

    /**
     * Click on current action.
     * example: openProcessStart(String name) { actionBar(START).click(); }
     */
    @Step
    public ActionBar click() {
        click(action.locator);
        return this;
    }

    public Filter getFilter() {
        return new Filter();
    }

    public enum Action {
        RESTORE_TO_ORIGINAL("Restore to Original"),
        DELETE("Delete"),
        COPY("Copy"),
        NEW("New"),
        GRID("grid"),
        CARD_GRID("card-grid"),
        CARD_LIST("card-list"),
        REFRESH("refresh"),
        START("Start"),
        /**
         * Manage Processes tab
         */
        FILTER("Filter"),
        /**
         * Process Definitions tab
         */
        IMPORT("Import"),
        /**
         * Tasks tab
         */
        DISMISS_ALL("Dismiss All"),
        /**
         * Messages tab
         */
        DISMISS_SELECTED("Dismiss Selected");

        private static final String actionLocator = "xpath=//div[contains(@class,'toolbar')]//a[.='%s'] | //button[contains(@class,'rx-standard-action-%s')]";

        static {
            Arrays.stream(Action.values()).forEach(a -> a.locator = format(actionLocator, a.name, a.name));
        }

        public String locator;
        private String name;

        Action(String name) {
            this.name = name;
        }
    }

    /**
     * class Filter implements a functionality for Tab toolbar`s item - Filter.
     * How to use:
     * 1) createFilter()
     * 2) call variable filter
     * 3) expandFilter()
     * 4) expandOwner()
     * 5) select owner or clear all
     */
    public class Filter implements Locators {
        String filterButton = "xpath=//a[contains(., '%s')][@ng-click='dropdownToggle()']";
        String clearAll = getLast("xpath=//div[@class='d-dropdown-menu']//div[contains(., 'Clear All')]//a");
        String dropdownMenu = getLast("xpath=//div[@class='d-dropdown-menu']//a[contains(., '%s')]");
        String allOwners = "xpath=//a[contains(@class, 'd-dropdown-menu-options-list-item-option ng-binding')]";
        private String currentFilter = "Filter";

        @Step
        public List<String> getAllOwners() {
            return getListOfWebElementsTextByLocator(allOwners);
        }

        public Filter clickFilter() {
            click(format(filterButton, currentFilter));
            return this;
        }

        public Filter expandOwner() {
            click(format(dropdownMenu, "Owner"));
            return this;
        }

        public Filter select(String owner) {
            click(format(dropdownMenu, owner));
            currentFilter = "Owner: " + owner;
            return this;
        }

        public Filter selectAll() {
            getAllOwners().forEach(this::select);
            return this;
        }

        public Filter clearAll() {
            click(clearAll);
            currentFilter = "Filter";
            return this;
        }
    }
}
