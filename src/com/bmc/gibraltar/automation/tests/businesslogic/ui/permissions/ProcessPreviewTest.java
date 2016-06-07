package com.bmc.gibraltar.automation.tests.businesslogic.ui.permissions;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.Application;
import com.bmc.gibraltar.automation.pages.LoginPage;
import com.bmc.gibraltar.automation.pages.ManageTabPage;
import com.bmc.gibraltar.automation.pages.ProcessViewerPage;
import com.bmc.gibraltar.automation.pages.StartProcessPage;
import com.bmc.gibraltar.automation.tests.BaseTest;
import com.jayway.restassured.path.json.JsonPath;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ProcessPreviewTest extends BaseTest {
    private static List<Object[]> data = new ArrayList<>();
    private RestDataProvider dataProvider = new RestDataProvider("jonnie@pepsi.com", "password");
    private String inputName;
    private ManageTabPage manageTabPage;
    // TODO: verify these tests when SW00499961 fixed

    @DataProvider(name = "processDefAndPreviewState")
    public static Iterator<Object[]> processDefAndPreviewState() {
        return data.iterator();
    }

    @BeforeClass
    public void createDefinitionAndLogin() {
        String userName = "manager";
        dataProvider.createUser(userName, userName, "password", userName, new String[]{"Process Manager"});
        String[] permissionTypes = {"Read", "Execute"};
        for (String permission : permissionTypes) {
            String processDefinitionName = permission + Long.toHexString(System.currentTimeMillis());
            String processDefinitionJson = new CommonSteps(dataProvider)
                    .createProcessDefinitionWithPermissionEntry(processDefinitionName, permission);
            String processDefinitionId = JsonPath.from(processDefinitionJson).get("id");
            boolean previewState = false;
            if (permission.startsWith("Read")) {
                previewState = true;
            }
            ArrayList<String> inputsNames = JsonPath.from(processDefinitionJson).get("inputParams.name");
            inputName = inputsNames.get(0);
            data.add(new Object[]{processDefinitionName, previewState});
            dataProvider.logout();
            RestDataProvider restProvider = new RestDataProvider("manager@pepsi.com", "password");
            HashMap<String, String> inputs = new HashMap<>();
            inputs.put(inputName, inputName);
            restProvider.startProcessWithInputs(processDefinitionId, inputs);
            restProvider.logout();
        }

        LoginPage loginPage = new LoginPage(wd, Application.TASK_MANAGER).navigateToPage();
        loginPage.loginTaskManager("manager@pepsi.com", "password");
        manageTabPage = new ManageTabPage(wd).navigateToPage();
    }

    @Test(dataProvider = "processDefAndPreviewState", groups = Groups.CATEGORY_SANITY, description = "processPreviewOnStartNewProcessPages")
    @Features("Permissions")
    @Stories("US206694")
    @GUID("2516e4fb-3694-484c-bc7d-a21740ebd0c5")
    public void processPreviewOnStartNewProcessPage(String processDefinitionName, boolean previewState) {
        testDescription = "Verifies the process definition: "
                + processDefinitionName + " has preview on the Start New Process page.";
        log.info("Checking the process preview for the PD: " + processDefinitionName);
        manageTabPage.selectProcess(processDefinitionName);
        int processCount = manageTabPage.getTasksAmount();
        StartProcessPage startProcessPage = manageTabPage.clickStart();
        startProcessPage.waitForPageLoaded();
        startProcessPage.verifyProcessPreview(previewState);
        List<String> requiredInputs = startProcessPage.getListOfRequiredProcInputs();
        verifyTrue(requiredInputs.size() == 1);
        startProcessPage.fillInputField(inputName, inputName);
        manageTabPage = startProcessPage.startTheProcess();
        manageTabPage.verifyCountOfProcesses(processDefinitionName, ++processCount);
    }

    @Test(dataProvider = "processDefAndPreviewState", groups = Groups.CATEGORY_SANITY, description = "processInstanceViewerPresence")
    @Features("Permissions")
    @Stories("US206694")
    @GUID("4e4c5047-3640-455a-90a6-1d163eeb8527")
    public void processInstanceViewerPresence(String processDefinitionName, boolean previewState) {
        testDescription = "Verifies if the process definition: "
                + processDefinitionName + " has the UI Process Instance viewer.";
        manageTabPage = manageTabPage.navigateToPage();
        manageTabPage.waitForPageLoaded();
        ProcessViewerPage processViewerPage = manageTabPage.openProcessInstanceLatest(processDefinitionName);
        processViewerPage.waitForPageLoaded();
        verifyEquals(processViewerPage.isProcessInstanceViewerPresent(), previewState,
                "Is the Process Viewer UI editor present? :" + processViewerPage.isProcessInstanceViewerPresent()
                        + ", but expected:" + previewState);
        verifyEquals(processViewerPage.isJsonEditorPresent(), !previewState, "Is the Json editor present? :"
                + processViewerPage.isJsonEditorPresent() + ", but expected:" + !previewState);
    }
}