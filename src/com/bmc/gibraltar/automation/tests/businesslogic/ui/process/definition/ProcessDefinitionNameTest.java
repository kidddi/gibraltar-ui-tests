package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.definition;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionJsonEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProcessDefinitionNameTest extends AppManagerBaseTest {
    private static List<Object[]> specialSymbols = new ArrayList<>();
    private static List<String> decodedProcessDefinitions = new ArrayList<>();
    private RestDataProvider dataProvider = new RestDataProvider();
    private ProcessDefinitionEditorPage processDefinitionEditorPage;
    private ProcessDefinitionsTabPage processDefinitionsTabPage;

    @DataProvider(name = "specialSymbolsInProcessDefinitionNames")
    public static Iterator<Object[]> specialSymbolsInProcessDefinitionNames() {
        return specialSymbols.iterator();
    }

    @BeforeClass
    protected void createDefinitions() {
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
            String decodedProcessDefinitionName = "Process" + symbols[0] + RandomStringUtils.randomAlphanumeric(5);
            commonSteps.createProcessDefinition(decodedProcessDefinitionName);
            decodedProcessDefinitions.add(decodedProcessDefinitionName);
        }
    }


    @Test(dataProvider = "specialSymbolsInProcessDefinitionNames", groups = Groups.CATEGORY_SANITY, description = "createProcessDefinitionsWithSpecialSymbols")
    @Features("Inspector")
    @Stories("US201388")
    @GUID("a6dd0ba1-001a-435f-9eff-cef8aa1a04de")
    public void createProcessDefinitionsWithSpecialSymbols(String decodedSymbol
            , String encodedSymbol) {
        testDescription = "Verify createProcessDefinitionsWithSpecialSymbols: " + " decodedSymbol:" + decodedSymbol
                + " encodedSymbol:" + encodedSymbol;
        String uniqueIdForProcess = Long.toHexString(System.currentTimeMillis());
        String decodedProcessDefinitionName = "Process" + decodedSymbol + uniqueIdForProcess;
        String encodedProcessDefinitionName = "Process" + encodedSymbol + uniqueIdForProcess;
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        ProcessDefinitionEditorPage processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        processDefinitionEditorPage.setProcessName(decodedProcessDefinitionName);
        ActiveElement startElement = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.START);
        ActiveElement endElement = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.END);
        processDefinitionEditorPage.bindElements(startElement, endElement);
        processDefinitionEditorPage.saveProcess();
        String expectedUrl = processDefinitionEditorPage.getPageUrl().replace("new", "edit/") + "task-manager:" + encodedProcessDefinitionName;
        WebUtils.waitForUrlPresent(wd, expectedUrl);
        processDefinitionEditorPage.closeProcess();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "processDefinitionNameInJsonEditor")
    @Features("Inspector")
    @Stories("US201388")
    @GUID("95eda437-9783-4da5-bf38-217889584240")
    public void processDefinitionNameInJsonEditor() {
        ProcessDefinitionsTabPage tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        for (String decodedProcessDefName : decodedProcessDefinitions) {
            processDefinitionEditorPage = (ProcessDefinitionEditorPage) tabPage.openTheSavedProcess(decodedProcessDefName);
            processDefinitionEditorPage.clickBtnToggle();
            ProcessDefinitionJsonEditorPage jsonEditor = new ProcessDefinitionJsonEditorPage(wd);
            jsonEditor.waitForPageLoaded();
            String processDefinitionJson = jsonEditor.getJsonText();
            String processDefinitionNameInJsonEditor = JsonPath.parse(processDefinitionJson).read("$.name");
            verifyTrue(processDefinitionNameInJsonEditor.equals("task-manager:" + decodedProcessDefName));
            processDefinitionEditorPage.closeProcess();
        }
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyProcessDefinitionNamesAreDecoded")
    @GUID("b925c790-d494-416b-99ed-048bdc00f727")
    public void verifyProcessDefinitionNamesAreDecoded() {
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        List<String> existingProcessDefinitions = processDefinitionsTabPage.getNamesOfSavedProcess();
        decodedProcessDefinitions.stream().forEach(pd -> assertTrue(existingProcessDefinitions.contains(pd)));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyProcessDefinitionNamesForCalledProcessProperty")
    @GUID("24594d9d-f232-404e-930b-67f4d7db3a56")
    public void verifyProcessDefinitionNamesForCalledProcessProperty() {
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        processDefinitionEditorPage.waitForPageLoaded();
        ActiveElement callActivity = processDefinitionEditorPage.dragAndDropToCanvas(ElementOfDesigner.CALL_ACTIVITY);
        processDefinitionEditorPage.clickOnElement(callActivity);
        ElementPropertiesTab elTab = processDefinitionEditorPage.getElementPropertiesTab();
        List<String> availableProcessesToCall = elTab
                .getAvailableOptionsFromDropdown(InspectorGroup.PROPERTIES, "Called Process:");
        verifyTrue(availableProcessesToCall.containsAll(decodedProcessDefinitions));
        processDefinitionEditorPage.closeProcess();
    }
}