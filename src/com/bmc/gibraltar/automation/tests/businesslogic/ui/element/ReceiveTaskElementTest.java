package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.tab.ValidationIssuesTab;
import com.bmc.gibraltar.automation.pages.ManageTabPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Optional;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.*;
import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;

public class ReceiveTaskElementTest extends AppManagerBaseTest {
    private ProcessDefinitionsTabPage configurePage;
    private ProcessDefinitionEditorPage editView;
    private ValidationIssuesTab validTab;

    @Test(groups = Groups.CATEGORY_FULL, description = "US201999: receiveTaskElementTest")
    @Features("Canvas")
    @Stories("US201999")
    @GUID("301cc04c-ec33-43db-b1d7-fc7de1542ff5")
    @Bug("")
    public void receiveTaskElementTest() {
        String processName = "ReceiveTaskElementTest" + RandomUtils.nextInt(0, 1001);
        getInstance();
        configurePage.navigateToPage();
        configurePage.initiateNewProcess();
        editView.setProcessName(processName);
        editView.clearCanvas();
        ActiveElement start = editView.dragAndDropFromCentre(START, 100, 100);
        ActiveElement end = editView.dragAndDropFromCentre(END, -100, -100);
        ActiveElement receiveTask = editView.dragAndDropFromCentre(RECEIVE_TASK, 0, 0);
        validTab.clickToValidationTab()
                .verifyIssueGroupPresent(receiveTask)
                .verifyIssueExists("One outbound and at least one inbound sequence flow is required.")
                .verifyIssueExists("At least one Signal Parameter is required.")
                .verifyСlickCorrectButton(receiveTask);
        editView.bindElements(start, receiveTask);
        editView.bindElements(receiveTask, end);
        validTab.clickToValidationTab()
                .verifyIssueGroupPresent(receiveTask)
                .verifyIssueNotExists("One incoming and one outgoing connection is required.")
                .verifyIssueExists("At least one Signal Parameter is required.")
                .verifyСlickCorrectButton(receiveTask);
        receiveTask.setProperty(PARAMETER_NAME, "123");
        receiveTask.setProperty(PARAMETER_NAME, "qwe");
        receiveTask.setProperty(PARAMETER_NAME, "Milkbone_UG");
        validTab.clickToValidationTab()
                .verifyValidationTabHasNoErrors();
        editView.saveProcess()
                .closeProcess();
        configurePage.openTheSavedProcess(processName);
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US203097: addingOutputMapForReceiveTaskElementInProcessDesigner")
    @Features("Palette")
    @Stories("US203097")
    @GUID("b12a7025-1915-4cee-a0c1-4bc24a901539")
    @Bug("")
    public void addingOutputMapForReceiveTaskElementInProcessDesigner() {
        String processName = "ReceiveTaskElementAddingOutputMap" + RandomUtils.nextInt(0, 1001);
        configurePage = new ProcessDefinitionsTabPage(wd);
        editView = new ProcessDefinitionEditorPage(wd);
        configurePage.navigateToPage();
        configurePage.initiateNewProcess();
        editView.setProcessName(processName);
        ActiveElement start = editView.getDroppedElement(START);
        ActiveElement end = editView.getDroppedElement(END);
        ActiveElement receiveTask1 = editView.dragAndDropFromCentre(RECEIVE_TASK, 0, 0);
        ActiveElement receiveTask2 = editView.dragAndDropFromCentre(RECEIVE_TASK, 0, 100);
        receiveTask1.setProperties(LABEL, PARAMETER_NAME, OUTPUT_MAP_NAME, OUTPUT_MAP_SOURCE, OUTPUT_MAP_NAME,
                OUTPUT_MAP_SOURCE)
                .byValues(new String[]{"receivTsk1", "First", "FirstInput", "${activityResults.rx-" + receiveTask1.getId()
                        + ".First}", "SecondInput", "${activityResults.rx-" + receiveTask2.getId() + ".Second}"});
        receiveTask2.setProperties(LABEL, PARAMETER_NAME, OUTPUT_MAP_NAME, OUTPUT_MAP_SOURCE, OUTPUT_MAP_NAME,
                OUTPUT_MAP_SOURCE)
                .byValues(new String[]{"receivTsk2", "Second", "FirstInput", "${activityResults.rx-" + receiveTask1.getId()
                        + ".First}", "SecondInput", "${activityResults.rx-" + receiveTask2.getId() + ".Second}"});
        editView.bindElements(start, receiveTask1, receiveTask2, end);
        editView.saveProcess()
                .closeProcess();
        ManageTabPage mPage = new ManageTabPage(wd).navigateToPage();
        mPage.openProcessStart(processName);
        mPage.startProcess(processName);
        mPage.verifyProcessStart(processName);
    }

    private void getInstance() {
        configurePage = Optional.ofNullable(configurePage).orElse(new ProcessDefinitionsTabPage(wd));
        editView = Optional.ofNullable(editView).orElse(new ProcessDefinitionEditorPage(wd));
        validTab = Optional.ofNullable(validTab).orElse(new ValidationIssuesTab(wd, editView));
    }
}