package com.bmc.gibraltar.automation.tests.businesslogic.ui.rule.action;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
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
import com.jayway.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.util.Arrays;
import java.util.List;

import static com.bmc.gibraltar.automation.items.rule.RuleAction.START_PROCESS;
import static com.bmc.gibraltar.automation.items.rule.RuleAction.TRIGGER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class StartProcessActionTest extends AppManagerBaseTest {
    private RestDataProvider dataProvider;
    private RuleDefinitionsTabPage ruleTab;
    private RuleEditorPage ruleEditor;
    private String processDefinitionName;
    private List<String> processInputs;
    private String primaryRecordDefinitionName = "Task";
    private List<String> primaryRecordFields;
    private RuleDictionary ruleDictionary;

    @BeforeClass
    protected void createDefinition() {
        dataProvider = new RestDataProvider();
        processDefinitionName = "PD-with-inputs-" + RandomStringUtils.randomAlphanumeric(8);
        String processDefinitionJson = new CommonSteps(dataProvider).createCustomProcessDefinition(processDefinitionName,
                "processDefinitionWithInputsOutputs.json");
        processInputs = JsonPath.from(processDefinitionJson).get("inputParams.name");
        primaryRecordFields = dataProvider.getRecordFields(primaryRecordDefinitionName);
    }

    @BeforeMethod
    protected void goToRuleDesigner() {
        log.info("BeforeClass start.");
        ruleTab = new RuleDefinitionsTabPage(wd).navigateToPage();
        ruleEditor = ruleTab.initiateNewRule();
        ruleEditor.selectPrimaryRecordDefinition(primaryRecordDefinitionName);
        ActiveRuleAction trigger = ruleEditor.getDroppedComponent(TRIGGER);
        ruleEditor.selectRuleElementOnCanvas(trigger);
        ruleEditor.setTriggerEvent(TriggerEvent.ON_CREATE);
        log.info("BeforeClass end.");
    }

    @AfterMethod
    protected void closeDictionary() {
        log.info("AfterMethod start");
        if (ruleDictionary != null && ruleDictionary.isDisplayed())
            ruleDictionary.close();
        log.info("AfterMethod end");
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "expressionBuilderForProcessToStartField")
    @TestCaseId("TC952780")
    @Features("Process Instance Viewer")
    @Stories("US204280")
    @GUID("a957b939-ef7d-4933-8d9b-4af9ec739f58")
    public void expressionBuilderForProcessToStartField() {
        ActiveRuleAction element = ruleEditor.dragAndDropElement(START_PROCESS);
        ruleEditor.selectRuleElementOnCanvas(element);
        PropertyTab elementPropertiesTab = ruleEditor.getInspector();
        String expression = "${ruleContext.testString}";
        String fieldName = "Process To Start:";
        elementPropertiesTab.setProperty(InspectorGroup.PROPERTIES, fieldName, expression);
        String fieldValue = elementPropertiesTab.getPropertyValue(InspectorGroup.PROPERTIES, fieldName);
        assertThat("Expression is not displayed for the Process To Start.", expression, equalTo(fieldValue));
        elementPropertiesTab.setProperty(InspectorGroup.PROPERTIES, fieldName, processDefinitionName);
        ruleEditor.confirmModalDialog(true);
        String processToStartValue = elementPropertiesTab.getPropertyValue(InspectorGroup.PROPERTIES, fieldName);
        assertThat("Process Definition was not chosen for the Process To Start.", processToStartValue,
                equalTo(processDefinitionName));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "inputMapOfProcessToStartAction")
    @Features("[P541] Rule Designer")
    @Stories("US209229: platform Start Process action in the Rule Designer")
    @TestCaseId("TC952780")
    @GUID("81b4d947-1bf5-429c-b13d-d433e8ddd0ce")
    public void inputMapOfProcessToStartAction() {
        ActiveRuleAction element = ruleEditor.dragAndDropElement(START_PROCESS);
        ruleEditor.selectRuleElementOnCanvas(element);
        PropertyTab elementPropertiesTab = ruleEditor.getInspector();
        String fieldName = "Process To Start:";
        elementPropertiesTab.setProperty(InspectorGroup.PROPERTIES, fieldName, processDefinitionName);
        assertTrue(elementPropertiesTab.getInspectorGroupsList().contains(InspectorGroup.INPUT_MAP),
                "Inspector does not contain the Input Map group.");
        List<String> inputProperties = elementPropertiesTab.getPropertiesList(InspectorGroup.INPUT_MAP);
        processInputs.stream().forEach(property -> assertThat("Input Map group doesn't contain a process input.",
                inputProperties, hasItem(property)));
        for (String property : processInputs) {
            ruleDictionary = new RuleDictionary(wd, element, property).open();
            assertTrue(ruleDictionary.getGroupsPresentInDataDictionary()
                    .containsAll(Arrays.asList("General", primaryRecordDefinitionName + " Fields")));
            ruleDictionary.expandAllGroups();
            List<String> recordFields = ruleDictionary.getAllVars(primaryRecordDefinitionName + " Fields");
            assertTrue(recordFields.containsAll(primaryRecordFields),
                    "Fields of the primary record definition is not present for the property: " + property);
            ruleDictionary.close();
        }
    }
}