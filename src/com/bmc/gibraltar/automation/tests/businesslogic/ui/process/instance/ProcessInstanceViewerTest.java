package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.instance;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.tab.ProcessInstanceViewerTabs;
import com.bmc.gibraltar.automation.pages.ManageTabPage;
import com.bmc.gibraltar.automation.pages.ProcessViewerPage;
import com.bmc.gibraltar.automation.tests.TaskManagerBaseTest;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProcessInstanceViewerTest extends TaskManagerBaseTest {
    private CommonSteps commonSteps;
    private RestDataProvider dataProvider = new RestDataProvider();
    private String processName = "Viewer-" + RandomStringUtils.randomAlphanumeric(3);
    private String processInstanceInfo;
    private String processInstanceId;
    private ManageTabPage manageTab;
    private ProcessViewerPage processInstanceViewer;
    private ElementOfDesigner userTask = ElementOfDesigner.USER_TASK;

    @BeforeClass
    public void createProcessDefinitionAndStartProcess() {
        commonSteps = new CommonSteps(dataProvider);
        String processDefinitionJson = commonSteps.createCustomProcessDefinition(processName,
                "processDefinitionWithInputsOutputs.json");
        String processDefinitionId = JsonPath.from(processDefinitionJson).get("id");
        List<String> inputs = JsonPath.from(processDefinitionJson).get("inputParams.name");
        HashMap<String, String> inputsMap = new HashMap<>();
        inputsMap.put(inputs.get(0), "ONE");
        processInstanceInfo = dataProvider.startProcessWithInputs(processDefinitionId, inputsMap);
        manageTab = new ManageTabPage(wd).navigateToPage();
        manageTab.waitForPageLoaded();
        processInstanceId = JsonPath.from(processInstanceInfo).get("instanceId");
        processInstanceViewer = manageTab.openProcessInstanceById(processName, processInstanceId);
    }

    @AfterClass
    protected void closeInstanceViewer() {
        processInstanceViewer.close();
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "verifyProcessInstanceViewerPresent")
    @Features("Process Instance Viewer")
    @Stories("US204280")
    @GUID("417608bb-045b-4f8f-80fb-4fcce4e9c530")
    public void verifyProcessInstanceViewerPresent() {
        processInstanceViewer
                .verifyProcName(processName)
                .verifyElementByLabel(userTask.getName())
                .verifyElementIsActive(userTask.getName());
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "validateInstanceViewerGeneralInfo")
    @Features("Process Instance Viewer")
    @Stories("US204280")
    @GUID("87b00fec-faec-406d-b6d3-68952ffe4b6f")
    public void validateInstanceViewerGeneralInfo() {
        int actualGroupCount = processInstanceViewer.toInspectorTab(ProcessInstanceViewerTabs.Tab.PROCESS_INFORMATION).getGroupsCount();
        assertThat("Number of groups in the Process Information tab is wrong.", actualGroupCount, equalTo(0));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "validateInstanceViewerProcessVariablesGroups")
    @Features("Process Instance Viewer")
    @Stories("US204280")
    @Issue("SW00495625: 'Other Variables' Group in the right blade of the Process Instance viewer contains GUID")
    @GUID("5e97905f-28be-4884-aa5c-ee82ee51121e")
    @Bug("SW00495625")
    public void validateInstanceViewerProcessVariablesGroups() {
        String[] expectedGroups = {"PROCESS INPUTS", "PROCESS OUTPUTS"};
        List<String> groups = processInstanceViewer.toInspectorTab(ProcessInstanceViewerTabs.Tab.PROCESS_VARIABLES)
                .getGroupsList();
        assertThat("Not all expected groups are present.", groups, hasItems(expectedGroups));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "validateInstanceViewerProcessVariablesGroupsCount")
    @Features("Process Instance Viewer")
    @Stories("US204280")
    @Issue("SW00495625: 'Other Variables' Group in the right blade of the Process Instance viewer contains GUID")
    @GUID("a880cb6f-7b32-4107-864b-5c11e5248632")
    @Bug("SW00495625")
    public void validateInstanceViewerProcessVariablesGroupsCount() {
        int actualGroupCount = processInstanceViewer.toInspectorTab(ProcessInstanceViewerTabs.Tab.PROCESS_VARIABLES).getGroupsCount();
        assertThat("Number of groups is wrong.", actualGroupCount, equalTo(2));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "validateInstanceViewerActivityResults")
    @Features("Process Instance Viewer")
    @Stories("US204280")
    @Issue("SW00496471: 'Activity Results' Tab in the right blade of the Process Instance viewer contains 'Activity Results' group")
    @GUID("be469699-5172-4bb4-b0d4-d6ad93aaa56d")
    @Bug("SW00496471")
    public void validateInstanceViewerActivityResults() {
        ProcessInstanceViewerTabs viewerTab = processInstanceViewer.toActivityResultsTab(userTask.getName());
        List<String> groupsInInspector = viewerTab.getGroupsList();
        verifyTrue(groupsInInspector.contains("ACTIVITY INFORMATION"),
                "Not all expected groups are present in the process instance viewer tab.");
        assertEquals(viewerTab.getGroupsCount(), 1, "Number of groups is wrong.");
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyHighlightingOfActivities")
    @Features("Process Instance Viewer")
    @Stories("US204280")
    @GUID("590b1ecb-9e68-4abf-ac1b-515d5dd35b83")
    public void verifyHighlightingOfActivities() {
        processInstanceViewer.verifyElementIsActive(userTask.getName());
        processInstanceViewer.close();
        String guid = CommonSteps.getIdFromProcessVariablesOfStartedInstance(processInstanceInfo).get(0);
        dataProvider.updateTask(guid, "7", "Completed");
        // verifying if the User Task element highlighted as 'Completed'
        processInstanceViewer = manageTab.openProcessInstanceById(processName, processInstanceId);
        processInstanceViewer.verifyElementActivityDone(userTask.getName());
    }
}