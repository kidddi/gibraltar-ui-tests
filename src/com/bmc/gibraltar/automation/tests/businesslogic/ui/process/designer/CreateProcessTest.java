package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.element.Link;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.parameter.ProcessParameter;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.pages.ManageTabPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.pages.StartProcessPage;
import com.bmc.gibraltar.automation.tests.TaskManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Random;

public class CreateProcessTest extends TaskManagerBaseTest {
    private ProcessDefinitionsTabPage configurePage;
    private ProcessDefinitionEditorPage page;

    @Test(groups = Groups.CATEGORY_FULL, description = "changeExistingProcess")
    @Features("Open existing process, change it, close and run task")
    @Stories("US199214")
    @GUID("77416bba-a575-47e8-b257-fde72a4c4241")
    public void changeExistingProcess() {
        //TODO: divide it into small tests + take into account that it should be divided into different classes
        // (one class extends from TaskManager base test and another - AppManagerBaseTest)
        page = new ProcessDefinitionEditorPage(wd);
        configurePage = new ProcessDefinitionsTabPage(wd);
        ProcessPropertiesTab processPropertiesTab = page.getProcessPropertiesTab();
        // TODO: this test checks just 1 element, and where are all other elements?
        ElementOfDesigner arr[] = {ElementOfDesigner.BUILD_COMPLETION_CONDITION};

        String processName = "";
        configurePage.navigateToPage();
        for (ElementOfDesigner event : arr) {
            try {
                if (!(event == ElementOfDesigner.START || event == ElementOfDesigner.END
                        || event == ElementOfDesigner.ANNOTATION)) {
                    configurePage.initiateNewProcess();
                    if (event == ElementOfDesigner.USER_TASK) {
                        ProcessParameter procParam = new ProcessParameter(
                                "taskId", DataType.TEXT, false, "helpText", "10");
                        processPropertiesTab.addProcessParameter(InspectorGroup.INPUT_PARAMETERS, procParam);
                    }
                    ActiveElement start = page.getDroppedElement(ElementOfDesigner.START);
                    ActiveElement end = page.getDroppedElement(ElementOfDesigner.END);
                    ActiveElement current = page.dragAndDrop(event);
                    if (event == ElementOfDesigner.SUB_PROCESS) {
                        current.generateSubProcessByDefault();
                    }

                    if (event == ElementOfDesigner.CALL_ACTIVITY) {
                        current.setProperty(ElementProperties.CALLED_PROCESS, processName);
                    } else {
                        current.setPropertiesByDefault();
                    }
                    page.bindElements(start, current, end);
                    if (event == ElementOfDesigner.PARALLEL_GATEWAY) {
                        Link gw = page.bindElements(current, end);
                        // TODO somebody need to review below warning
                        if (event == ElementOfDesigner.EXCLUSIVE_GATEWAY)
                            gw.typeConditionByDefault();
                    }
                    processName = "Start Process " + event.getName() + new Random().nextInt(100);
                    page.setProcessName(processName);
                    page.saveProcess().closeProcess();
                }

                ManageTabPage mPage = new ManageTabPage(wd);
                StartProcessPage startProcessPage = mPage.openProcessStart(processName);
                if (event == ElementOfDesigner.USER_TASK) {
                    startProcessPage.fillInputField("taskId", "1");
                }
                mPage.startProcess(processName);
                mPage.verifyProcessStart(processName);
                log.info("PASSED!!!!!!!!!!!!!!!!!!!!!!!  " + event + " PASSED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                // TODO catching all type of exceptions is a bad practice
            } catch (Exception e) {
                log.info("FAILED!!!!!!!!!!!!!!!!!!!!!!!  " + event + " FAILED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "configureRunAsPropertyForProcessActionAndActions")
    @Features("Inspector")
    @Stories("US204269")
    @GUID("0118fb81-5c9e-4a5c-bc16-a885c8b21785")
    public void configureRunAsPropertyForProcessActionAndActions() { //should be improved
        String processName = "Start Process RunAs" + new Random().nextInt(1000);
        configurePage = new ProcessDefinitionsTabPage(wd);
        page = new ProcessDefinitionEditorPage(wd);
        configurePage.navigateToPage();
        configurePage.initiateNewProcess();
        page.setProcessName(processName)
                .isPropertyCorrect(ProcessDefinitionEditorPage.runAs, ProcessDefinitionEditorPage.ADMINISTRATOR)
                .setRunAs(ProcessDefinitionEditorPage.CURRENT_USER)
                .clearCanvas();
        ActiveElement start = page.dragAndDrop(ElementOfDesigner.START);
        ActiveElement end = page.dragAndDrop(ElementOfDesigner.END);
        ActiveElement cbp = page.dragAndDrop(ElementOfDesigner.CREATE_BASECAMP_PROJECT);
        cbp.verifyPropertyHasValue(ElementProperties.RUN_AS, "Inherit from Process")
                .setProperty(ElementProperties.RUN_AS, ProcessDefinitionEditorPage.ADMINISTRATOR);
        page.bindElements(start, cbp, end).
                saveProcess()
                .closeProcess()
                .openTheSavedProcess(processName);
        page.isPropertyCorrect(ProcessDefinitionEditorPage.runAs, ProcessDefinitionEditorPage.CURRENT_USER)
                .setRunAs(ProcessDefinitionEditorPage.ADMINISTRATOR);
        cbp.verifyPropertyHasValue(ElementProperties.RUN_AS, ProcessDefinitionEditorPage.ADMINISTRATOR)
                .setProperty(ElementProperties.RUN_AS, ProcessDefinitionEditorPage.CURRENT_USER);
        page.saveProcess()
                .closeProcess()
                .openTheSavedProcess(processName);
        page.isPropertyCorrect(ProcessDefinitionEditorPage.runAs, ProcessDefinitionEditorPage.ADMINISTRATOR);
        cbp.verifyPropertyHasValue(ElementProperties.RUN_AS, ProcessDefinitionEditorPage.CURRENT_USER);
    }
}
