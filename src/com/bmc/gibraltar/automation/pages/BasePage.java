package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.framework.utils.Bindings;
import com.bmc.gibraltar.automation.framework.utils.PropertiesUtils;
import com.bmc.gibraltar.automation.framework.utils.web.AngularJs;
import com.bmc.gibraltar.automation.items.Icon;
import com.jayway.awaitility.Awaitility;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class BasePage extends Bindings implements Locators {
    public final static int SHORT_TIMEOUT = 10;
    public final static int LONG_TIMEOUT = 30;
    public final String TASK_MANAGER_URL = PropertiesUtils.getAppServerUrl() + "/task-manager/index.html#";
    public final String APP_MANAGER_URL = PropertiesUtils.getAppServerUrl() + "/application-manager/index.html#";
    protected String presentDialogMessage = "xpath=//*[contains(@class,'modal-body') " +
            "or contains(@class,'content-item')]";
    protected String headerProfileArea = "xpath=//div[contains(@class,'d-n-actions')]";
    protected String logoutLink = "xpath=//a[@ng-click='logout()']";
    protected String modalDialog = "xpath=//div[@class='modal-content' and .//h5[contains(text(), 'Warning')]]";
    protected String errorMessage = "xpath=//div[contains(@class,'alert-error')]//div[@ng-bind='message.text']";
    protected String successMessage = "xpath=//div[contains(@class,'alert-success')]//div[@ng-bind='message.text']";
    protected String infoMessage = "xpath=//div[contains(@class,'alert-info')]//div[@ng-bind='message.text']" +
            "[contains(.,'%s')]";
    protected String loadingSpinner = "xpath=//div[contains(@class, 'cg-busy-animation')]";
    private String closeErrorButton = "/../..//button[@class='close']";
    private String closeErrorWithSpecificMessageButton = "[contains(.,'%s')]" + closeErrorButton;
    private String confirmChoiceOnDialog = "//button[.='OK' or .='Yes']";
    private String declineChoiceOnDialog = "//button[.='Cancel' or .='No']";
    private String columnHeaderText = "xpath=//div[contains(@class, 'ngHeaderText ng-binding colt')]";

    protected BasePage(WebDriver driver) {
        wd = driver;
        PageFactory.initElements(driver, this);
    }

    /* HELPER METHODS */
    public boolean isElementPresent(String locator) {
        return super.isElementPresent(locator);
    }

    public WebElement typeKeysWithEnter(String locator, String value) {
        return super.typeKeys(locator, value + "\n");
    }

    public WebElement typeKeysWithNoEnter(String locator, String value) {
        return super.typeKeys(locator, value);
    }

    public WebElement getElement(String locator) {
        return super.getElement(locator);
    }

    /**
     * Types keys into field and, if first attempt was not successful, types keys once more
     */
    public void typeKeysBySure(String locator, String value) {
        typeKeysWithEnter(locator, value);
        if (!getValue(locator).equals(value))
            typeKeysWithEnter(locator, value);
    }

    public void click(String locator) {
        super.click(locator);
    }

    public void click(WebElement locator) {
        super.click(locator);
    }

    public String randomString() {
        return RandomStringUtils.randomAlphanumeric(16).toUpperCase();
    }

    public String getPageName() {
        return this.getClass().getSimpleName();
    }

    protected abstract boolean isPageLoaded();

    //TODO remove usage in tests and make protected
    public final void waitForPageLoaded() {
        AngularJs.waitForAngularRequestsToFinish(wd);
        Awaitility.await("'" + getClass().getSimpleName() + "' page is not loaded")
                .atMost(LONG_TIMEOUT, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(this::isPageLoaded);
    }

    /**
     * Navigates in browser to any page by know getPageUrl() for that page
     * and assures that page is loaded by waitForPageLoaded(), which is different for each page
     *
     * @return BasePage
     */
    @Step
    public final <T extends BasePage> T navigateToPage() {
        if (isPageLoaded()) {
            log.info("Page '" + getClass().getSimpleName() + "' is already loaded");
            return (T) this;
        }
        String pageUrl = getPageUrl();
        if (wd.getCurrentUrl().equals(pageUrl)) {
            log.info(String.format("URL %s is already loaded. Refreshing page", pageUrl));
            wd.navigate().refresh();
        } else {
            log.info("Loading URL: " + pageUrl);
            wd.get(pageUrl);
        }
        alertAcceptIfPresent();
        waitForPageLoaded();
        log.info("Navigated to '" + getClass().getSimpleName() + "' page");
        return (T) this;
    }

    /**
     * By default returns current URL.
     * But it's overridden in some child pages, to return predefined URL of the page.
     *
     * @return current page URL
     */
    // TODO make method abstract
    protected String getPageUrl() {
        throw new RuntimeException("There is no defined url for this page.");
    }

    /**
     * Chooses "Yes" or "No" option in the confirmation dialog, if argument 'confirm' - true or false accordingly
     *
     * @param confirm should be true or false
     */
    @Step
    public void confirmModalDialog(boolean confirm) {
        String choice;
        if (confirm) {
            choice = confirmChoiceOnDialog;
        } else {
            choice = declineChoiceOnDialog;
        }
        if (isModalDialogPresent()) {
            click(modalDialog + choice);
            waitForElementNotPresent(modalDialog, 2);
        }
    }

    /**
     * Checks if any Modal Dialog is displayed on page
     */
    public boolean isModalDialogPresent() {
        return waitForElementPresent(modalDialog, 3);
    }

    /**
     * Verifies if a modal dialog with message 'expectedMsgOnDialog' is/not displayed (depends on argument 'isExpected')
     *
     * @param expectedMessageOnDialog message that should be present on the modal dialog
     * @param isExpected              true - should be present, false - text should not be present
     */
    @Step
    public BasePage verifyModalDialog(String expectedMessageOnDialog, boolean isExpected) {
        try {
            waitForElementPresent(presentDialogMessage, 5);
            String actualTextMsgOnDialog = getText(presentDialogMessage);
            if (isExpected) {
                verifyContains(actualTextMsgOnDialog, expectedMessageOnDialog);
            } else {
                verifyFalse(actualTextMsgOnDialog.contains(expectedMessageOnDialog));
            }
        } catch (NoSuchElementException e) {
            log.error("No modal dialog found:" + e);
        }
        return this;
    }

    public boolean isErrorMessageDisplayed() {
        return waitForElementPresent(errorMessage, 10);
    }

    public boolean isErrorMessageDisplayed(String errorMessageText) {
        return waitForElementPresent(errorMessage + "[contains(.,'" + errorMessageText + "')]", 5);
    }

    @Step
    public void closeAllErrorAlerts() {
        getElements(errorMessage + closeErrorButton).stream().forEach(this::click);
    }

    @Step
    public void closeErrorAlert(String message) {
        if (isErrorMessageDisplayed(message))
            click(errorMessage + String.format(closeErrorWithSpecificMessageButton, message));
    }

    public boolean isSucessMessageDisplayed(String successMessageText) {
        return waitForElementPresent(successMessage + "[contains(.,'" + successMessageText + "')]", 5);
    }

    @Step
    public void verifyNoErrorAppears() {
        verifyFalse(isErrorMessageDisplayed(), "Such errors are present on Page: " + getAllErrorMessages());
    }

    //TODO: Consider Hamcrest usage.
    @SafeVarargs
    public final void verifyEquals(List actual, List expected, String... extraMsg) {
        String add = extraMsg == null || extraMsg.length == 0 ? "Nothing" : Arrays.toString(extraMsg);
        verifyTrue(actual.containsAll(expected) && actual.size() == expected.size(),
                String.format("Actual: %s. Expected: %s. Additional information: %s",
                        actual.toString(), expected.toString(), add));
    }

    @Step
    public void verifyErrorMessagesAppear(String errorMessageText) {
        verifyTrue(isErrorMessageDisplayed(errorMessageText));
    }

    @Step
    public void verifyInfoMessagesAppear(String infoMessageText) {
        verifyTrue(isElementPresent(String.format(infoMessage, infoMessageText)));
    }

    @Step
    public void verifyErrorMessagesAppear() {
        verifyTrue(isErrorMessageDisplayed());
    }

    public List<String> getAllErrorMessages() {
        return getElements(errorMessage).stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public String getValue(String locator) {
        String select = getElement(locator).getTagName();
        if (select.startsWith("select")) {
            return getSelect(getElement(locator)).getFirstSelectedOption().getText();
        } else {
            return getAttr(locator, "value");
        }
    }

    @Step
    public List<String> getColumnNames() {
        return getListOfWebElementsTextByLocator(columnHeaderText);
    }

    public Object executeJS(String script) {
        return super.executeJS(script);
    }

    public void alertAcceptIfPresent() {
        if (isAlertPresent()) {
            accept();
        }
    }

    private void accept() {
        try {
            Alert alert = wd.switchTo().alert();
            alert.accept();
        } catch (Exception e) {
            takeScreenshot();
            log.warn("Problems with page alert! See screen");
        }
    }

    public boolean isAlertPresent() {
        try {
            wd.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    @Step
    public List<String> getListOfWebElementsAttribute(String locator, String attribute) {
        return getListOfElements(locator).stream().map(we -> we.getAttribute(attribute)).collect(Collectors.toList());
    }

    /**
     * Verifies fields values are auto-completed for Drop Downs, where we can see all options.
     * NOT applicable for autocomplete of non drop-down fields.
     *
     * @param selectItemsPath - locator of results list , which must be shown as a dropDow
     *                        and also a fewer options after matching with already typed symbols.
     * @param locator         -  locator of field where will start typing(e.g. select type, with "Autocomplete" function).
     */
    @Step
    public void verifyAutoCompleteForDropDowns(String locator, String selectItemsPath) {
        WebElement field = getElement(locator);
        if (!isElementPresent(selectItemsPath))
            field.click();
        List<String> dropDownList = getListOfWebElementsTextByLocator(selectItemsPath);
        for (String var : dropDownList) {
            String temp = "";
            for (Character button : var.toCharArray()) {
                typeKeys(locator, "" + button, false, false);
                temp += button;
                final String expSequence = temp;
                List<String> expected = dropDownList.stream().filter(s ->
                        s.toLowerCase().contains(expSequence.toLowerCase())).collect(Collectors.toList());
                List<String> actual = getListOfWebElementsTextByLocator(selectItemsPath);
                verifyEquals(actual, expected);
            }
            field.clear();
        }
    }

    /**
     * Every page can call Icon class and use icon`s services.
     *
     * @param anyIconLocator any locator: css, xpath, ....
     */
    public Icon forIcon(String anyIconLocator) {
        return new Icon(anyIconLocator, wd);
    }
}
