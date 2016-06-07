package com.bmc.gibraltar.automation.items.datadictionary;

import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.Property;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.List;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.*;
import static com.bmc.gibraltar.automation.items.element.ElementProperties.LABEL;
import static com.bmc.gibraltar.automation.items.element.PaletteGroup.BASECAMP;
import static java.lang.String.format;

public class DataDictionary extends Dictionary {
    // TODO: make 'DATA_DICTIONARY' private
    public static final String DATA_DICTIONARY = "Edit Expression for";
    protected String eventLocator;
    protected String edit = "//a[@ng-click='openEditor()']";
    protected String subGroupOrOptionsPath = "xpath=//li[.//label[.='%s']]//treeitem//div[contains(@class, '%s')]";
    protected String varElement = "//label[.//div[text()='%s']]//div[@ng-dblclick='addValueToExpression(node)']";
    protected ActiveElement elementInActivities;
    protected boolean isExpressionBuilder;
    private String expressionTemplate = "${process.%s}";
    private String var = "";
    private String varExpression = "";
    private String group = "Process Variables";
    private String elementLocator = "";
    private String associationsLocator = "xpath=//label[contains(@class, 'rx-tree-node-parent')]" +
            "[contains(., 'Associations')]";
    private String associationsTreeLocator = "/../../treeitem[contains(@class, 'ng-scope')]";
    private String associationsInputLocator = "xpath=//label[contains(@class, 'tree-node-leaf')]" +
            "[//*[contains(@class, 'icon __icon-arrow_right_square_input')]][.='%s']";

    public DataDictionary(WebDriver driver, ActiveElement element, Property property) {
        super(driver, element, property);
        eventLocator = property.getFieldNameLocator();
        this.element = element;
        editLocator = getLast(eventLocator + edit);
        header = String.format(header, DATA_DICTIONARY);
    }

    public DataDictionary open() {
        if (waitForElementPresent(header, 3)) {
            return this;
        }
        if (!isElementPresent(editLocator)) {
            element.click();
        }
        click(editLocator);
        waitForElementPresent(editorWindow, 5);
        return this;
    }

    @Override/*for needed Type conversion of parent method*/
    public <T> DataDictionary doubleClickVar(T... var) {
        return (DataDictionary) super.doubleClickVar(var);
    }

    @Step
    public <T> DataDictionary clickOnVar(T... var) {
        return (DataDictionary) super.clickOnVar(var);
    }

    @Override
    protected <T> String getLocatorOfVariable(T... vars) {
        var = "" + vars[0];
        varExpression = var;

        if (vars[0] instanceof DictionaryGroup.Legend) {
            return legendMOD(vars);
        }

        if (vars[0] instanceof DictionaryGroup) {
            differentGroupsMOD(vars);
        }

        if (vars[0] instanceof DictionaryGroup.GENERAL) {
            DictionaryGroup.GENERAL var = (DictionaryGroup.GENERAL) vars[0];
            expressionTemplate = "%s";
            group = "General";
            this.var = var.getName();
            varExpression = var.getValue();
        }
        if (vars[0] instanceof ActiveElement) {
            elementInActivities = (ActiveElement) vars[0];
            group = elementInActivities.getExistPropertyValue(LABEL);
            expressionTemplate = "${activityResults.rx-" + elementInActivities.getId() + ".%s}";
            var = "" + vars[1];
            propertyMOD(vars[1]);
            varExpression = var;
            taskGroupMOD();
            userTaskMOD();
            basecampMOD();
        }
        addExpectedExpression();
        expandGroup();
        return generateLocator();
    }

    private <T> String legendMOD(T... vars) {
        varElement += "[.//i[@class='" + ((DictionaryGroup.Legend) vars[0]).getIconClass() + "']]";
        T[] tempArr = vars;
        for (int i = 0; i <= vars.length - 2; i++) {
            tempArr[i] = vars[i + 1];
        }
        return getLocatorOfVariable(tempArr);
    }

    private void expandGroup() {
        tooLargeTreeMOD();
        String expandPath = String.format(collapsed, group);
        if (waitForElementPresent(expandPath, 2)) {
            click(expandPath);
        }
    }

    private String generateLocator() {
        String tree = String.format(parentGroup, group);
        String leaf = String.format(varElement, var);
        elementLocator = tree + leaf;
        return getLast(elementLocator);
    }

    private void addExpectedExpression() {
        expected = String.format(expressionTemplate, varExpression);
    }

    // MODS start
    private <T> void propertyMOD(T property) {
        if (property instanceof Property)
            var = ((Property) property).getClearName();
    }

    private void userTaskMOD() {
        if (isExpressionBuilder && element.getType() == USER_TASK && element == elementInActivities) {
            expressionTemplate = "${userTask.%s}";
        }
    }

    private void taskGroupMOD() {
        if (elementInActivities.getType().equals(CREATE_TASK, CREATE_TASK_CUSTOM)) {
            if (var.equalsIgnoreCase("count")) {
                varExpression = "count";
            } else {
                group += ".Task";
                varExpression = "task." + var;
            }
        }
    }

    private void tooLargeTreeMOD() {
        if (group.contains(".")) {
            String groups[] = group.split("\\.");
            String tempGr = groups[0];
            for (String grp : groups) {
                group = grp;
                expandGroup();
            }
            group = tempGr;
        }
    }

    private <T> void differentGroupsMOD(T... vars) {
        if (vars[0] == DictionaryGroup.OPTIONS) {
            group = DictionaryGroup.OPTIONS.getName();
            expressionTemplate = "%s";
        }
        var = "" + vars[1];
        varExpression = var;
    }

    private void basecampMOD() {
        if (elementInActivities.getType().getPaletteGroup() == BASECAMP && !var.equals("Output"))
            varExpression = var.toLowerCase().replace(" ", "");
    }
    // MODS end

    @Override/*for needed Type conversion of parent method*/
    public DataDictionary doubleClickInTreeOnVar(DictionaryGroup mainGroup, String[] subGroupsAndVariable) {
        return (DataDictionary) super.doubleClickInTreeOnVar(mainGroup, subGroupsAndVariable);
    }

    @Step
    public void verifyExpressionCorrectness() {
        log.info("Verifying if the expression is correct.");
        String actual = getExpressionFieldValue();
        verifyEquals(actual, expected, "Actual: " + actual + " Expected: " + expected);
    }

    @Step
    public void verifyExpressionCorrectness(String expected) {
        String actual = getExpressionFieldValue();
        verifyEquals(actual, expected, "Actual: " + actual + " Expected: " + expected);
    }

    @Step
    public void verifyResultCorrectness() {
        log.info("Verifying that changes have been applied.");
        String actual = element.getPropertyValue(property).trim();
        verifyEquals(actual, expected, "Actual: " + actual + " Expected: " + expected);
    }

    @Step
    public void verifyResultCorrectness(String property) {
        verifyTrue(element.getPropertyValues(property).contains(expected));
    }

    @Override
    public DataDictionary expandGroup(String group) {
        DataDictionary currentDD = (DataDictionary) super.expandGroup(group);
        if (elementInActivities != null) {
            String locatorCollapsed = String.format(collapsed, group);
            if (elementInActivities.getType().equals(CREATE_TASK, CREATE_TASK_CUSTOM))
                locatorCollapsed = String.format(collapsed, "Task");
            if (isElementPresent(locatorCollapsed))
                click(locatorCollapsed);
        }
        return currentDD;
    }

    /**
     * Gets all present sub-groups in the 'Activities' group in the Data Dictionary
     *
     * @return list of sub-groups
     */
    @Step
    public List<String> getSubGroupsOfActivitiesGroupInDataDictionary() {
        expandRootGroups();
        String groupName = DictionaryGroup.ACTIVITIES.getName();
        expandGroup(groupName);
        List<String> optionsInGroup = new ArrayList<>();
        List<WebElement> groupOptionsInDataDictionary = getElements(String.format(subGroupOrOptionsPath + "/div", groupName, "rx-data-dictionary-item"));
        for (WebElement singleGroup : groupOptionsInDataDictionary) {
            String singleGroupName = singleGroup.getText();
            optionsInGroup.add(singleGroupName);
        }
        return optionsInGroup;
    }

    /**
     * Verifies if specified sub-groups are present in the 'Activities' group
     *
     * @param groups list of groups that should be present
     */
    @Step
    public void verifySubGroupsPresentInActivitiesGroup(String... groups) {
        List<String> allSubGroups = getSubGroupsOfActivitiesGroupInDataDictionary();
        for (String subGroup : groups) {
            log.info("Verifying if the '" + subGroup + "' sub-group is present in the 'Activities' group.");
            verifyTrue(allSubGroups.contains(subGroup));
        }
    }

    @Step
    public DataDictionary verifyOptionsPresentInOptionsGroup(List<String> optionsThatShouldBePresent) {
        List<String> listOfPresentOptions = getOptionsPresentInOptionGroup();
        for (String option : optionsThatShouldBePresent) {
            verifyTrue(listOfPresentOptions.contains(option), option + "Is not present");
        }
        return this;
    }

    @Step
    public List<String> getOptionsPresentInOptionGroup() {
        List<String> listOfPresentOptions = new ArrayList<>();
        List<WebElement> optionsInDataDictionary = getElements(String.format(subGroupOrOptionsPath, "Options", "rx-data-dictionary-item-value"));
        for (WebElement singleOption : optionsInDataDictionary) {
            String singleOptionName = singleOption.getText();
            listOfPresentOptions.add(singleOptionName);
        }
        return listOfPresentOptions;
    }

    @Override/*for needed Type conversion of parent method*/
    public DataDictionary setExpression(String s) {
        return (DataDictionary) super.setExpression(s);
    }

    /**
     * Expands the variable and Associations from Property Group and counts all Associations.
     *
     * @param varName the variable from Property Group (should contain Associations).
     * @return Example: var a(process input) associated with B, and B associated with C, returns: "a -> B -> C".
     * Other examples: "b -> A, C", "c -> B -> A".
     */
    @Step
    public String getAssociationFor(String varName) {
        int associationsCounter = 0;
        clickOnVar(DictionaryGroup.PROCESS_VARIABLES, varName);
        if (isElementPresent(associationsLocator)) {
            do {
                ++associationsCounter;
                varName += " -> ";
                click(getLast(associationsLocator));
                String inputVar = getText(getElement(getLast(associationsLocator) + associationsTreeLocator));
                if (!inputVar.contains("\n")) {
                    click(format(associationsInputLocator, inputVar));
                } else inputVar = inputVar.replace("\n", ", ");
                varName += inputVar;
            }
            while (associationsCounter < getElements(associationsLocator).size());
        }
        return varName;
    }

    /**
     * Expands the variable and Associations from Property Group, search and click on associated variables.
     *
     * @param varName input parameter, contains associations.
     */
    @Step
    public Dictionary addAllAssociationVarsFor(String varName) {
        int associationsCounter = 0;
        cleanExpressionTextBox();
        click(getLocatorOfVariable(DictionaryGroup.PROCESS_VARIABLES, varName));
        if (isElementPresent(associationsLocator)) {
            do {
                ++associationsCounter;
                click(getLast(associationsLocator));
                String inputVar = getText(getElement(getLast(associationsLocator) + associationsTreeLocator));
                for (String var : inputVar.split("\n")) {
                    var = format(associationsInputLocator, var);
                    click(var);
                }
            }
            while (associationsCounter < getElements(associationsLocator).size());
        }
        return this;
    }

    public DataDictionary clickOk() {
        click(okButton);
        return this;
    }
}