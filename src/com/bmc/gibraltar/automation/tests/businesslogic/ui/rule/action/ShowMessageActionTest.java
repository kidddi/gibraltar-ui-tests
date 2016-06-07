package com.bmc.gibraltar.automation.tests.businesslogic.ui.rule.action;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.datadictionary.RuleDictionary;
import com.bmc.gibraltar.automation.items.rule.ActiveRuleAction;
import com.bmc.gibraltar.automation.items.rule.TriggerEvent;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.PropertyTab;
import com.bmc.gibraltar.automation.pages.RuleDefinitionsTabPage;
import com.bmc.gibraltar.automation.pages.RuleEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.util.List;

import static com.bmc.gibraltar.automation.items.rule.RuleAction.SHOW_MESSAGE;
import static com.bmc.gibraltar.automation.items.rule.RuleAction.TRIGGER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;

public class ShowMessageActionTest extends AppManagerBaseTest {
    private RuleDefinitionsTabPage ruleTab;
    private RuleEditorPage ruleEditor;
    private ActiveRuleAction showMessage;
    private PropertyTab elementPropertiesTab;

    @BeforeClass
    protected void goToRuleDesigner() {
        log.info("BeforeClass start.");
        ruleTab = new RuleDefinitionsTabPage(wd).navigateToPage();
        ruleEditor = ruleTab.initiateNewRule();
        ruleEditor.selectPrimaryRecordDefinition("Task");
        ActiveRuleAction trigger = ruleEditor.getDroppedComponent(TRIGGER);
        ruleEditor.selectRuleElementOnCanvas(trigger);
        ruleEditor.setTriggerEvent(TriggerEvent.ON_UPDATE);
        showMessage = ruleEditor.dragAndDropElement(SHOW_MESSAGE);
        elementPropertiesTab = ruleEditor.getInspector();
        log.info("BeforeClass end.");
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "dictionaryForMessageTextProperty")
    @Features("[P541] Rule Designer")
    @Stories("US209111: platform Show Message action in the Rule Designer")
    @TestCaseId("TC945887")
    @GUID("9416656f-3b13-48c7-8740-e60bf9d61459")
    public void dictionaryForMessageTextProperty() {
        RuleDictionary ruleDictionary = new RuleDictionary(wd, showMessage, "Message Text:");
        ruleDictionary.open();
        assertTrue(ruleDictionary.isDisplayed(), "Rule Dictionary is not displayed.");
        ruleDictionary.close();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "propertiesOfShowMessageElement")
    @Features("[P541] Rule Designer")
    @Stories("US209111: platform Show Message action in the Rule Designer")
    @TestCaseId("TC945887")
    @GUID("ea218d1e-0f60-407d-99d6-ef429b030845")
    public void propertiesOfShowMessageElement() {
        List<String> propertiesOfElement = elementPropertiesTab.getPropertiesList(InspectorGroup.PROPERTIES);
        assertThat("Not all properties are present for the Show Message element.", propertiesOfElement,
                hasItems("Label:", "Message Text:", "Message Code:", "Message Type:"));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "messageCodeDefaultValue")
    @Features("[P541] Rule Designer")
    @Stories("US209111: platform Show Message action in the Rule Designer")
    @TestCaseId("TC945887")
    @GUID("48d080b2-6d91-40b9-997b-398d03703493")
    public void messageCodeDefaultValue() {
        //TODO: need to add additional tests for message code field (positive integer equal or greater than 10000)
        assertEquals(elementPropertiesTab.getPropertyValue(InspectorGroup.PROPERTIES, "Message Code:"), "10000",
                "Default value for the 'Message Code' is wrong.");
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "messageTypeAvailableOptions")
    @Features("[P541] Rule Designer")
    @Stories("US209111: platform Show Message action in the Rule Designer")
    @TestCaseId("TC945887")
    @GUID("44ce54c6-80bc-42ea-af9f-37acfa1596b2")
    public void messageTypeAvailableOptions() {
        List<String> options = elementPropertiesTab.getAvailableOptionsFromDropdown(InspectorGroup.PROPERTIES, "Message Type:");
        assertThat("Not all options are present for the 'Message Type' dropdown.", options,
                hasItems("NOTE", "WARNING", "ERROR"));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "defaultMessageType")
    @Features("[P541] Rule Designer")
    @Stories("US209111: platform Show Message action in the Rule Designer")
    @TestCaseId("TC945887")
    @GUID("c8565f38-dcf0-4de4-b60a-33d743e338e2")
    public void defaultMessageType() {
        String defaultValue = elementPropertiesTab.getPropertyValue(InspectorGroup.PROPERTIES, "Message Type:");
        assertThat("Default 'Message Type' value is wrong.", defaultValue, equalTo("NOTE"));
    }
}