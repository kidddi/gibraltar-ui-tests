package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.definition;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.processdesigner.ToolbarItem;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class SaveProcessDefinitionAsDraftTest extends AppManagerBaseTest {
    private ProcessDefinitionsTabPage processDefinitionsTabPage;
    private String modalDialogText = "This process definition is not valid. You may save the process definition in" +
            " an invalid state but it will be disabled. Do you want to continue?";
    private ProcessDefinitionEditorPage editorPage;
    private ProcessPropertiesTab processPropertiesTab;

    @Test(groups = Groups.CATEGORY_FULL, description = "defaultStateOfEnabledSwitch")
    @Features("Process Designer Editor")
    @Stories("US206697")
    @GUID("20397e73-8723-46ad-8a3d-5557533a802d")
    public void defaultStateOfEnabledSwitch() {
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editorPage = processDefinitionsTabPage.initiateNewProcess();
        editorPage.waitForPageLoaded();
        processPropertiesTab = editorPage.getProcessPropertiesTab();
        processPropertiesTab.verifyProcessDefinitionEnabled(true);
        editorPage.closeProcess();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "disableNewProcessDefinition")
    @Features("Process Designer Editor")
    @Stories("US206697")
    @GUID("f4870360-adcc-48e4-b94c-0d044bb77899")
    public void disableNewProcessDefinition() {
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editorPage = processDefinitionsTabPage.initiateNewProcess();
        editorPage.waitForPageLoaded();
        processPropertiesTab = editorPage.getProcessPropertiesTab();
        String processDefinitionName = "Draft" + Long.toHexString(System.currentTimeMillis());
        processPropertiesTab.setProcessName(processDefinitionName);
        editorPage.clearCanvas();
        ActiveElement start = editorPage.dragNDropByCoordinates(ElementOfDesigner.START, "270", "375");
        ActiveElement end = editorPage.dragNDropByCoordinates(ElementOfDesigner.END, "580", "375");
        ActiveElement createTaskElement = editorPage.dragNDropByCoordinates(ElementOfDesigner.CREATE_TASK, "340", "300");
        editorPage.bindElements(start, createTaskElement);
        editorPage.clickSaveButton();
        editorPage.verifyErrorMessagesAppear();
        processPropertiesTab.switchToTab();
        processPropertiesTab.verifyProcessDefinitionEnabled(true);
        editorPage.bindElements(createTaskElement, end);
        confirmDialogAndCheckProcessDefinitionState(false);
        confirmDialogAndCheckProcessDefinitionState(true);
        editorPage.closeProcess();
        assertTrue(processDefinitionsTabPage.getNamesOfSavedProcess().contains(processDefinitionName));
    }

    private void confirmDialogAndCheckProcessDefinitionState(boolean confirmDialog) {
        editorPage.clickSaveButton();
        editorPage.verifyModalDialog(modalDialogText, true);
        editorPage.confirmModalDialog(confirmDialog);
        processPropertiesTab.switchToTab();
        processPropertiesTab.verifyProcessDefinitionEnabled(!confirmDialog);
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "warningWindowWhileSavingDisabledProcessDefinition")
    @Features("Process Designer Editor")
    @Stories("US206697")
    @GUID("ac2c9dba-6301-49b3-946e-a8818b681e12")
    public void warningWindowWhileSavingDisabledProcessDefinition() {
        String processDefName = "Disabled" + RandomStringUtils.randomAlphanumeric(10);
        new CommonSteps(new RestDataProvider()).createDisabledProcessDefinition(processDefName);
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editorPage = (ProcessDefinitionEditorPage) processDefinitionsTabPage
                .openTheSavedProcess(processDefName);
        processPropertiesTab = editorPage.getProcessPropertiesTab();
        processPropertiesTab.verifyProcessDefinitionEnabled(false);
        ActiveElement start = editorPage.getDroppedElement(ElementOfDesigner.START);
        editorPage.deleteElement(start);
        editorPage.clickSaveButton();
        editorPage.verifyErrorMessagesAppear();
        processPropertiesTab.verifyProcessDefinitionEnabled(false);
        editorPage.executeToolbarAction(ToolbarItem.UNDO);
        processPropertiesTab.verifyProcessDefinitionEnabled(false);
        editorPage.clickSaveButton();
        verifyFalse(editorPage.isModalDialogPresent());
        editorPage.closeProcess();
        assertTrue(processDefinitionsTabPage.getNamesOfSavedProcess().contains(processDefName));
    }
}