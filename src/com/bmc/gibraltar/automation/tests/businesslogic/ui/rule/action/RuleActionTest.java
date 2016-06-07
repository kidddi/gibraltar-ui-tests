package com.bmc.gibraltar.automation.tests.businesslogic.ui.rule.action;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.rule.ActiveRuleAction;
import com.bmc.gibraltar.automation.items.rule.RuleLink;
import com.bmc.gibraltar.automation.items.rule.TriggerEvent;
import com.bmc.gibraltar.automation.pages.RuleDefinitionsTabPage;
import com.bmc.gibraltar.automation.pages.RuleEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.TestCaseId;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;

import static com.bmc.gibraltar.automation.items.rule.RuleAction.*;

public class RuleActionTest extends AppManagerBaseTest {
    private RuleDefinitionsTabPage ruleTab;
    private RuleEditorPage ruleEditor;

    @BeforeClass
    protected void goToRuleDesigner() {
        log.info("BeforeClass start.");
        ruleTab = new RuleDefinitionsTabPage(wd).navigateToPage();
        ruleEditor = ruleTab.initiateNewRule();
        ruleEditor.selectPrimaryRecordDefinition("Task");
        ActiveRuleAction trigger = ruleEditor.getDroppedComponent(TRIGGER);
        ruleEditor.selectRuleElementOnCanvas(trigger);
        ruleEditor.setTriggerEvent(TriggerEvent.ON_CREATE);
        log.info("BeforeClass end.");
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "orderOfActionsOnCanvas")
    @Features("[P541] Rule Designer")
    @Stories("US209232: Rule Designer UI - Continued")
    @TestCaseId("TC945745")
    @GUID("4217bf3e-ff81-4e85-ab40-9c67c8e598d1")
    public void orderOfActionsOnCanvas() {
        ActiveRuleAction qualification = ruleEditor.dragAndDropElement(RULE_QUALIFICATION);
        ActiveRuleAction showMessage = ruleEditor.dragAndDropElement(SHOW_MESSAGE);
        ruleEditor.clickOnFreeSpaceOnCanvas();
        Screenshot processWorkflow = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(wd, ruleEditor.getWorkflowContainer());
        ruleEditor.deleteElement(qualification);
        assertFalse(ruleEditor.isRuleElementPresent(qualification));
        RuleLink lastLinkOnCanvas = ruleEditor.getLastLink();
        assertEquals(lastLinkOnCanvas.getTargetElement().getRuleElementId(), showMessage.getRuleElementId(),
                "Not all elements are connected.");
        ruleEditor.dragAndDropElement(RULE_QUALIFICATION);
        ruleEditor.clickOnFreeSpaceOnCanvas();
        Screenshot processWorkflowWithNewQualification = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(wd, ruleEditor.getWorkflowContainer());
        ImageDiff diff = new ImageDiffer().makeDiff(processWorkflow, processWorkflowWithNewQualification);
        assertFalse(diff.hasDiff(), "The 'Qualification' was not connected to the Trigger element.");
    }
}