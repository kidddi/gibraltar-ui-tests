package com.bmc.gibraltar.automation.tests.businesslogic.ui.rule.definition;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.rule.ActiveRuleAction;
import com.bmc.gibraltar.automation.items.rule.TriggerEvent;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.PropertyTab;
import com.bmc.gibraltar.automation.pages.JsonEditorPage;
import com.bmc.gibraltar.automation.pages.RuleDefinitionsTabPage;
import com.bmc.gibraltar.automation.pages.RuleEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
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

import java.util.HashMap;

import static com.bmc.gibraltar.automation.items.rule.RuleAction.*;

public class ShowMessageRuleTest extends AppManagerBaseTest {
    private RuleDefinitionsTabPage ruleTab;
    private RuleEditorPage ruleEditor;
    private String ruleDefinitionNameForShowMessage = "Show Message action" + RandomStringUtils.randomAlphanumeric(5);
    private Screenshot ruleWorkflow;
    private String messageText = "Your Task is created. The task id is $1$";
    private int messageCode = 20000;
    private String messageType = "NOTE";

    @BeforeClass
    public void createRuleDefinition() {
        ruleTab = new RuleDefinitionsTabPage(wd).navigateToPage();
        ruleEditor = ruleTab.initiateNewRule();
        ruleEditor.selectPrimaryRecordDefinition("Task");
        ActiveRuleAction trigger = ruleEditor.getDroppedComponent(TRIGGER);
        ruleEditor.selectRuleElementOnCanvas(trigger);
        ruleEditor.setTriggerEvent(TriggerEvent.ON_CREATE);

        PropertyTab elementPropertiesTab = ruleEditor.getInspector();
        ruleEditor.dragAndDropElement(SHOW_MESSAGE);
        elementPropertiesTab.setProperty(InspectorGroup.PROPERTIES, "Message Text:", messageText);
        elementPropertiesTab.setProperty(InspectorGroup.PROPERTIES, "Message Code:", "" + messageCode);
        elementPropertiesTab.setProperty(InspectorGroup.PROPERTIES, "Message Type:", messageType);
        ActiveRuleAction qualification = ruleEditor.dragAndDropElement(RULE_QUALIFICATION);
        ruleEditor.selectRuleElementOnCanvas(qualification);
        elementPropertiesTab.setProperty(InspectorGroup.PROPERTIES, "Qualification:", "\"Assigned To\" = $USER$");
        ruleEditor.setRuleName(ruleDefinitionNameForShowMessage);
        ruleEditor.clickOnFreeSpaceOnCanvas();
        ruleWorkflow = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(wd, ruleEditor.getWorkflowContainer());
        ruleEditor.saveDefinition();
        ruleTab = ruleEditor.closeRule();
        ruleTab.refresh();
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "ruleDefinitionWithShowMessageActionSaved")
    @Features("[P541] Rule Designer")
    @Stories("US209111: platform Show Message action in the Rule Designer")
    @TestCaseId("TC945888, TC945889")
    @GUID("2d7c30ce-939c-4ab3-97ee-a8e0371e9c50")
    public void ruleDefinitionWithShowMessageActionSaved() {
        assertTrue(ruleTab.isRuleExistent(ruleDefinitionNameForShowMessage),
                "Rule Definition is not present on the Rule Definitions page.");
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "ruleWorkflowSameAfterSaving")
    @Features("[P541] Rule Designer")
    @Stories("US209111: platform Show Message action in the Rule Designer")
    @TestCaseId("TC945888, TC945889")
    @GUID("4c2997b6-d9ae-40fe-be7c-71d45e621cae")
    public void ruleWorkflowSameAfterSaving() {
        ruleEditor = ruleTab.openRuleDefinition(ruleDefinitionNameForShowMessage);
        Screenshot ruleWorkflowAfterReopening = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(wd, ruleEditor.getWorkflowContainer());
        ImageDiff diff = new ImageDiffer().makeDiff(ruleWorkflow, ruleWorkflowAfterReopening);
        assertFalse(diff.hasDiff(), "The rule workflow is not the same after saving.");
        ruleTab = ruleEditor.closeRule();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "showMessageJsonAfterSaving")
    @Features("[P541] Rule Designer")
    @Stories("US209111: platform Show Message action in the Rule Designer")
    @TestCaseId("TC945888, TC945889")
    @GUID("abbd01f9-395b-4ad7-bece-0449d3647f50")
    public void showMessageJsonAfterSaving() {
        ruleEditor = ruleTab.openRuleDefinition(ruleDefinitionNameForShowMessage);
        JsonEditorPage jsonEditorPage = ruleEditor.goToJsonEditor();
        String fullJsonOfRuleDefinition = jsonEditorPage.getJsonText();
        HashMap<String, Object> showMessageActionMap = JsonPath.from(fullJsonOfRuleDefinition).get("actions[0]");
        assertEquals(showMessageActionMap.get("resourceType"), "com.bmc.arsys.rx.services.rule.domain.ShowMessageAction");
        assertEquals(showMessageActionMap.get("name"), "Show Message");
        assertEquals(showMessageActionMap.get("messageText"), messageText);
        assertEquals(showMessageActionMap.get("messageCode"), messageCode);
        assertEquals(showMessageActionMap.get("messageType"), messageType);
        ruleTab = ruleEditor.closeRule();
    }
}