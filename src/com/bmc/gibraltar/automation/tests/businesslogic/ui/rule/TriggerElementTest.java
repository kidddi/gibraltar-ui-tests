package com.bmc.gibraltar.automation.tests.businesslogic.ui.rule;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.rule.ActiveRuleAction;
import com.bmc.gibraltar.automation.items.rule.TriggerEvent;
import com.bmc.gibraltar.automation.pages.RuleDefinitionsTabPage;
import com.bmc.gibraltar.automation.pages.RuleEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.util.List;
import java.util.stream.Stream;

import static com.bmc.gibraltar.automation.items.rule.RuleAction.TRIGGER;

public class TriggerElementTest extends AppManagerBaseTest {
    private RuleDefinitionsTabPage ruleTab;
    private RuleEditorPage ruleEditor;
    private TriggerEvent event = TriggerEvent.ON_CREATE;
    private String recordDefinitionName;
    private ActiveRuleAction trigger;

    @BeforeClass
    protected void goToRuleDesigner() {
        log.info("BeforeClass start for " + getClass().getSimpleName());
        ruleTab = new RuleDefinitionsTabPage(wd).navigateToPage();
        ruleEditor = ruleTab.initiateNewRule();
        //TODO: remove hardcoded record definition name. Note: record definition should be in a bundle
        recordDefinitionName = "Task";
        ruleEditor.selectPrimaryRecordDefinition(recordDefinitionName);
        trigger = ruleEditor.getDroppedComponent(TRIGGER);
        ruleEditor.selectRuleElementOnCanvas(trigger);
        ruleEditor.setTriggerEvent(event);
        log.info("BeforeClass end for " + getClass().getSimpleName());
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "formatOfLabelOfTriggerElement")
    @Features("[P541] Rule Designer")
    @Stories("US209232: Rule Designer UI - Continued")
    @TestCaseId("TC945744")
    @GUID("f9361816-8462-47f2-adf1-c324d36ef161")
    public void formatOfLabelOfTriggerElement() {
        String triggerLabel = ruleEditor.getTriggerLabelOnCanvas();
        assertEquals(triggerLabel, String.format("%s\n%s", event.getEvent(), recordDefinitionName),
                "Label format for Trigger event is wrong.");
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "triggerEventsOfTriggerElement")
    @Features("[P541] Rule Designer")
    @Stories("US209232: Rule Designer UI - Continued")
    @TestCaseId("TC945749")
    @GUID("1bd65ec9-4e18-4753-88fe-6097993f321c")
    public void triggerEventsOfTriggerElement() {
        ruleEditor.selectRuleElementOnCanvas(trigger);
        List<String> presentTriggerEvents = ruleEditor.getTriggerEvents();
        Stream.of(TriggerEvent.ON_CREATE, TriggerEvent.ON_UPDATE, TriggerEvent.ON_DELETE)
                .map(TriggerEvent::getEvent)
                .forEach(eventName -> assertTrue(presentTriggerEvents.contains(eventName),
                        eventName + " event is not present for the Trigger Element."));
    }
}