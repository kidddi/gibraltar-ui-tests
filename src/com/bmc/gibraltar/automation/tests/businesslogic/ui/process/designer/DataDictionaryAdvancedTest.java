package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.datadictionary.ConditionsEditor;
import com.bmc.gibraltar.automation.items.datadictionary.DataDictionary;
import com.bmc.gibraltar.automation.items.datadictionary.DictionaryGroup;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.element.Link;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.*;
import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataDictionaryAdvancedTest extends AppManagerBaseTest {
    private ProcessDefinitionEditorPage editView;
    private ProcessDefinitionsTabPage configurePage;

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyEditButtonForSourceFieldInOutputMapOfAllActivities")
    @Features("Data Dictionary")
    @Stories("US204060")
    @GUID("464c8f20-68a4-4348-b5c7-184ddb50d7cd")
    public void verifyEditButtonForSourceFieldInOutputMapOfAllActivities() {
        ProcessDefinitionsTabPage tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = tabPage.initiateNewProcess();
        List<ElementOfDesigner> processActions = ElementOfDesigner.getAllProcessActions(tabPage);
        for (ElementOfDesigner activity : processActions) {
            ActiveElement element = editView.dragAndDrop(activity);
            element.addOutputMap(new String[]{"name"})
                    .openDataDictionaryFor(OUTPUT_MAP_SOURCE)
                    .doubleClickVar(DictionaryGroup.GENERAL.CURRENT_USER)
                    .apply();
        }
        tabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifySupportDynamicQualificationForExpressionBuilder")
    @Features("Expression Builder")
    @Stories("US205211")
    @GUID("c4bd0ee5-557f-4cfc-a2d4-065581cf064a")
    public void verifySupportDynamicQualificationForExpressionBuilder() {
        ProcessDefinitionsTabPage tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = tabPage.initiateNewProcess();
        String inputProcVar = editView.addInputParam("some input");
        String outputProcVar = editView.addOutputParam("some OUTPUT");
        ActiveElement userTask = editView.dragAndDrop(USER_TASK);
        userTask.setPropertyByDefault(RECORD_DEFINITION);
        userTask.setProperty(COMPLETION_CRITERIA, "${process.some input}");
        ConditionsEditor expBuilder = (ConditionsEditor) userTask.openDataDictionaryFor(COMPLETION_CRITERIA);
        expBuilder.clickUseDynamicQualification();
        expBuilder.apply();
        verifyTrue(userTask.getPropertyValue(COMPLETION_CRITERIA).equals("EXTERNAL(${process.some input})"));
        userTask.setProperty(COMPLETION_CRITERIA, "");
        expBuilder.open().doubleClickVar(inputProcVar);
        expBuilder.clickUseDynamicQualification().clickOk().cancel();
        log.info("There should be SW00497107");
        verifyTrue(userTask.getPropertyValue(COMPLETION_CRITERIA).equals("EXTERNAL(${process.some input})"));
        userTask.setProperty(COMPLETION_CRITERIA, " ");
        expBuilder.open();
        expBuilder.setExpression("${process.some OUTPUT}");
        expBuilder.clickUseDynamicQualification().clickOk().cancel();
        log.info("There should be SW00497107");
        verifyTrue(userTask.getPropertyValue(COMPLETION_CRITERIA).equals("EXTERNAL(${process.some OUTPUT})"));
        userTask.setProperty(COMPLETION_CRITERIA, outputProcVar);
        expBuilder.open();
        expBuilder.verifyOperatorButtonsEnabling(true).clickUseDynamicQualification();
        expBuilder.verifyOperatorButtonsEnabling(false).clickUseDynamicQualification().apply();
        editView.verifyTrue(userTask.getPropertyValue(COMPLETION_CRITERIA).equals(outputProcVar));
        expBuilder.open();
        expBuilder.clickUseDynamicQualification().clickOk();
        verifyTrue(expBuilder.isErrorPresent("Invalid Value"), "Error is not displayed for not valid expression value.");
        expBuilder.cancel();
        tabPage.navigateToPage();
    }

    //TODO: add test to verify operator buttons presence

    @Test(groups = Groups.CATEGORY_FULL, description = "addingResultsOfUserAndReceiveTaskInDataDictionary")
    @Features("Data Dictionary")
    @Stories("US203095, US203096, US204063")
    @GUID("a98b3bb9-96dc-4c42-b63f-5ca7969c4c0d")
    public void addingResultsOfUserAndReceiveTaskInDataDictionary() {
        configurePage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = configurePage.initiateNewProcess();
        ActiveElement receiveTask1 = editView.dragAndDrop(RECEIVE_TASK);
        ActiveElement receiveTask2 = editView.dragAndDrop(RECEIVE_TASK);
        ActiveElement userTask = editView.dragAndDrop(USER_TASK);
        receiveTask1.setProperties(LABEL, PARAMETER_NAME, PARAMETER_NAME)
                .byValues(new String[]{"reciveTask1", "ParamName1", "ParamName2"});
        receiveTask2.setProperties(PARAMETER_NAME, PARAMETER_NAME)
                .byValues(new String[]{"ParamName3", "ParamName2"});
        userTask.setPropertyByDefault(RECORD_DEFINITION);
        FuncTwoVoid<ActiveElement, String> dataDictionary = (el, var) -> {
            DataDictionary dictionary = userTask.openDataDictionaryFor(RECORD_INSTANCE_ID).doubleClickVar(el, var);
            dictionary.verifyExpressionCorrectness();
            dictionary.clickOk();
            dictionary.verifyResultCorrectness();
            userTask.setProperty(RECORD_INSTANCE_ID, "");
        };
        DataDictionary dataDictionary1 = userTask.openDataDictionaryFor(RECORD_INSTANCE_ID)
                .doubleClickVar(DictionaryGroup.GENERAL.CURRENT_USER);
        dataDictionary1.verifyExpressionCorrectness();
        dataDictionary1.clickOk();
        dataDictionary1.verifyResultCorrectness();
        userTask.setProperty(RECORD_INSTANCE_ID, "");
        dataDictionary.that(userTask, "Notes");
        dataDictionary.that(receiveTask1, "ParamName1");
        dataDictionary.that(receiveTask2, "ParamName2");
        configurePage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyAllProcessVariablesAreSavingCorrectlyAndAlphabeticalOrdered")
    @Features("Data Dictionary")
    @Stories("US206060, US203098, US203089")
    @GUID("b1b9e837-e552-45b8-a385-7f3b75ea6e14")
    @Bug("SW00495549")
    @Issue("SW00495549: Data Dictionary. If to add expression $PROCESSCORRELATIONID$ into a property and save the process, the expression converts to $\\PROCESSCORRELATIONID$")
    public void verifyAllProcessVariablesAreSavingCorrectlyAndAlphabeticalOrdered() {
        configurePage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        log.info("Test Environment preparation");
        editView = configurePage.initiateNewProcess();
        String procName = "ProcessVariablesAreSavedCorrectly" + RandomStringUtils.randomAlphanumeric(7);
        editView.setProcessName(procName);
        String globalInputVar = editView.addInputParam("global Input Var");
        String globalOutputVar = editView.addOutputParam("global Output Var");
        ActiveElement element = editView.dragAndDrop(BUILD_COMPLETION_CONDITION);
        element.setOutputMap("ElementMapVar", "1", 0);
        element.setOutputMap(globalOutputVar, "2", 1);

        log.info("Test Data preparation");
        ElementProperties[] properties = {
                OUTPUT_MAP_SOURCE, APPROVER_LIST, COMPLETION_CRITERIA, COMPLETION_CRITERIA_PARAMETER, REQUEST_ID};
        Object[][] vars = {{DictionaryGroup.GENERAL.PROCESS_CORRELATION_ID},
                {DictionaryGroup.Legend.OUTPUT_MAP_VARIABLE, globalOutputVar},
                {globalInputVar},
                {DictionaryGroup.Legend.PROCESS_OUTPUT_PARAMETER, globalOutputVar},
                {"ElementMapVar"}};
        Map<ElementProperties, Object[]> map = new HashMap<>();
        String expression;
        int i = 0;
        for (Object[] var : vars) {
            map.put(properties[i++], var);
        }
        log.info("Alphabetical ordering assertion");
        List<String> realProcessVars = element.openDataDictionaryFor(APPROVER_LIST)
                .getAllVars(DictionaryGroup.PROCESS_VARIABLES.getName());
        editView.assertTrue(isAlphabetical(realProcessVars));

        log.info("At least! Test executing!");
        for (Map.Entry<ElementProperties, Object[]> elMap : map.entrySet()) {
            ElementProperties elProperty = elMap.getKey();
            element.openDataDictionaryFor(elProperty)
                    .doubleClickVar(elMap.getValue())
                    .clickOk()
                    .verifyResultCorrectness();
            expression = element.getPropertyValue(elProperty);
            log.info("Verify Correct result for property: " + elProperty + ". Entered var is: " + expression);
            map.replace(elProperty, new String[]{expression});
        }
        editView.bindElements(editView.getDroppedElement(START), element, editView.getDroppedElement(END))
                .saveProcess()
                .closeProcess();
        configurePage.openTheSavedProcess(procName);

        for (ElementProperties prop : map.keySet()) {
            expression = Arrays.toString(map.get(prop)).replace("[", "").replace("]", "");
            log.info("Verify Correct result after saving for property: " + prop + ". Expression var is: " + expression);
            if (!expression.equals("$PROCESSCORRELATIONID$"))  // SW00495549
                editView.assertTrue(element.getPropertyValue(prop).equals(expression));
        }
        configurePage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "showCallActivityResults")
    @Features("Data Dictionary")
    @Stories("US205407")
    @GUID("c81e9a84-db5d-47f7-90f7-e263b479f876")
    public void showCallActivityResults() {
        configurePage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = configurePage.initiateNewProcess();
        ActiveElement callActivity = editView.dragAndDrop(CALL_ACTIVITY);
        ActiveElement evaluateExpression = editView.dragAndDrop(EVALUATE_EXPRESSION);
        callActivity.setProperty(CALLED_PROCESS, "Approval Process");
        callActivity.setProperty(OUTPUT_MAP_NAME, "overAllProcessOutput");
        callActivity.setProperty(OUTPUT_MAP_SOURCE, "source");
        callActivity.setProperty(OUTPUT_MAP_NAME, "Some 123 test");
        callActivity.setProperty(OUTPUT_MAP_SOURCE, "source 123");
        DataDictionary dictionary = evaluateExpression.openDataDictionaryFor(EXPRESSION_TEXT)
                .doubleClickVar("overAllProcessOutput")
                .doubleClickVar("Some 123 test")
                .doubleClickVar(DictionaryGroup.GENERAL.CURRENT_USER)
                .doubleClickVar(evaluateExpression, "Output");
        dictionary.verifyExpressionCorrectness();
        dictionary.clickOk();
        dictionary.verifyResultCorrectness();
        configurePage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "userTaskExpressionForCompletionCriteria")
    @Features("Data Dictionary")
    @Stories("US204064")
    @GUID("808be079-38a9-49b3-84f6-881823c8aeab")
    public void userTaskExpressionForCompletionCriteria() {
        configurePage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = configurePage.initiateNewProcess();
        ActiveElement userTask = editView.dragNDropByCoordinates(USER_TASK, "330", "370");
        userTask.setProperty(RECORD_DEFINITION, "Task");
        userTask.openConditionsEditorFor(COMPLETION_CRITERIA)
                .doubleClickInTreeOnVar(DictionaryGroup.ACTIVITIES, new String[]{userTask.getLabel(), "Assigned To"})
                .apply();
        assertThat(userTask.getPropertyValue(COMPLETION_CRITERIA), equalTo("${userTask.Assigned To}"));
        configurePage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "userTaskExpressionInDataDictionary")
    @Features("Data Dictionary")
    @Stories("US204064")
    @GUID("b932e0fd-2660-430d-be5a-339e34d9210c")
    public void userTaskExpressionInDataDictionary() {
        configurePage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = configurePage.initiateNewProcess();
        ActiveElement userTask = editView.dragAndDrop(USER_TASK);
        userTask.setProperty(RECORD_DEFINITION, "Task");
        DataDictionary dictionary = userTask.openDataDictionaryFor(ASSIGNED_TO)
                .doubleClickVar(userTask, "Assigned To");
        dictionary.verifyExpressionCorrectness();
        dictionary.clickOk();
        dictionary.verifyResultCorrectness();
        configurePage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "conditionsEditorIsPresentForLink")
    @Features("Inspector")
    @Stories("US201857")
    @GUID("9cc80c98-3208-4461-82f0-4efe6670dbbc")
    public void conditionsEditorIsPresentForLink() {
        ProcessDefinitionsTabPage tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = tabPage.initiateNewProcess();
        ActiveElement end = editView.getDroppedElement(END);
        ActiveElement exclusiveGateway = editView.dragNDropByCoordinates(EXCLUSIVE_GATEWAY, "290", "270");
        Link link = editView.bindElements(exclusiveGateway, end);
        link.openConditionsEditorFor(CONDITION).close();
        tabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "selectionOfExistingProcessVariablesForOutputMapEntryName")
    @Features("Inspector")
    @Stories("US204061")
    @GUID("b38d5ee7-2ecf-49c0-9252-41e2fc16d869")
    @Bug("SW00497885")
    @Issue("SW00497885: Process Designer. The autocomplete function of the property Output Map Name doesn`t match \"space\" character")
    public void selectionOfExistingProcessVariablesForOutputMapEntryName() {
        ProcessDefinitionsTabPage tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = tabPage.initiateNewProcess();
        log.info("Test data preparation");
        String procVar[] = {"A input process param", "a output process param", "0 bbc output map", "1 bbc output map"};
        editView.addInputParam(procVar[0]);
        editView.addOutputParam(procVar[1]);
        ActiveElement bcc = editView.dragAndDrop(BUILD_COMPLETION_CONDITION);
        bcc.addOutputMap(new String[]{procVar[2]});
        bcc.addOutputMap(new String[]{procVar[3]});

        log.info("Test execution");
        List<String> dropDownList = bcc.getDropDownList(OUTPUT_MAP_NAME);
        verifyEquals(dropDownList, Arrays.asList(procVar));
        bcc.verifyAutoComplete(OUTPUT_MAP_NAME);
        ActiveElement cc = editView.dragAndDrop(CREATE_CONFIGURATION);
        cc.addOutputMap(new String[]{procVar[0]});
        dropDownList = cc.getDropDownList(OUTPUT_MAP_NAME);
        verifyEquals(dropDownList, Arrays.asList(procVar));
        tabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "improveOutputMappingForCreateRecordInstance")
    @Features("Data Dictionary")
    @Stories("US205397")
    @GUID("0bde3332-0161-4092-a79c-ad3126548111")
    public void improveOutputMappingForCreateRecordInstance() {
        log.info("Test data preparation");
        configurePage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = configurePage.initiateNewProcess();
        ActiveElement cri = editView.dragAndDrop(CREATE_RECORD_INSTANCE);
        ActiveElement helper = editView.dragAndDrop(BUILD_COMPLETION_CONDITION);
        RestDataProvider rest = new RestDataProvider();
        List<String> records = rest.getRecordDefinitionNames();
        cri.click();
        ElementPropertiesTab inspector = editView.getElementPropertiesTab();
        List<String> recordsListForCreateRecordElement = inspector
                .getAvailableOptionsFromDropdown(InspectorGroup.INPUT_MAP, ElementProperties.RECORD_DEFINITION_NAME.getName());
        assertTrue(recordsListForCreateRecordElement.containsAll(records));
        DataDictionary dict = helper.openDataDictionaryFor(APPROVER_LIST);
        dict.close();
        log.info("Test execution");
        for (int i = 0; i < 5; i++) {
            String record = records.get(RandomUtils.nextInt(0, records.size()));
            List<String> variablesExpected = rest.getRecordFields(record);
            int variableIndex = RandomUtils.nextInt(0, variablesExpected.size());
            String var = variablesExpected.get(variableIndex);
            inspector.setPropertyValue(cri, InspectorGroup.INPUT_MAP, RECORD_DEFINITION_NAME.getName(), record);
            List<String> variablesReal = dict.open().doubleClickVar(cri, var)
                    .getAllVars(cri.getExistPropertyValue(LABEL));
            dict.verifyResultCorrectness();
            verifyEquals(variablesReal, variablesExpected);
            dict.close();
        }
        configurePage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "calledProcessIdFieldEditableInCallActivityProperties")
    @Features("P534 - Process Designer")
    @Stories("US206695")
    @GUID("31a16de2-bcce-40fd-bda9-e59c2f2ab8ed")
    public void calledProcessIdFieldEditableInCallActivityProperties() {
        configurePage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = configurePage.initiateNewProcess();
        RestDataProvider provider = new RestDataProvider();
        List<String> processDefinitionNames = provider.getProcessDefinitionsNames(false);
        String processDefinitionName = processDefinitionNames.get(0);
        String id = provider.getProcessDefinitionID(processDefinitionName);
        ActiveElement callActivity = editView.dragAndDrop(CALL_ACTIVITY);
        callActivity.setProperty(CALLED_PROCESS, processDefinitionName);
        DataDictionary dictionary = callActivity.openDataDictionaryFor(CALLED_PROCESS);
        assertEquals(dictionary.getExpressionFieldValue(), id);
        String newProcessDefinitionName = processDefinitionNames.get(1);
        String newId = provider.getProcessDefinitionID(newProcessDefinitionName);
        dictionary.setExpression(true, newId);
        assertEquals(dictionary.getExpressionFieldValue(), newId);
        dictionary.clickOk();
        editView.confirmModalDialog(true);
        assertEquals(callActivity.getPropertyValue(CALLED_PROCESS), newProcessDefinitionName);
        configurePage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "errorIsDisplayedForNotValidCalledProcessId")
    @Features("P534 - Process Designer")
    @Stories("US206695")
    @GUID("baef92b1-dee0-4f84-baa3-4fb210a880db")
    public void errorIsDisplayedForNotValidCalledProcessId() {
        configurePage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = configurePage.initiateNewProcess();
        RestDataProvider provider = new RestDataProvider();
        List<String> processDefinitionNames = provider.getProcessDefinitionsNames(false);
        String processDefinitionName = processDefinitionNames.get(0);
        String id = provider.getProcessDefinitionID(processDefinitionName);
        ActiveElement callActivity = editView.dragAndDrop(CALL_ACTIVITY);
        callActivity.setProperty(CALLED_PROCESS, processDefinitionName);
        DataDictionary dictionary = callActivity.openDataDictionaryFor(CALLED_PROCESS);
        dictionary.setExpression(id.replaceFirst("\\d{2}", "99")).clickOk();
        assertTrue(dictionary.isErrorPresent("Invalid Value"), "Error is not displayed for not valid expression value.");
        dictionary.cancel();
        assertEquals(callActivity.getPropertyValue(CALLED_PROCESS), processDefinitionName);
        configurePage.navigateToPage();
    }
}