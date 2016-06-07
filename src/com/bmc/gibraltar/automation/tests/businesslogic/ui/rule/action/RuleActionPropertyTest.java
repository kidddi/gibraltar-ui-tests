package com.bmc.gibraltar.automation.tests.businesslogic.ui.rule.action;

import com.bmc.gibraltar.automation.dataprovider.ElementsProperties;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.record.RecordField;
import com.bmc.gibraltar.automation.items.rule.ActiveRuleAction;
import com.bmc.gibraltar.automation.items.rule.RuleAction;
import com.bmc.gibraltar.automation.items.rule.TriggerEvent;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.PropertyTab;
import com.bmc.gibraltar.automation.pages.RuleDefinitionsTabPage;
import com.bmc.gibraltar.automation.pages.RuleEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;
import static com.bmc.gibraltar.automation.items.rule.RuleAction.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class RuleActionPropertyTest extends AppManagerBaseTest {
    private RuleDefinitionsTabPage ruleTab;
    private RuleEditorPage ruleEditor;

    @DataProvider
    public static Object[][] labelOfRuleElements() {
        return new Object[][]{
                new Object[]{RULE_QUALIFICATION, "label for qualification"},
                new Object[]{SHOW_MESSAGE, "label for show message"},
        };
    }

    @DataProvider
    public static Object[][] actionAndProperties() {
        return new Object[][]{
                new Object[]{CREATE_RECORD_INSTANCE, new String[]{RECORD_DEFINITION_NAME.getName()}},
                new Object[]{UPDATE_RECORD_INSTANCE,
                        new String[]{RECORD_DEFINITION_NAME.getName(), RECORD_INSTANCE_ID.getName()}},
        };
    }

    @BeforeMethod
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

    @Test(dataProvider = "labelOfRuleElements", groups = Groups.CATEGORY_FULL, description = "labelOfActionOnCanvas")
    @Features("[P541] Rule Designer")
    @Stories("US209232: Rule Designer UI - Continued")
    @TestCaseId("TC945746, TC945748")
    @GUID("2aa65793-a8c6-43f6-bd9b-1018e995eec6")
    public void labelOfActionOnCanvas(RuleAction action, String expectedLabel) {
        testDescription = "Verifies that " + action + " has '" + expectedLabel + "' label after it dropped on Canvas.";
        ActiveRuleAction element = ruleEditor.dragAndDropElement(action);
        ruleEditor.selectRuleElementOnCanvas(element);
        PropertyTab elementPropertiesTab = ruleEditor.getInspector();
        elementPropertiesTab.setProperty(InspectorGroup.PROPERTIES, LABEL.getName(), expectedLabel);
        String labelOnCanvas = ruleEditor.getElementLabelOnCanvas(element);
        assertEquals(labelOnCanvas, expectedLabel, action + " element on Canvas doesn't contain the expected label.");
    }

    @Test(dataProvider = "actionAndProperties", groups = Groups.CATEGORY_FULL, description = "inputMapFieldsForRecordActions")
    @Features("[P541] Rule Designer")
    @Stories("US209214: Custom actions in the Rule Designer")
    @TestCaseId("TC945879")
    @GUID("d88a85bf-ff99-4668-9b01-5ea27c41ec3e")
    public void inputMapFieldsForRecordActions(RuleAction action, String[] defaultProperties) {
        testDescription = "Verifies that " + action + " has '" + defaultProperties + "' properties in Inspector.";
        ActiveRuleAction element = ruleEditor.dragAndDropElement(action);
        ruleEditor.selectRuleElementOnCanvas(element);
        PropertyTab elementPropertiesTab = ruleEditor.getInspector();
        RestDataProvider dataProvider = new RestDataProvider();
        List<String> recordDefinitionNames = dataProvider.getRecordDefinitionNames();
        String recordDefinition = recordDefinitionNames.get(RandomUtils.nextInt(0, recordDefinitionNames.size()));
        elementPropertiesTab.setProperty(InspectorGroup.INPUT_MAP, RECORD_DEFINITION_NAME.getName(), recordDefinition);
        List<String> systemFields = dataProvider.getRecordFieldsNamesByFieldOption(RecordField.Option.SYSTEM, recordDefinition);
        List<String> expectedFields = dataProvider.getRecordFields(recordDefinition)
                .stream().filter(field -> !systemFields.contains(field))
                .map(field -> field + ":").collect(Collectors.toList());
        List<String> presentFields = elementPropertiesTab.getPropertiesList(InspectorGroup.INPUT_MAP);
        presentFields.removeAll(Arrays.asList(defaultProperties));
        expectedFields.stream().forEach(expectedField -> assertTrue(presentFields.contains(expectedField)));
        assertThat("Record fields are not present in the Input Map.", expectedFields.size(), equalTo(presentFields.size()));
    }

    @Test(dataProvider = "actionElementProperties", dataProviderClass = ElementsProperties.class,
            groups = Groups.CATEGORY_FULL, description = "ruleActionsPropertiesPresenceInInspector")
    @Features("[P541] Rule Designer")
    @Stories("US209214: Custom actions in the Rule Designer")
    @TestCaseId("TC945879, TC952780")
    @GUID("b4ba0e85-b51c-4ad5-8bb2-6fd23ae53904")
    public void ruleActionsPropertiesPresenceInInspector(RuleAction action, int defaultGroupsCount,
                                                         List<ElementsProperties.Group> propertiesGroups) {
        testDescription = "Verifies that " + action + " has '" + defaultGroupsCount + "' groups in Inspector and " +
                " verifies default properties in each group.";
        ActiveRuleAction element = ruleEditor.dragAndDropElement(action);
        ruleEditor.selectRuleElementOnCanvas(element);
        PropertyTab elementPropertiesTab = ruleEditor.getInspector();
        List<String> presentInspectorGroups = elementPropertiesTab.getGroupsList();
        assertThat("Number of default groups is wrong.", presentInspectorGroups.size(), equalTo(defaultGroupsCount));
        elementPropertiesTab.expandAllGroups();
        for (ElementsProperties.Group singleGroup : propertiesGroups) {
            String groupName = singleGroup.getGroupName();
            assertTrue(presentInspectorGroups.contains(groupName), groupName + " group is not present in Inspector.");
            elementPropertiesTab.expandAllGroups();
            InspectorGroup group = singleGroup.getGroup();
            List<String> presentProperties = elementPropertiesTab.getPropertiesList(group);
            assertThat("Count of default properties in " + groupName + " is wrong.",
                    presentProperties.size(), equalTo(singleGroup.getLabelsCount()));
            assertThat("Not all default properties present in " + groupName,
                    presentProperties, hasItems(singleGroup.getLabels()));
            Arrays.asList(singleGroup.getRequiredLabels())
                    .forEach(property -> assertTrue(elementPropertiesTab
                                    .isPropertyMarkedAsRequired(group, property),
                            property + " is not marked as required in " + groupName));
        }
    }
}