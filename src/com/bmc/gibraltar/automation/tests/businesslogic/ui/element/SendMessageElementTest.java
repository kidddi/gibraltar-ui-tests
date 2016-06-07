package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.processdesigner.ToolbarItem;
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

import java.util.Optional;

public class SendMessageElementTest extends AppManagerBaseTest {
    private String processName = "SendMessage" + Long.toHexString(System.currentTimeMillis());
    private ValidationIssuesTab validationErrorsTab;
    private ProcessDefinitionEditorPage procDefEditPage;
    private ElementPropertiesTab elementTab;
    private ActiveElement sendMessageElement;
    private ActiveElement sendEmailMessageElement;
    private String messageTo = "$\\USER$";
    private String messageSubject = "\"You have been assigned the HR Onboarding task unknown task id\"";
    private String messageBody = "\"You have been assigned the IT Access task.\"";

    @Test(groups = Groups.CATEGORY_FULL, description = "US199760: specifyInputParametersForSendMessage")
    @Features("Inspector")
    @Stories("US199760")
    @GUID("7f53957b-0560-4068-8318-c0d18ff84e78")
    @Bug("")
    public void specifyInputParametersForSendMessage() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = processDefinitionsTabPage.initiateNewProcess();
        sendMessageElement = procDefEditPage.dragAndDropToCanvas(ElementOfDesigner.SEND_MESSAGE);
        procDefEditPage.clickOnElement(sendMessageElement);
        elementTab = procDefEditPage.getElementPropertiesTab();
        elementTab.verifyPropertyMarkedAsRequired(
                sendMessageElement, InspectorGroup.INPUT_MAP, true, "Subject:", "Body:", "Recipients:");
        sendMessageElement.setProperty(ElementProperties.BODY, messageBody);
        sendMessageElement.setProperty(ElementProperties.SUBJECT, messageSubject);
        sendMessageElement.setProperty(ElementProperties.RECIPIENTS, messageTo);
    }

    @Test(dependsOnMethods = "specifyInputParametersForSendMessage", groups = Groups.CATEGORY_FULL, description = "US198336: saveProcessDefinitionWithSendMessage")
    @Features("Inspector")
    @Stories("US198336")
    @GUID("eef41c0c-e26e-4bc3-9f6f-0062a992a869")
    @Bug("")
    public void saveProcessDefinitionWithSendMessage() {
        procDefEditPage = Optional.ofNullable(procDefEditPage).orElse(new ProcessDefinitionEditorPage(wd));
        ActiveElement startElement = procDefEditPage.getDroppedElement(ElementOfDesigner.START);
        ActiveElement endElement = procDefEditPage.getDroppedElement(ElementOfDesigner.END);
        ActiveElement sendMessageElementOnCanvas = procDefEditPage.getDroppedElement(ElementOfDesigner.SEND_MESSAGE);
        sendMessageElementOnCanvas.setProperty(ElementProperties.X, "150");
        sendMessageElementOnCanvas.setProperty(ElementProperties.Y, "370");
        endElement.setProperty(ElementProperties.X, "270");
        endElement.setProperty(ElementProperties.Y, "375");
        procDefEditPage.bindElements(startElement, sendMessageElementOnCanvas, endElement);
        validationErrorsTab = new ValidationIssuesTab(wd, procDefEditPage);
        validationErrorsTab.verifyValidationTabHasNoErrors();
        ProcessPropertiesTab processPropertiesTab = procDefEditPage.getProcessPropertiesTab();
        processPropertiesTab.switchToTab();
        processPropertiesTab.setProcessName(processName);
        procDefEditPage.saveProcess();
        procDefEditPage.verifyButtonOnCanvasToolbarIsDisabled(ToolbarItem.REDO);
        procDefEditPage.verifyButtonOnCanvasToolbarIsDisabled(ToolbarItem.UNDO);
        procDefEditPage.closeProcess();
    }

    @Test(dependsOnMethods = {
            "saveProcessDefinitionWithSendMessage"}, alwaysRun = true, groups = Groups.CATEGORY_FULL, description = "US199760: verifyPresenceOfTheInputParametersForSendMessage")
    @Features("Inspector")
    @Stories("US199760")
    @GUID("9b19e2cc-8a7b-48d4-8d2b-b4d2d8c930ae")
    @Bug("")
    public void verifyPresenceOfTheInputParametersForSendMessage() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = (ProcessDefinitionEditorPage) processDefinitionsTabPage.openTheSavedProcess(processName);
        sendEmailMessageElement = procDefEditPage.getDroppedElement(ElementOfDesigner.SEND_MESSAGE);
        procDefEditPage.clickOnElement(sendEmailMessageElement);
        elementTab = procDefEditPage.getElementPropertiesTab();
        elementTab.verifyPropertyValue(InspectorGroup.INPUT_MAP, "Subject:", messageSubject);
        elementTab.verifyPropertyValue(InspectorGroup.INPUT_MAP, "Body:", messageBody);
        elementTab.verifyPropertyValue(InspectorGroup.INPUT_MAP, "Recipients:", messageTo);
    }

    @Test(dependsOnMethods = {
            "verifyPresenceOfTheInputParametersForSendMessage"}, alwaysRun = true, groups = Groups.CATEGORY_FULL, description = "US199760: specifyInvalidInputParametersForSendMessage")
    @Features("Inspector")
    @Stories("US199760")
    @GUID("e9c6d916-a231-4ff4-a4d7-d08a76969b69")
    @Bug("")
    public void specifyInvalidInputParametersForSendMessage() {
        procDefEditPage = Optional.ofNullable(procDefEditPage).orElse(new ProcessDefinitionEditorPage(wd));
        sendEmailMessageElement = procDefEditPage.getDroppedElement(ElementOfDesigner.SEND_MESSAGE);
        elementTab = Optional.ofNullable(elementTab).orElse(procDefEditPage.getElementPropertiesTab());
        elementTab.expandPanelGroup(InspectorGroup.INPUT_MAP);
        sendMessageElement.setProperty(ElementProperties.SUBJECT, "");
        sendMessageElement.setProperty(ElementProperties.RECIPIENTS, "");
        validationErrorsTab = Optional.ofNullable(validationErrorsTab)
                .orElse(new ValidationIssuesTab(wd, procDefEditPage));
        validationErrorsTab.verifyValidationTabHasWarnings();
        validationErrorsTab.verifyIssueExists(sendMessageElement, "Property \"Subject\" cannot be blank.");
        validationErrorsTab.verifyIssueExists(sendMessageElement, "Property \"Recipients\" cannot be blank.");
    }

    @Test(dependsOnMethods = {"specifyInvalidInputParametersForSendMessage"}, alwaysRun = true, groups = Groups.CATEGORY_FULL, description = "specifyValidInputParametersForSendMessage")
    @GUID("1b88ce0e-ec45-4bd8-9858-d14acffb379e")
    @Bug("")
    public void specifyValidInputParametersForSendMessage() {
        procDefEditPage = Optional.ofNullable(procDefEditPage).orElse(new ProcessDefinitionEditorPage(wd));
        sendEmailMessageElement = procDefEditPage.getDroppedElement(ElementOfDesigner.SEND_MESSAGE);
        elementTab = procDefEditPage.getElementPropertiesTab();
        sendMessageElement.setProperty(ElementProperties.SUBJECT, messageSubject);
        sendMessageElement.setProperty(ElementProperties.RECIPIENTS, messageTo);
        procDefEditPage.clickOnFreeSpaceOnCanvas();
        validationErrorsTab = Optional.ofNullable(validationErrorsTab)
                .orElse(new ValidationIssuesTab(wd, procDefEditPage));
        validationErrorsTab.verifyValidationTabHasNoErrors();
        procDefEditPage.saveProcess();
    }
}
