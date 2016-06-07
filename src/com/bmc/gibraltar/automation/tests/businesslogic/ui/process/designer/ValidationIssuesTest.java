package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.element.Link;
import com.bmc.gibraltar.automation.items.record.RecordField;
import com.bmc.gibraltar.automation.items.tab.ValidationIssuesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;
import java.util.Random;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.*;
import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;

public class ValidationIssuesTest extends AppManagerBaseTest {
    private ProcessDefinitionEditorPage editView;
    private ProcessDefinitionsTabPage configurePage;
    private ValidationIssuesTab validTab;

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyValidationsForExclusiveGateway")
    @Features("Inspector")
    @Stories("US200961")
    @GUID("552ddff5-b0b3-4640-ad67-8583c0f61d91")
    public void verifyValidationsForExclusiveGateway() {
        String processName = "ExclusiveGatewayValidation" + new Random().nextInt(1000);
        getInstance();
        configurePage.navigateToPage();
        configurePage.initiateNewProcess();
        editView.setProcessName(processName);
        editView.clearCanvas();
        ActiveElement end = editView.dragAndDropFromCentre(END, 350, 0);
        ActiveElement exclusiveGateway = editView.dragAndDropFromCentre(EXCLUSIVE_GATEWAY, 50, 50);
        validTab.clickToValidationTab()
                .verifyIssueGroupPresent(exclusiveGateway)
                .verifyIssueExists("The number of connections is invalid.")
                .verifyСlickCorrectButton(exclusiveGateway);
        Link exGWEndCondition = editView.bindElements(exclusiveGateway, end);
        validTab.clickToValidationTab()
                .verifyIssueGroupPresent(exclusiveGateway)
                .verifyIssueExists("Exclusive Gateway must have one outgoing Sequence Flow with no Condition specified.")
                .verifyСlickCorrectButton(exclusiveGateway);
        exGWEndCondition.typeCondition("${userTask.Status} = \"Assigned\"");
        editView.clickOnFreeSpaceOnCanvas()
                .saveProcess()
                .closeProcess();
        configurePage.openTheSavedProcess(processName);
        Link exGWEndCondition1 = editView.bindElements(exclusiveGateway, end);
        Link exGWEndCondition2 = editView.bindElements(exclusiveGateway, end);
        validTab.clickToValidationTab()
                .verifyIssueGroupPresent(exclusiveGateway)
                .verifyIssueExists("The number of connections is invalid.")
                .verifyСlickCorrectButton(exclusiveGateway);
        exGWEndCondition1.remove();
        exGWEndCondition2.remove();
        editView.clickOnFreeSpaceOnCanvas()
                .saveProcess()
                .closeProcess();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "improvingProcessValidationTest")
    @Features("Canvas")
    @Stories("US201707")
    @GUID("9961f2bd-80de-4b77-86df-e5dccb4fa640")
    public void improvingProcessValidationTest() {
        String processName = "ImprovingProcessValidation" + new Random().nextInt(1000);
        getInstance();
        configurePage.navigateToPage();
        configurePage.initiateNewProcess();
        editView.setProcessName(processName);
        editView.clearCanvas();
        ActiveElement start = editView.dragAndDropFromCentre(START, -350, 0);
        ActiveElement end = editView.dragAndDropFromCentre(END, 350, 0);
        ActiveElement createBaseCampProject = editView.dragAndDropFromCentre(CREATE_BASECAMP_PROJECT, 0, 0);
        validTab.verifyIssueExists("One outbound and at least one inbound sequence flow is required.")
                .verifyСlickCorrectButton(createBaseCampProject)
                .verifyIssueExists("At least one inbound sequence flow is required.")
                .verifyСlickCorrectButton(end)
                .verifyIssueExists("A single outbound sequence flow is required.")
                .verifyСlickCorrectButton(start);
        editView.bindElementsFastWay(createBaseCampProject, start);
        editView.verifyErrorMessagesAppear("Element \"Start\" cannot have inbound sequence flows.");
        editView.bindElementsFastWay(start, createBaseCampProject);
        editView.bindElementsFastWay(start, createBaseCampProject);
        editView.verifyErrorMessagesAppear("Element \"Start\" can have only one outbound sequence flow.");
        editView.bindElementsFastWay(createBaseCampProject, end);
        validTab.verifyValidationTabHasNoErrors();
        createBaseCampProject.setProperty(LABEL, "createBscmpPrjct");
        editView.bindElementsFastWay(createBaseCampProject, end);
        // it is a Defect!!! SW00490010
        editView.verifyErrorMessagesAppear("The sequence flow must have a source and a target.");
        createBaseCampProject.setProperty(LABEL, "createBscmpPrjct");
        editView.clickOnFreeSpaceOnCanvas();
        editView.bindElementsFastWay(createBaseCampProject, end);
        editView.verifyErrorMessagesAppear("Element \"Create Basecamp Project\" can have only one outbound sequence flow.");
        editView.bindElementsFastWay(createBaseCampProject, createBaseCampProject);
        editView.verifyErrorMessagesAppear("The sequence flow must connect two different elements.");
        editView.saveProcess()
                .closeProcess();
        configurePage.openTheSavedProcess(processName);
        validTab.verifyValidationTabHasNoErrors();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "userTaskIfRecordInstanceIsDefinedNoAnyRequiredFields")
    @Features("Inspector")
    @Stories("US201707")
    @GUID("441e4a9d-feb7-4ed4-877c-5baeb714095a")
    public void userTaskIfRecordInstanceIsDefinedNoAnyRequiredFields() {
        String processName = "UserTaskIfRecordInstanceIsDefined" + new Random().nextInt(1000);
        getInstance();
        configurePage.navigateToPage();
        configurePage.initiateNewProcess();
        editView.setProcessName(processName);
        ActiveElement start = editView.getDroppedElement(START);
        ActiveElement end = editView.getDroppedElement(END);
        ActiveElement userTask = editView.dragAndDropFromCentre(USER_TASK, 0, 0);
        editView.bindElements(start, userTask, end);
        validTab.verifyIssueExists("Record Definition cannot be blank.");
        userTask.setPropertyByDefault(RECORD_DEFINITION);
        userTask.setProperty(RECORD_INSTANCE_ID, "1");
        List<String> listOfRequiredParameters = new RestDataProvider()
                .getListOfFieldsWithoutDefaultValue(RecordField.Option.REQUIRED, RECORD_DEFINITION.getDefault());
        for (String option : listOfRequiredParameters) {
            String errorMessage = String.format("Input Map Property \"%s\" cannot be blank.", option);
            ElementProperties reqProperty = ElementProperties.get(option + ":");
            userTask.ensurePropertyNotRequired(reqProperty);
            validTab.verifyIssueNotExists(errorMessage);
        }
        userTask.setProperty(RECORD_INSTANCE_ID, "");
        for (String option : listOfRequiredParameters) {
            String errorMessage = String.format("Input Map Property \"%s\" cannot be blank.", option);
            validTab.verifyIssueExists(errorMessage);
            ElementProperties reqProperty = ElementProperties.get(option + ":");
            userTask.ensurePropertyRequired(reqProperty);
            userTask.setPropertyByDefault(reqProperty);
            validTab.verifyIssueNotExists(errorMessage);
        }
        validTab.verifyIssueExists("Completion Criteria cannot be blank.");
        userTask.setPropertyByDefault(COMPLETION_CRITERIA);
        validTab.verifyValidationTabHasNoErrors();
        editView.saveProcess().closeProcess();
    }

    private void getInstance() {
        configurePage = new ProcessDefinitionsTabPage(wd);
        editView = new ProcessDefinitionEditorPage(wd);
        validTab = new ValidationIssuesTab(wd, editView);
    }
}