package com.bmc.gibraltar.automation.items.datadictionary;

import com.bmc.gibraltar.automation.items.element.Property;
import com.bmc.gibraltar.automation.items.rule.ActiveRuleAction;
import com.bmc.gibraltar.automation.pages.RuleEditorPage;
import org.openqa.selenium.WebDriver;

public class RuleDictionary extends Dictionary {
    protected String edit = "//a[@ng-click='openEditor()']";
    protected String label = "xpath=//div[//label[.='%s']]";
    private ActiveRuleAction ruleElement;
    private String propertyName;
    private String eventLocator;
    private RuleEditorPage page;

    public RuleDictionary(WebDriver driver, ActiveRuleAction ruleElement, Property property) {
        super(driver, ruleElement, property);
    }

    public RuleDictionary(WebDriver driver, ActiveRuleAction ruleElement, String propertyName) {
        this.ruleElement = ruleElement;
        this.propertyName = propertyName;
        eventLocator = String.format(label, propertyName);
        editLocator = getLast(eventLocator + edit);
        page = ruleElement.getPage();
        wd = driver;
        header = String.format(header, "Edit Expression for");
    }

    @Override
    protected <T> String getLocatorOfVariable(T... vars) {
        return "";
    }

    public RuleDictionary open() {
        if (waitForElementPresent(header, 2)) {
            return this;
        }
        if (!isElementPresent(editLocator)) {
            page.selectRuleElementOnCanvas(ruleElement);
        }
        click(editLocator);
        waitForElementPresent(editorWindow, 5);
        return this;
    }

    @Override
    public RuleEditorPage close() {
        click(closeFormBtn);
        return page;
    }
}