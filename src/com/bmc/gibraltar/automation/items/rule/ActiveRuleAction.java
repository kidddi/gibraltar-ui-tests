package com.bmc.gibraltar.automation.items.rule;

import com.bmc.gibraltar.automation.items.DesignerElement;
import com.bmc.gibraltar.automation.pages.RuleEditorPage;

public class ActiveRuleAction extends DesignerElement {
    private RuleAction ruleAction;
    private String ruleElementId;
    private RuleEditorPage ruleEditorPage;
    private String modelId = "[@model-id='%s']";

    public ActiveRuleAction(RuleAction ruleAction, String ruleElementId, RuleEditorPage ruleEditorPage) {
        this.ruleAction = ruleAction;
        this.ruleElementId = ruleElementId;
        this.ruleEditorPage = ruleEditorPage;
    }

    public ActiveRuleAction() {
    }

    public RuleAction getRuleAction() {
        return ruleAction;
    }

    public String getName() {
        return ruleAction.getName();
    }

    public String getXPath() {
        return ruleAction.locatorOnCanvas() + String.format(modelId, ruleElementId);
    }

    public String getRuleElementId() {
        return ruleElementId;
    }

    @Override
    public RuleEditorPage getPage() {
        return ruleEditorPage;
    }
}