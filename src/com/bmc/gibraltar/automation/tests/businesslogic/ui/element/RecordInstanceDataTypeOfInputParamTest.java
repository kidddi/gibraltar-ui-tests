package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.datadictionary.DictionaryGroup;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.parameter.ProcessParameter;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.pages.ManageTabPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.pages.StartProcessPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.ArrayList;
import java.util.List;

import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;

public class RecordInstanceDataTypeOfInputParamTest extends AppManagerBaseTest {
    private List<String> processDefinitionNames = new ArrayList<>();

    @DataProvider(name = "recordInstanceInput")
    public static Object[][] recordInstanceInput() {
        return new Object[][]{
                new Object[]{new ProcessParameter(
                        "Use Sample", DataType.RECORD_INSTANCE, true, "Task", false, "Helper ")},
                new Object[]{new ProcessParameter(
                        "Not Use Sample", DataType.RECORD_INSTANCE, false, "Task", false, "Helper ")}
        };
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US205209: useProcessInputWithTheRecordInstanceDataTypeInExpressions")
    @Features("Data Dictionary")
    @Stories("US205209")
    @GUID("0bc28a3b-091d-4361-92c6-8c25c6674a96")
    @Bug("")
    public void useProcessInputWithTheRecordInstanceDataTypeInExpressions() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        ProcessDefinitionEditorPage processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        ProcessPropertiesTab inspector = processDefinitionEditorPage.getProcessPropertiesTab();
        String processDefinitionName = "RecInst" + Long.toHexString(System.currentTimeMillis());
        inspector.setProcessName(processDefinitionName);
        ProcessParameter inputParam = new ProcessParameter(
                "Task Record", DataType.RECORD_INSTANCE, true, "Task", false, "Helper ");
        inspector.addProcessParameter(InspectorGroup.INPUT_PARAMETERS, inputParam);
        ActiveElement start = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.START);
        ActiveElement userTask = processDefinitionEditorPage.dragAndDropToCanvas(ElementOfDesigner.USER_TASK);
        ActiveElement end = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.END);
        processDefinitionEditorPage.bindElements(start, userTask, end);

        userTask.setProperty(RECORD_DEFINITION, "Task");
        userTask.openConditionsEditorFor(COMPLETION_CRITERIA)
                .doubleClickInTreeOnVar(DictionaryGroup.PROCESS_VARIABLES, new String[]{inputParam.getName(), "Submitter"})
                .apply();
        verifyEquals(userTask.getPropertyValue(COMPLETION_CRITERIA),
                "${process." + inputParam.getName() + ".Submitter}");
        userTask.setProperty(COMPLETION_CRITERIA, "${process." + inputParam.getName() + ".Submitter} = $\\USER$");

        userTask.openDataDictionaryFor(PRIORITY)
                .doubleClickInTreeOnVar(DictionaryGroup.OPTIONS, new String[]{"Medium"}).apply();
        processDefinitionEditorPage.saveProcess();
        verifyEquals(userTask.getPropertyValue(COMPLETION_CRITERIA),
                "${process." + inputParam.getName() + ".Submitter} = $\\USER$");
        processDefinitionEditorPage.closeProcess();
    }

    @Test(dataProvider = "recordInstanceInput", groups = Groups.CATEGORY_FULL, description = "US205209: processInputWithRecordInstanceDataType")
    @Features("Data Dictionary")
    @Stories("US205209")
    @GUID("df961425-be4b-4482-bfda-4a34579a32ae")
    @Bug("")
    public void processInputWithRecordInstanceDataType(ProcessParameter parameter) {
        this.testDescription = "Verifies a process definition with the 'Record Instance' process input that has " +
                "the 'Use Sample Data' as: " + parameter.isUseSampleData() + " can be saved.";
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        ProcessDefinitionEditorPage processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        ProcessPropertiesTab inspector = processDefinitionEditorPage.getProcessPropertiesTab();

        String processDefName = parameter.getName() + Long.toHexString(System.currentTimeMillis());
        inspector.setProcessName(processDefName);
        inspector.addProcessParameter(InspectorGroup.INPUT_PARAMETERS, parameter);
        ActiveElement start = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.START);
        ActiveElement end = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.END);
        processDefinitionEditorPage.bindElements(start, end);
        processDefinitionEditorPage.saveProcess().closeProcess();
        processDefinitionNames.add(processDefName);
    }

    @Test(dependsOnMethods = "processInputWithRecordInstanceDataType", groups = Groups.CATEGORY_FULL, description = "US205209: startProcessInstance")
    @Features("Data Dictionary")
    @Stories("US205209")
    @GUID("c0581745-f91a-48e9-a10c-1639419646b7")
    @Bug("")
    public void startProcessInstance() {
        for (String processDefinitionName : processDefinitionNames) {
            ManageTabPage manageTabPage = new ManageTabPage(wd).navigateToPage();
            manageTabPage.selectProcess(processDefinitionName);
            int processCount = manageTabPage.getTasksAmount();
            StartProcessPage startProcessPage = manageTabPage.openProcessStart(processDefinitionName);
            startProcessPage.fillInputField("Id", "1");
            startProcessPage.fillInputField("Record Definition Name", "task-manager:Task");
            manageTabPage = startProcessPage.startTheProcess();
            manageTabPage.verifyCountOfProcesses(processDefinitionName, ++processCount);
        }
    }
}