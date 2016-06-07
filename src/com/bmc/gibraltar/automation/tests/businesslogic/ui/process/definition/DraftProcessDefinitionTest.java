package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.definition;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.HashMap;
import java.util.List;

public class DraftProcessDefinitionTest extends AppManagerBaseTest {
    private RestDataProvider dataProvider = new RestDataProvider();
    private ProcessDefinitionsTabPage processDefinitionsTabPage;

    @Test(groups = Groups.CATEGORY_FULL, description = "disablingStartButtonForProcessDefinition")
    @Features("[P534] Process Designer")
    @Stories("US206697")
    @GUID("2dc5abbd-6a8c-42c0-bfa5-5c5d6716dfb9")
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
        //TODO: add assert that process definition marked as enabled on the Process Definitions page

        setProcessDefinitionState(processDefinitionName, true, false);
        //TODO: add assert that process definition marked as disabled on the Process Definitions page
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "enablingStartButtonForProcessDefinition")
    @Features("[P534] Process Designer")
    @Stories("US206697")
    @GUID("c45e2057-4648-423e-8827-7acd15c16d29")
    public void enablingStartButtonForProcessDefinition() {
        String processDefinitionName = "EnablingPD" + RandomStringUtils.randomAlphanumeric(15);
        new CommonSteps(dataProvider).createDisabledProcessDefinition(processDefinitionName);
        //TODO: add assert that process definition marked as disabled on the Process Definitions page
        setProcessDefinitionState(processDefinitionName, false, true);
        //TODO: add assert that process definition marked as enabled on the Process Definitions page
    }

    @Step
    private void setProcessDefinitionState(String processDefinitionName, boolean defaultState, boolean newState) {
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        ProcessDefinitionEditorPage editorPage = (ProcessDefinitionEditorPage) processDefinitionsTabPage
                .openTheSavedProcess(processDefinitionName);
        editorPage.waitForPageLoaded();
        ProcessPropertiesTab processPropertiesTab = editorPage.getProcessPropertiesTab();
        processPropertiesTab.verifyProcessDefinitionEnabled(defaultState);
        processPropertiesTab.setProcessDefinitionState(newState);
        editorPage.clickSaveButton().closeProcess();
    }
}