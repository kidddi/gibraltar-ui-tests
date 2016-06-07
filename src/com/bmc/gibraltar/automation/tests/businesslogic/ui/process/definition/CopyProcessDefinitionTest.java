package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.definition;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.framework.utils.PropertiesUtils;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;

import static com.bmc.gibraltar.automation.items.ActionBar.Action.COPY;
import static com.bmc.gibraltar.automation.utils.JsonUtils.cleanJson;
import static com.bmc.gibraltar.automation.utils.JsonUtils.removeAllIDs;

public class CopyProcessDefinitionTest extends AppManagerBaseTest {
    private ProcessDefinitionsTabPage processDefinitionsTabPage;
    private RestDataProvider dataProvider = new RestDataProvider();
    private String processDefinitionName = "NotificationProcess" + Long.toHexString(System.currentTimeMillis());

    @BeforeClass
    public void createDefinition() {
        new CommonSteps(dataProvider).createProcessDefinitionWithCreateTaskElement(processDefinitionName);
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processDefinitionsTabPage.waitForPageLoaded();
        assertTrue(processDefinitionsTabPage.getNamesOfSavedProcess().contains(processDefinitionName));
    }

    @Test(dataProvider = "gridViews", groups = Groups.CATEGORY_FULL, description = "copyButtonInGridViews")
    @Features("Process Definitions tab")
    @Stories("US206700")
    @GUID("6de1b21e-b4ef-41f1-a6a8-21c1142d1c2e")
    public void copyButtonInGridViews(boolean isButtonPresent) {
        assertEquals(processDefinitionsTabPage.actionBar(COPY).isActionPresent(), isButtonPresent);
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "copyButtonState")
    @Features("Process Definitions tab")
    @Stories("US206700")
    @GUID("8ab8b48b-efa1-42bd-973b-90484cd3014e")
    public void copyButtonState() {
        processDefinitionsTabPage.verifyCopyButtonDisabled();
        List<String> processDefinitions = processDefinitionsTabPage.getNamesOfSavedProcess();
        CommonSteps steps = new CommonSteps(new RestDataProvider());
        while (processDefinitions.size() < 3) {
            steps.createProcessDefinition(Long.toHexString(System.currentTimeMillis()));
            processDefinitionsTabPage.refresh();
            processDefinitions = processDefinitionsTabPage.getNamesOfSavedProcess();
        }
        String processDef1 = processDefinitions.get(0);
        String processDef2 = processDefinitions.get(1);
        processDefinitionsTabPage.selectProcessDefinition(processDef1);
        processDefinitionsTabPage.verifyCopyButtonEnabled();
        processDefinitionsTabPage.selectProcessDefinition(processDef2);
        processDefinitionsTabPage.verifyCopyButtonDisabled();
        processDefinitionsTabPage.deselectProcessDefinition(processDef1);
        processDefinitionsTabPage.verifyCopyButtonEnabled();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "copyProcessDefinition")
    @Features("Process Definitions tab")
    @Stories("US206700")
    @Issue("SW00496834")
    @GUID("4b120142-2103-48c5-89eb-9f3d487ba458")
    @Bug("SW00496834")
    public void copyProcessDefinition() {
        ProcessDefinitionEditorPage editorPage = processDefinitionsTabPage
                .openEditorToCopyProcessDefinition(processDefinitionName);
        String processDefCopyName = "CopyOf" + processDefinitionName;
        verifyTrue(editorPage.getProcessName().equals(String.format("<Copy of %s>", processDefinitionName)));
        verifyEquals(editorPage.getProcessDefName(), "");
        String urlOfCopy = PropertiesUtils.getAppServerUrl() + "/tms/process-designer/copy/task-manager:"
                + processDefinitionName;
        verifyTrue(wd.getCurrentUrl().equals(urlOfCopy));
        editorPage.setProcessName(processDefCopyName);
        editorPage.saveProcess().closeProcess();
        RestDataProvider data = new RestDataProvider();
        String originalPD = data.getJsonOfProcess(processDefinitionName);
        String copyPD = data.getJsonOfProcess(processDefCopyName);
        data.logout();
        log.info("Verifying that the json of the [" + processDefinitionName + "] \n and json of the [" +
                processDefCopyName + "] are the same.");
        JSONAssert.assertEquals(removeAllIDs(cleanJson(originalPD)), removeAllIDs(cleanJson(copyPD)), false);
    }
}