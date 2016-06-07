package com.bmc.gibraltar.automation.pages;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import ru.yandex.qatools.allure.annotations.Step;

import static com.bmc.gibraltar.automation.items.ActionBar.Action.DELETE;
import static com.bmc.gibraltar.automation.items.ActionBar.Action.NEW;

public class RuleDefinitionsTabPage extends TaskManagerHomePage {

    public RuleDefinitionsTabPage(WebDriver driver) {
        super(driver, "task-manager");
        tabName = "Rule Definitions";
    }

    @Override
    public String getPageUrl() {
        return APP_MANAGER_URL + String.format("/app/application/%s/definitions?tab=rule-definitions", bundle);
    }

    @Step
    public RuleDefinitionsTabPage selectRule(String ruleName) {
        selectRow(ruleName);
        return this;
    }

    @Step
    public boolean isRuleExistent(String ruleName) {
        return isRowExistent(ruleName);
    }

    public RuleEditorPage initiateNewRule(String ruleName) {
        return initiateNewRule().setRuleName(ruleName);
    }

    public RuleEditorPage initiateNewRule() {
        actionBar(NEW).waitForActionPresent(5000).click();
        RuleEditorPage ruleEditorPage = new RuleEditorPage(wd);
        ruleEditorPage.waitForPageLoaded();
        return ruleEditorPage;
    }

    public RuleDefinitionsTabPage deleteRule(String ruleName) {
        selectRow(ruleName);
        actionBar(DELETE).click();
        confirmModalDialog(true);
        return this;
    }

    @Step
    public RuleEditorPage openRuleDefinition(String ruleDefinitionName) {
        log.info("Opening the rule definition with the name: " + ruleDefinitionName);
        String ruleLink = "link=" + ruleDefinitionName;
        waitForElementNotPresent(loadingSpinner, 10);
        try {
            waitForElementPresent(ruleLink, 10);
            click(ruleLink);
        } catch (NoSuchElementException e) {
            Assert.fail("\n Cannot find the rule definition with the name: " + ruleLink, e);
        }
        RuleEditorPage editorPage = new RuleEditorPage(wd);
        if (editorPage.isPageLoaded()) {
            return editorPage;
        } else {
            Assert.fail("Cannot open the rule definition.");
            return null;
        }
    }
}