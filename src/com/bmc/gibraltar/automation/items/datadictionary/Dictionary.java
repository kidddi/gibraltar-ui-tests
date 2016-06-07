package com.bmc.gibraltar.automation.items.datadictionary;

import com.bmc.gibraltar.automation.framework.utils.Bindings;
import com.bmc.gibraltar.automation.items.DesignerElement;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.Property;
import com.bmc.gibraltar.automation.pages.BasePage;
import com.bmc.gibraltar.automation.pages.HasValidator;
import com.bmc.gibraltar.automation.pages.Locators;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Dictionary extends Bindings implements Locators {
    public static final String CONDITIONS_EXPRESSION = "Edit Expression for";
    protected static String variableInDictionary = "xpath=//div[@class='rx-data-dictionary-item']//*[.='%s']";
    protected String expressionTextbox = "xpath=//*[contains(local-name(), 'rx-rich-expression-editor')]/div/p";
    protected String expressionSearch = "xpath=//div[@class='d-textfield']//input[@type='search']";
    protected String groupLabel = "xpath=//label[contains(@class, 'rx-tree-node-parent')]";
    protected String collapsed = "xpath=//li[contains(@class, 'tree-collapsed')]//label[.='%s']";
    protected Property property;
    protected String expected = "";
    protected String parentGroup = "xpath=//li[contains(., '%s')]";
    protected String header = "xpath=//h4[contains(text(), '%s')]";
    protected String editLocator;
    protected ActiveElement element;
    protected String mainGroupPathPrefix = "xpath=//treecontrol//li[.//label[.='%s']]";
    protected String variablePathSuffix = "//treeitem//div[.='%s'][contains(@class, 'rx-data-dictionary-item-value')]";
    protected String groupsTreePath = "xpath=//treecontrol/ul/li[contains(concat('', @class, ''), 'tree-%s')]/div/label";
    protected String dictionaryHeader = "xpath=//h4[contains(@class,'modal-title')]";
    protected String propertyName = "xpath=//span[contains(@class,'d-textfield__item ng-binding')]";
    protected String next = "xpath=//button[@ng-click='next()']";
    protected String prev = "xpath=//button[@ng-click='prev()']";
    protected String errorPath = "xpath=//p[@class='d-error d-error_pattern'][contains(text(), '%s')]";
    protected String infoHintMsgIcon = "xpath=//a[contains(@class, 'rx-highlight-on-hover')]";
    protected String hintTextMsg = "xpath=//div[contains(@class, 'popover-content ng-binding')]";
    protected String tagsInExpression = expressionTextbox + "//span[contains(@class,'-item-value')]";
    protected String operatorsButton = "xpath=//div[@class='rx-expression-editor-button-list']//li/button";
    protected String specificOperatorButton = operatorsButton + "[contains(text(),'%s')]";
    protected String editorWindow = "xpath=//div[@class='modal-content']";
    protected String dataItem = "//div[@class='ng-binding rx-data-dictionary-item-value']";
    protected String cancelBtn = "xpath=//button[@ng-click='cancel()'][text() ='Cancel']";
    protected String okButton = "xpath=//button[@ng-click='ok()']";
    private DesignerElement definitionElement;

    protected Dictionary(WebDriver driver, DesignerElement element, Property property) {
        this.definitionElement = element;
        this.property = property;
        wd = driver;
    }

    protected Dictionary() {
    }

    /**
     * @return verdict if desired Dictionary is displayed
     */
    @Step
    public boolean isDisplayed() {
        return waitForElementPresent(dictionaryHeader, 2);
    }

    /**
     * @param header
     * @return verdict if current header is equals to {@header}
     */
    protected boolean isHeaderEquals(String header) {
        waitForElementPresent(dictionaryHeader, 1);
        String actual = getText(dictionaryHeader);
        return actual.equals(header);
    }

    @Step
    public List<String> getPresentOperators() {
        return getElements(operatorsButton).stream()
                .filter(WebElement::isDisplayed).map(WebElement::getText).collect(Collectors.toList());
    }

    @Step
    public Dictionary addOperatorToExpression(String operator) {
        if (waitForElementPresent(String.format(specificOperatorButton, operator), 2)) {
            click(String.format(specificOperatorButton, operator));
        }
        return this;
    }

    /**
     * @return the name of current editable property
     */
    @Step
    public String getCurrentProperty() {
        return getElement(propertyName).getText();
    }

    /**
     * Executes click on 'Next' button
     *
     * @return instance of Dictionary
     */
    @Step
    public Dictionary next() {
        click(next);
        return this;
    }

    /**
     * Executes click on 'Previous' button
     *
     * @return instance of Dictionary
     */
    @Step
    public Dictionary previous() {
        click(prev);
        return this;
    }

    /**
     * Types the {@expression} into a expression textarea (without clearing the textarea content)
     *
     * @param expression to be set into the textarea
     * @return instance of current Dictionary
     */
    @Step
    public Dictionary setExpression(String expression) {
        return setExpression(false, expression);
    }

    @Step
    public Dictionary setExpression(boolean clearBeforeSet, String expression) {
        if (clearBeforeSet) {
            cleanExpressionTextBox();
        }
        getElement(expressionTextbox).click();
        wd.switchTo().activeElement().sendKeys(expression);
        return this;
    }

    /**
     * Cleans all info present on Expression Text Box
     *
     * @return instance of the same Dictionary
     */
    @Step
    public Dictionary cleanExpressionTextBox() {
        getElement(expressionTextbox).clear();
        return this;
    }

    /**
     * Abstract method of finding the locator to any vars.
     *
     * @param vars
     * @param <T>
     * @return locator
     */
    protected abstract <T> String getLocatorOfVariable(T... vars);

    /**
     * Executes double click on {@var} among all variables in the dictionary
     *
     * @param var
     * @param <T>
     * @return instance of Dictionary
     */
    @Step
    public <T> Dictionary doubleClickVar(T... var) {
        doubleClick(getLocatorOfVariable(var));
        return this;
    }

    /**
     * Executes one click on {@var} among all variables in the dictionary
     *
     * @param var
     * @param <T>
     * @return instance of Dictionary
     */
    @Step
    public <T> Dictionary clickOnVar(T... var) {
        click(getLocatorOfVariable(var));
        return this;
    }

    /**
     * Expands all groups in Dictionary tree*
     */
    @Step
    public Dictionary expandAllGroups() {
        expandRootGroups();
        getGroupsPresentInDataDictionary().forEach(this::expandGroup);
        return this;
    }

    /**
     * Expands only all Root high level groups in Dictionary tree
     */
    @Step
    public Dictionary expandRootGroups() {
        getElements(String.format(groupsTreePath, "collapsed")).forEach(WebElement::click);
        return this;
    }

    /**
     * Closes Dictionary by clicking 'Close' button
     *
     * @return instance of a corresponding designer page
     */
    @Step
    public <T extends BasePage & HasValidator> T close() {
        click(closeFormBtn);
        return backToDesignerPage();
    }

    private <T extends BasePage & HasValidator> T backToDesignerPage() {
        waitForElementNotPresent(dictionaryHeader);
        expected = "";
        return definitionElement.getPage();
    }

    /**
     * Closes Dictionary by clicking 'Cancel' button
     *
     * @return instance of a corresponding designer page
     */
    @Step
    public <T extends BasePage & HasValidator> T cancel() {
        click(cancelBtn);
        return backToDesignerPage();
    }

    /**
     * Clicks on 'OK' button with purpose to apply changes and confirms any modal dialog.
     *
     * @param <T>
     * @return instance of a corresponding designer page
     */
    @Step
    public <T extends BasePage & HasValidator> T apply() {
        log.info("Applying changes made in Dictionary editor.");
        click(okButton);
        T page = backToDesignerPage();
        page.confirmModalDialog(true);
        return page;
    }

    /**
     * This method allows to click on the option inside the list in the Data Dictionary.
     * <p/>
     * For example,
     * we need to double-click on the 'Submitter' variable that is inside the 'Record Info' group,
     * and the 'Record Info' group is a sub-group in the {@mainGroup} (e.g. 'Process Variables')
     * In this case we will have:
     * doubleClickInTreeOnVar(DictionaryGroup.PROCESS_VARIABLES, new String[] {"Record Info", "Submitter"})
     *
     * @param mainGroup            mainGroup should be one of these: e.g. 'Options', 'Process Variables', 'General', 'Activities'
     * @param subGroupsAndVariable subGroupsAndVariable the last in this array is a variable to be clicked, all before - are subgroups.
     */
    @Step
    public Dictionary doubleClickInTreeOnVar(DictionaryGroup mainGroup, String[] subGroupsAndVariable) {
        doubleClick(getVariablePath(mainGroup, subGroupsAndVariable));
        return this;
    }

    /**
     * Returns locator to variable by knowing full tree path to variable as a node.
     *
     * @param mainGroup            mainGroup should be one of these: e.g. 'Options', 'Process Variables', 'General', 'Activities'
     * @param subGroupsAndVariable - subGroupsAndVariable the last in this array is a variable to be clicked, all before - are subgroups.
     * @return
     */
    protected String getVariablePath(DictionaryGroup mainGroup, String[] subGroupsAndVariable) {
        String mainGroupName = mainGroup.getName();
        String pathToMainGroup = String.format(mainGroupPathPrefix, mainGroupName);
        expandGroup(mainGroupName);
        String subGroupsPath = "";
        if (subGroupsAndVariable.length >= 2) {
            for (int i = 0; i <= subGroupsAndVariable.length - 2; i++) {
                String path = "//li[.//label[.='" + subGroupsAndVariable[i] + "']]";
                String pathToSubParent = pathToMainGroup + subGroupsPath + path;
                boolean isCollapsed = getAttr(pathToSubParent, "class").contains("tree-collapsed");
                if (isCollapsed) {
                    click(pathToSubParent + "//label");
                }
                subGroupsPath += "//treeitem" + path;
            }
        }
        String variableToHandle = subGroupsAndVariable[subGroupsAndVariable.length - 1];
        String variableName = String.format(variablePathSuffix, variableToHandle);
        String finalPath = pathToMainGroup + subGroupsPath + variableName;
        return finalPath;
    }

    /**
     * Expands the group {@group} if that is collapsed in the tree of Dictionary
     *
     * @param group
     * @return
     */
    @Step
    public Dictionary expandGroup(String group) {
        String locatorCollapsed = String.format(collapsed, group);
        if (waitForElementPresent(locatorCollapsed, 2)) {
            click(locatorCollapsed);
        }
        return this;
    }

    /**
     * Returns list of group names, with are visible in Dictionary
     *
     * @return
     */
    @Step
    public List<String> getGroupsPresentInDataDictionary() {
        expandRootGroups();
        return getListOfWebElementsTextByLocator(groupLabel);
    }

    /**
     * Returns value from expression text box.
     * For more correct value use mix of this method and getTagsFromExpressionTextBox()
     *
     * @return
     */
    @Step
    public String getExpressionFieldValue() {
        return getText(expressionTextbox).replaceAll("\n", " ").trim();
    }

    /**
     * @return list of names for all variable Tags, present on Expression Text Box
     */
    public List<String> getTagsFromExpressionTextBox() {
        return getListOfWebElementsTextByLocator(tagsInExpression);
    }

    /**
     * Verifies current expression in text field is equals to {@text}
     *
     * @param text
     * @return instance of Dictionary
     */
    @Step
    public void verifyExpressionEquals(String text) {
        verifyTrue(getExpressionFieldValue().equals(text));
    }

    /**
     * Checks if {@variable} name is present in Dictionary. Intermediate Groups need to be expanded for correct result.
     *
     * @param variable - name of needful variable
     * @return boolean verdict if {@variable} is present in Dictionary
     */
    @Step
    public boolean isVarPresentInDictionary(String variable) {
        return isElementPresent(String.format(variableInDictionary, variable));
    }

    /**
     * Verifies that current header's text is equal to {@text}
     *
     * @param text
     * @return instance of Dictionary
     */
    @Step
    public Dictionary verifyHeaderText(String text) {
        verifyTrue(isHeaderEquals(text));
        return this;
    }

    /**
     * Verifies by clicking 'Info' icon, that hint info message text equals to {@msgText}
     *
     * @param msgText to be present in the Dictionary
     * @return instance of Dictionary
     */
    @Step
    public Dictionary verifyHintInfoMessage(String msgText) {
        click(infoHintMsgIcon);
        verifyTrue(getText(hintTextMsg).contains(msgText), "Dictionary does not have the expected info message.");
        click(infoHintMsgIcon);
        return this;
    }

    @Step
    public boolean isErrorPresent(String errorMessage) {
        return isElementPresent(String.format(errorPath, errorMessage));
    }

    /**
     * @param group
     * @return list of all variables, visible in Dictionary tree
     */
    public List<String> getAllVars(String group) {
        String groupLocator = String.format(parentGroup, group) + dataItem;
        waitForElementPresent(groupLocator, 3);
        return getListOfWebElementsTextByLocator(groupLocator);
    }
}