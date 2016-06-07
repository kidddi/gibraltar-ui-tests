package com.bmc.gibraltar.automation.items.rule;

import com.bmc.gibraltar.automation.pages.RuleEditorPage;
import org.openqa.selenium.WebDriver;

/**
 * Link between two rule elements.
 * Once a rule element dropped to the Canvas, it should be linked with the previous element.
 * This doesn't apply to the Qualification element, that should be connected to the trigger element.
 */
public class RuleLink extends ActiveRuleAction {
    private ActiveRuleAction source;
    private ActiveRuleAction target;
    private String xPath;
    private String ruleElementId;
    private RuleEditorPage editorPage;
    private String modelIdLocator = "xpath=//*[@model-id='%s']";
    private String pathToLinkIcon = "//*[@class='tool-options']//*[contains(local-name(), 'path')]";
    private String removeIcon = "//*[@class='tool-remove']//*[contains(@transform,'scale')]";

    private RuleLink(RuleAction ruleAction, String ruleElementId, RuleEditorPage ruleEditorPage) {
        super(ruleAction, ruleElementId, ruleEditorPage);
    }

    public RuleLink(WebDriver driver, ActiveRuleAction source, ActiveRuleAction target, String ruleElementId, RuleEditorPage editorPage) {
        this.editorPage = editorPage;
        this.ruleElementId = ruleElementId;
        this.source = source;
        this.target = target;
        this.xPath = String.format(modelIdLocator + pathToLinkIcon, ruleElementId);
        wd = driver;
    }

    public String getModelId() {
        return String.format(modelIdLocator, ruleElementId);
    }

    public String getXPath() {
        return xPath;
    }

    public void remove() {
        editorPage.click(xPath);
        String removeLocator = xPath + removeIcon;
        editorPage.click(removeLocator);
    }

    public ActiveRuleAction getTargetElement() {
        return target;
    }

    public ActiveRuleAction getSourceElement() {
        return source;
    }

    @Override
    public RuleEditorPage getPage() {
        return editorPage;
    }
}