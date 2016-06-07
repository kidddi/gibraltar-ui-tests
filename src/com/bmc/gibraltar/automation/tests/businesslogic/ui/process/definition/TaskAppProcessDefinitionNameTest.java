package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.definition;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.pages.ManageTabPage;
import com.bmc.gibraltar.automation.pages.StartProcessPage;
import com.bmc.gibraltar.automation.tests.TaskManagerBaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.ArrayList;
import java.util.List;

public class TaskAppProcessDefinitionNameTest extends TaskManagerBaseTest {
    private static List<Object[]> specialSymbols = new ArrayList<>();
    private static List<String> decodedProcessDefinitions = new ArrayList<>();

    @BeforeClass
    protected void createDefinitions() {
        RestDataProvider dataProvider = new RestDataProvider();
        // The process definition with the ":" symbol cannot be opened due to bundle naming changes
        // because the server thinks that "Process" is also the bundle
        // and as result the test tries to open the process definition with the name "1516e13669a"
        // instead of "Process:1516e13669a"
        // TODO: remove commented line when the decision is made on ":" in the definition names
        //   specialSymbols.add(new Object[]{":", "%3A"});
        specialSymbols.add(new Object[]{"%", "%25"});
        specialSymbols.add(new Object[]{"/", "/"});
        specialSymbols.add(new Object[]{"?", "%3F"});
        specialSymbols.add(new Object[]{"#", "%23"});
        specialSymbols.add(new Object[]{" ", "%20"});
        CommonSteps commonSteps = new CommonSteps(dataProvider);
        for (Object[] symbols : specialSymbols) {
            String uniqueIdForProcess = Long.toHexString(System.currentTimeMillis());
            String decodedProcessDefinitionName = "Process" + symbols[0] + uniqueIdForProcess;
            commonSteps.createProcessDefinition(decodedProcessDefinitionName);
            decodedProcessDefinitions.add(decodedProcessDefinitionName);
        }
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyProcessDefinitionNamesOnStartProcessPage")
    @Features("[P534] Process Designer")
    @Stories("US206697")
    @GUID("c45e2057-4648-423e-8827-7acd15c16d29")
    public void verifyProcessDefinitionNamesOnStartProcessPage() {
        ManageTabPage manageTabPage = new ManageTabPage(wd).navigateToPage();
        List<String> pds = manageTabPage.getListOfProcessDefinitions();
        verifyTrue(pds.containsAll(decodedProcessDefinitions));
        manageTabPage.selectProcess(decodedProcessDefinitions.get(0));
        StartProcessPage startProcessPage = manageTabPage.clickStart();
        startProcessPage.waitForPageLoaded();
        List<String> pdOnStartProcessPage = startProcessPage.getNamesOfProcessesInDropDown();
        assertTrue(pdOnStartProcessPage.containsAll(decodedProcessDefinitions));
    }
}