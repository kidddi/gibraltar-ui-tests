package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.datadictionary.DictionaryGroup;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.parameter.ProcessParameter;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.ValidationIssuesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CallActivityElementTest extends AppManagerBaseTest {
    private String processNameWithInputs;
    private String processDefName;
    private List<String> inputsOfCalledProcess = new ArrayList<>();
    private ProcessParameter inputProcParam;
    private ProcessParameter inputProcParam2;
    private String[] optionForSelectionFieldForInput = {"a", "b", "c"};
    private ProcessDefinitionEditorPage editView;
    private ElementPropertiesTab elTab;
    private ActiveElement callActivity;
    private ValidationIssuesTab validations;

    @Test(groups = Groups.CATEGORY_FULL, description = "US199764: verifyCallActivityElementBehavior")
    @Features("Inspector")
    @Stories("US199764")
    @GUID("cffd3da4-594f-4e19-8077-472d44766162")
    @Bug("")
    public void verifyCallActivityElementBehavior() {
        editView = new ProcessDefinitionEditorPage(wd).navigateToPage();
        ProcessParameter inputProcParam = new ProcessParameter(
                "call Process", DataType.TEXT, false, "helpText", "default");
        ProcessPropertiesTab processPropertiesTab = editView.getProcessPropertiesTab();
        processPropertiesTab.addProcessParameter(InspectorGroup.INPUT_PARAMETERS, inputProcParam);
        callActivity = editView.dragAndDropToCanvas(ElementOfDesigner.CALL_ACTIVITY);
        elTab = editView.getElementPropertiesTab();
        editView.doubleClick(callActivity.getXPath());
        elTab.verifyPropertyValue(InspectorGroup.PROPERTIES, "Called Process:", "");
        editView.verifyInfoMessagesAppear("Please select a Called Process before expanding this Call Activity.");
        elTab.verifyPropertyMarkedAsRequired(callActivity, InspectorGroup.PROPERTIES, true, "Called Process:");
        callActivity.openDataDictionaryFor(ElementProperties.CALLED_PROCESS)
                .doubleClickInTreeOnVar(DictionaryGroup.PROCESS_VARIABLES, new String[]{inputProcParam.getName()})
                .apply();
        editView.doubleClick(callActivity.getXPath());
        editView.verifyInfoMessagesAppear("Dynamic Call Activity cannot be expanded.");
        // element's properties verification are covered in 'checkPropertyGroupsPresenceForElement' test;
        editView.closeProcess();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US199764: validateCallActivityElementProperties")
    @Features("Inspector")
    @Stories("US199764")
    @GUID("7642edd6-ad5c-4563-9760-768651eb0c28")
    @Bug("")
    public void validateCallActivityElementProperties() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = processDefinitionsTabPage.initiateNewProcess();
        callActivity = editView.dragAndDropToCanvas(ElementOfDesigner.CALL_ACTIVITY);
        validations = Optional.ofNullable(validations).orElse(new ValidationIssuesTab(wd, editView));
        String expectedValidator = "Called Process cannot be blank.";
        String expectedMessage = "The Process Definition is not valid";
        callActivity.openDataDictionaryFor(ElementProperties.CALLED_PROCESS)
                .setExpression("")
                .apply();
        editView.confirmModalDialog(true);
        editView.clickSaveButton();
        validations.verifyIssueExists(expectedValidator);
        editView.verifyErrorMessagesAppear(expectedMessage);
        editView.closeProcess();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US205672: createProcessDefinitionWithInputs")
    @Features("Inspector")
    @Stories("US205672")
    @GUID("cc19fcf8-f61a-48f0-bc00-d5e8069cc4d3")
    @Bug("")
    public void createProcessDefinitionWithInputs() {
        ProcessDefinitionsTabPage definitionsTabPage = new ProcessDefinitionsTabPage(wd);
        definitionsTabPage.navigateToPage();
        editView = definitionsTabPage.initiateNewProcess();
        ProcessPropertiesTab processPropertiesTab = editView.getProcessPropertiesTab();
        processNameWithInputs = "Inputs" + Long.toHexString(System.currentTimeMillis());
        processPropertiesTab.setProcessName(processNameWithInputs);
        ActiveElement startElement = editView.getDroppedElement(ElementOfDesigner.START);
        ActiveElement endElement = editView.getDroppedElement(ElementOfDesigner.END);
        editView.bindElements(startElement, endElement);
        inputProcParam = new ProcessParameter("SelectionParameter", DataType.SELECTION, optionForSelectionFieldForInput,
                false, "help Text", optionForSelectionFieldForInput[0]);
        inputProcParam2 = new ProcessParameter(
                "StringParameter", DataType.TEXT, true, "helpTextForParameter", "defaultValue");
        processPropertiesTab.addProcessParameter(InspectorGroup.INPUT_PARAMETERS, inputProcParam);
        processPropertiesTab.addProcessParameter(InspectorGroup.INPUT_PARAMETERS, inputProcParam2);
        inputsOfCalledProcess.add(inputProcParam.getName() + ":");
        inputsOfCalledProcess.add(inputProcParam2.getName() + ":");
        editView.clickOnFreeSpaceOnCanvas().saveProcess();
        editView.closeProcess();
    }

    @Test(dependsOnMethods = "createProcessDefinitionWithInputs", groups = Groups.CATEGORY_FULL, description = "US199764: verifyCallActivityElementCallProcess")
    @Features("Inspector")
    @Stories("US199764")
    @GUID("1ddacf63-00f8-4e75-a090-21dd6611aef8")
    @Bug("")
    public void verifyCallActivityElementCallProcess() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd)
                .navigateToPage();
        editView = processDefinitionsTabPage.initiateNewProcess();
        editView.waitForPageLoaded();
        ProcessPropertiesTab processPropertiesTab = editView.getProcessPropertiesTab();
        processDefName = "Call" + processNameWithInputs;
        processPropertiesTab.setProcessName(processDefName);
        ActiveElement startElement = editView.getDroppedElement(ElementOfDesigner.START);
        ActiveElement endElement = editView.getDroppedElement(ElementOfDesigner.END);
        callActivity = editView.dragAndDropToCanvas(ElementOfDesigner.CALL_ACTIVITY);
        elTab = editView.getElementPropertiesTab();
        elTab.setPropertyValue(callActivity, InspectorGroup.PROPERTIES, "Called Process:", processNameWithInputs);
        elTab.verifyPropertiesPresence(InspectorGroup.INPUT_MAP,
                inputsOfCalledProcess.toArray(new String[inputsOfCalledProcess.size()]));
        List<String> optionsInInspector = elTab.getAvailableOptionsFromDropdown(InspectorGroup.INPUT_MAP,
                inputProcParam.getName() + ":");
        verifyTrue(optionsInInspector.containsAll(Arrays.asList(optionForSelectionFieldForInput)));
        callActivity.openDataDictionaryFor(inputProcParam.getName() + ":")
                .verifyOptionsPresentInOptionsGroup(Arrays.asList(optionForSelectionFieldForInput))
                .close();
        editView.bindElements(startElement, callActivity, endElement);
        editView.saveProcess();
        editView.closeProcess();
    }

    @Test(dependsOnMethods = "verifyCallActivityElementCallProcess", enabled = false, groups = Groups.CATEGORY_FULL, description = "US204059: deleteCalledProcess")
    @Features("Inspector")
    @Stories("US204059")
    @GUID("dc5fea0f-b73e-4720-a7c7-243352cfa65d")
    @Bug("")
    public void deleteCalledProcess() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd);
        processDefinitionsTabPage.navigateToPage();
        processDefinitionsTabPage.deleteProcessDefinition(true, processNameWithInputs);
    }

    @Test(dependsOnMethods = "deleteCalledProcess", enabled = false, groups = Groups.CATEGORY_FULL, description = "US204059: verifyBehaviorAfterDeletingCalledProcess")
    @Features("Inspector")
    @Stories("US204059")
    @GUID("ffea2cce-a217-4fd1-9b87-bace1f03ac56")
    @Bug("")
    public void verifyBehaviorAfterDeletingCalledProcess() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd);
        processDefinitionsTabPage.navigateToPage();
        editView = (ProcessDefinitionEditorPage) processDefinitionsTabPage.openTheSavedProcess(processDefName);
        validations = Optional.ofNullable(validations).orElse(new ValidationIssuesTab(wd, editView));
        String expectedValidator = "Called Process cannot be blank.";
        editView.clickSaveButton();
        validations.verifyIssueExists(expectedValidator);
    }
}