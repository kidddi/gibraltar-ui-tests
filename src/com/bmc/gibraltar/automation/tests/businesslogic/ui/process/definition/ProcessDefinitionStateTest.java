package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.definition;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.pages.ManageTabPage;
import com.bmc.gibraltar.automation.pages.ProcessViewerPage;
import com.bmc.gibraltar.automation.pages.StartProcessPage;
import com.bmc.gibraltar.automation.tests.TaskManagerBaseTest;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.HashMap;
import java.util.List;

public class ProcessDefinitionStateTest extends TaskManagerBaseTest {
    private RestDataProvider dataProvider = new RestDataProvider();

    @Test(groups = Groups.CATEGORY_FULL, description = "disablingStartButtonForProcessDefinition")
    @Features("Process Designer Editor")
    @Stories("US206697")
    @GUID("f4870360-adcc-48e4-b94c-0d044bb77899")
    public void disablingStartButtonForProcessDefinition() {
        String processDefinitionName = "DisablingPD" + RandomStringUtils.randomAlphanumeric(15);
        String processDefinitionJson = new CommonSteps(dataProvider)
                .createProcessDefinitionWithPermissionEntry(processDefinitionName, "READ");
        String processDefinitionId = JsonPath.from(processDefinitionJson).get("id");
        HashMap<String, String> inputs = new HashMap<>();
        List<String> inputsOfProcessDefinition = JsonPath.from(processDefinitionJson).get("inputParams.name");
        String processInputName = inputsOfProcessDefinition.get(0);
        inputs.put(processInputName, "text");
        dataProvider.startProcessWithInputs(processDefinitionId, inputs);

        //TODO: send api request to check the current state of the PD( should be enabled)
        // and then update process definition state to the disabled state
        ManageTabPage manageTabPage = new ManageTabPage(wd).navigateToPage();
        manageTabPage.verifyStartButtonDisabled(processDefinitionName);
        ProcessViewerPage processViewerPage = manageTabPage.openProcessInstanceLatest(processDefinitionName);
        processViewerPage.verifyProcName(processDefinitionName).close();
        List<String> enabledPDs = dataProvider.getProcessDefinitionsNamesByState(true);
        manageTabPage.selectProcess(enabledPDs.get(0));
        StartProcessPage startPage = manageTabPage.clickStart();
        startPage.waitForPageLoaded();
        List<String> allPDsOnStartNewProcessPage = startPage.getNamesOfProcessesInDropDown();
        verifyTrue(!allPDsOnStartNewProcessPage.contains(processDefinitionName));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "enablingStartButtonForProcessDefinition")
    @Features("Process Designer Editor")
    @Stories("US206697")
    @GUID("20397e73-8723-46ad-8a3d-5557533a802d")
    public void enablingStartButtonForProcessDefinition() {
        String processDefinitionName = "EnablingPD" + RandomStringUtils.randomAlphanumeric(15);
        new CommonSteps(dataProvider).createDisabledProcessDefinition(processDefinitionName);

        //TODO: send api request to check the current state of the PD (should be disabled)
        // and then update process definition state to the enabled state
        ManageTabPage manageTabPage = new ManageTabPage(wd).navigateToPage();
        manageTabPage.verifyStartButtonEnabled(processDefinitionName);
        manageTabPage.selectProcess(processDefinitionName);
        StartProcessPage startPage = manageTabPage.clickStart();
        startPage.waitForPageLoaded();
    }
}