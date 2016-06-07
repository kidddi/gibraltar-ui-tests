package com.bmc.gibraltar.automation.tests.businesslogic.ui.rule.definition;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.rule.ActiveRuleAction;
import com.bmc.gibraltar.automation.items.rule.TriggerEvent;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.PropertyTab;
import com.bmc.gibraltar.automation.pages.RuleDefinitionsTabPage;
import com.bmc.gibraltar.automation.pages.RuleEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;
import static com.bmc.gibraltar.automation.items.rule.RuleAction.*;
import static com.bmc.gibraltar.automation.items.tab.InspectorGroup.INPUT_MAP;
import static com.bmc.gibraltar.automation.items.tab.InspectorGroup.PROPERTIES;

public class RuleDefinitionTest extends AppManagerBaseTest {
    private RuleDefinitionsTabPage ruleTab;
    private RuleEditorPage ruleEditor;
    private String recordDefinitionName = "Task";

    @BeforeMethod
    protected void goToRuleDesigner() {
        log.info("BeforeClass start.");
        ruleTab = new RuleDefinitionsTabPage(wd).navigateToPage();
        ruleEditor = ruleTab.initiateNewRule();
        ruleEditor.selectPrimaryRecordDefinition(recordDefinitionName);
        ActiveRuleAction trigger = ruleEditor.getDroppedComponent(TRIGGER);
        ruleEditor.selectRuleElementOnCanvas(trigger);
        ruleEditor.setTriggerEvent(TriggerEvent.ON_CREATE);
        log.info("BeforeClass end.");
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "ruleDefinitionWithActionsOfSameType")
    @Features("[P541] Rule Designer")
    @GUID("872e9e02-53a8-4dc1-a0d1-8b4da515a092")
    public void ruleDefinitionWithActionsOfSameType() {
        String ruleDefinitionName = "Rule-" + RandomStringUtils.randomAlphanumeric(5);
        PropertyTab elementPropertiesTab = ruleEditor.getInspector();
        ruleEditor.dragAndDropElement(SHOW_MESSAGE);
        elementPropertiesTab.setProperty(InspectorGroup.PROPERTIES, "Message Text:", "expected message");
        ruleEditor.dragAndDropElement(SHOW_MESSAGE);
        elementPropertiesTab.setProperty(InspectorGroup.PROPERTIES, "Message Text:", "expected message2");
        ruleEditor.setRuleName(ruleDefinitionName);
        ruleEditor.saveDefinition();
        ruleTab = ruleEditor.closeRule();
        assertTrue(ruleTab.isRuleExistent(ruleDefinitionName),
                "Rule Definition is not present on the Rule Definitions page.");
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "ruleDefinitionWithRecordInstanceActions")
    @Features("[P541] Rule Designer")
    @Stories("US209214: Custom actions in the Rule Designer")
    @TestCaseId("TC945881")
    @GUID("bf04189a-60ab-4304-9b21-32b044c9566e")
    public void ruleDefinitionWithRecordInstanceActions() {
        String ruleDefinitionName = "RuleWithRecordActions-" + RandomStringUtils.randomAlphanumeric(5);
        ruleEditor.setRuleName(ruleDefinitionName);
        PropertyTab elementPropertiesTab = ruleEditor.getInspector();
        ruleEditor.dragAndDropElement(RULE_QUALIFICATION);
        elementPropertiesTab.setProperty(PROPERTIES, "Qualification:", "true");
        ruleEditor.dragAndDropElement(CREATE_RECORD_INSTANCE);
        elementPropertiesTab.setProperty(INPUT_MAP, RECORD_DEFINITION_NAME.getName(), recordDefinitionName);
        elementPropertiesTab.setProperty(INPUT_MAP, PRIORITY.getName(), "Medium");
        ruleEditor.dragAndDropElement(UPDATE_RECORD_INSTANCE);
        elementPropertiesTab.setProperty(INPUT_MAP, RECORD_DEFINITION_NAME.getName(), recordDefinitionName);
        elementPropertiesTab.setProperty(INPUT_MAP, RECORD_INSTANCE_ID.getName(), "1");
        ruleEditor.saveDefinition();
        verifyTrue(ruleEditor.getAllErrorMessages().isEmpty(), "Errors are present while saving the rule definition.");
        ruleTab = ruleEditor.closeRule();
        assertTrue(ruleTab.isRuleExistent(ruleDefinitionName),
                "Rule Definition is not present on the Rule Definitions page.");
    }
}