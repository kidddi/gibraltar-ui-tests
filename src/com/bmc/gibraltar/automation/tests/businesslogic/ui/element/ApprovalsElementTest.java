package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class ApprovalsElementTest extends AppManagerBaseTest {
    private HashMap<String, ElementOfDesigner> processDefNameAndEl = new HashMap<>();

    @DataProvider(name = "elementAndItsRequiredOptions")
    public static Iterator<Object[]> elementAndItsRequiredOptions() {
        RestDataProvider dataProvider = new RestDataProvider();
        List<Object[]> data = new ArrayList<>();
        ElementOfDesigner[] approvalElements = {ElementOfDesigner.APPROVAL_PROCESS, ElementOfDesigner.WAIT_FOR_APPROVAL};
        for (ElementOfDesigner el : approvalElements) {
            List<String> inputParameters = dataProvider.getRequiredInputsOfProcessDefinition("approval:", el.getName());
            List<String> options = inputParameters.stream().map(inputName -> inputName + ":")
                    .collect(Collectors.toList());
            data.add(new Object[]{el, options});
        }
        return data.iterator();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US204932: verifyThatApprovalProcessDefinitionIsPresent")
    @Features("Process Definitions tab")
    @Stories("US204932")
    @GUID("89641790-c571-4b8c-8e95-2c63cf46a712")
    @Bug("")
    public void verifyThatApprovalProcessDefinitionIsPresent() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        assertThat("Approvals process definitions are not present.", processDefinitionsTabPage.getNamesOfSavedProcess(),
                hasItems("Approval Process", "Wait For Approval"));
    }

    @Test(dataProvider = "elementAndItsRequiredOptions", groups = Groups.CATEGORY_FULL, description = "US204932: createProcessDefinitionWithApproval")
    @Features("Process Designer Editor")
    @Stories("US204932")
    @GUID("a0152e21-8e54-445a-9eab-f270c65231f2")
    @Bug("")
    public void createProcessDefinitionWithApproval(ElementOfDesigner approvalElement, List<String> requiredOptions) {
        this.testDescription = "Verifies that a process definition with the approvals element :" + approvalElement.getName() + " can be created.";
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processDefinitionsTabPage.waitForPageLoaded();
        ProcessDefinitionEditorPage editorPage = processDefinitionsTabPage.initiateNewProcess();
        editorPage.waitForPageLoaded();
        editorPage.clearCanvas();
        // generating and setting the process definition name
        ProcessPropertiesTab processPropertiesTab = editorPage.getProcessPropertiesTab();
        String processDefName = approvalElement.getName() + Long.toHexString(System.currentTimeMillis());
        processPropertiesTab.setProcessName(processDefName);
        // building the process workflow
        ActiveElement start = editorPage.dragNDropByCoordinates(ElementOfDesigner.START, "280", "240");
        ActiveElement approvalProcessElement = editorPage.dragNDropByCoordinates(approvalElement, "360", "330");
        ActiveElement end = editorPage.dragNDropByCoordinates(ElementOfDesigner.END, "450", "430");
        editorPage.bindElements(start, approvalProcessElement, end);
        // filling in required fields in the Input Map section
        ElementPropertiesTab elementPropertiesTab = editorPage.getElementPropertiesTab();
        editorPage.clickOnElement(approvalProcessElement);
        for (String option : requiredOptions) {
            elementPropertiesTab.setPropertyValue(approvalProcessElement, InspectorGroup.INPUT_MAP,
                    option, "\"test\"");
        }
        editorPage.saveProcess().closeProcess();
        processDefNameAndEl.put(processDefName, approvalElement);
    }

    @Test(dependsOnMethods = "createProcessDefinitionWithApproval", groups = Groups.CATEGORY_FULL, description = "US204932: changeProcessDefinitionWithApproval")
    @Features("Process Designer Editor")
    @Stories("US204932")
    @GUID("fc569bbc-7e3a-4904-98bb-66e6ab55ec98")
    @Bug("")
    public void changeProcessDefinitionWithApproval() {
        for (Map.Entry<String, ElementOfDesigner> processDefAndEl : processDefNameAndEl.entrySet()) {
            ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd)
                    .navigateToPage();
            ProcessDefinitionEditorPage editorPage = (ProcessDefinitionEditorPage) processDefinitionsTabPage
                    .openTheSavedProcess(processDefAndEl.getKey());
            editorPage.waitForPageLoaded();
            ActiveElement approvalEl = editorPage.getDroppedElement(processDefAndEl.getValue());
            approvalEl.setElementPosition(190, 370);
            approvalEl.setProperty(ElementProperties.DESCRIPTION, "help text for the approval pd.");
            editorPage.clickOnFreeSpaceOnCanvas().saveProcess().closeProcess();
        }
    }
}
