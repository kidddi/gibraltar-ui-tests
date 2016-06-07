package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.datadictionary.ConditionsEditor;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.element.Link;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ValidationIssuesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.CREATE_TASK;
import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.USER_TASK;

public class UniqueLabelTest extends AppManagerBaseTest {
    private ProcessDefinitionsTabPage definitionsTabPage;
    private ProcessDefinitionEditorPage editorPage;
    private ValidationIssuesTab validationIssuesTab;

    @Test(groups = Groups.CATEGORY_FULL, description = "US203478: uniqueLabelsForElements")
    @Features("Inspector")
    @Stories("US203478")
    @GUID("adf80e9e-d3d9-4138-88cd-10ee75bdb5af")
    @Bug("")
    public void uniqueLabelsForElements() {
        definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editorPage = definitionsTabPage.initiateNewProcess();
        ActiveElement sendMessage = editorPage.dragAndDropToCanvas(ElementOfDesigner.SEND_MESSAGE);
        sendMessage.setProperty(ElementProperties.X, "220");
        ActiveElement userTask = editorPage.dragAndDropToCanvas(ElementOfDesigner.USER_TASK);
        userTask.setElementPosition(480, 375);
        validationIssuesTab = new ValidationIssuesTab(wd, editorPage).clickToValidationTab();
        validationIssuesTab.refreshIssuesList();
        validationIssuesTab.verifyIssueNotExists(sendMessage.getType(), "Label must be unique.");
        validationIssuesTab.verifyIssueNotExists(userTask.getType(), "Label must be unique.");
        ElementPropertiesTab elTab = editorPage.getElementPropertiesTab();
        elTab.setPropertyValue(userTask, InspectorGroup.PROPERTIES, "Label:", "Send Message");
        validationIssuesTab.verifyIssueExists(sendMessage.getType(), "Label must be unique.");
        definitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US203478: groupHeaderInValidationsIssuesTab")
    @Features("Inspector")
    @Stories("US203478")
    @GUID("3a5e09fb-7e67-4472-ad5a-eaa22f2a0eef")
    @Bug("")
    public void groupHeaderInValidationsIssuesTab() {
        definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editorPage = definitionsTabPage.initiateNewProcess();
        ActiveElement userTask = editorPage.dragAndDropToCanvas(ElementOfDesigner.USER_TASK);
        ActiveElement startEvent = editorPage.getDroppedElement(ElementOfDesigner.START);
        ElementPropertiesTab elTab = editorPage.getElementPropertiesTab();
        elTab.setPropertyValue(userTask, InspectorGroup.PROPERTIES, "Label:", "Some new label");
        editorPage.clickOnElement(startEvent);
        elTab.verifyPropertyValue(InspectorGroup.PROPERTIES, "Label:", "");
        elTab.setPropertyValue(startEvent, InspectorGroup.PROPERTIES, "Label:", "Label of the Start event");
        validationIssuesTab = new ValidationIssuesTab(wd, editorPage).clickToValidationTab();
        validationIssuesTab.refreshIssuesList();
        validationIssuesTab.verifyIssueGroupPresent("Some new label");
        validationIssuesTab.verifyIssueGroupPresent("Label of the Start event");
        definitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US203478: sameLabelForElementAndSequenceFlows")
    @Features("Inspector")
    @Stories("US203478")
    @GUID("e8720668-1182-4a83-94ab-5177ff1d367c")
    @Bug("")
    public void sameLabelForElementAndSequenceFlows() {
        definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editorPage = definitionsTabPage.initiateNewProcess();
        editorPage.clearCanvas();
        ActiveElement start = editorPage.dragNDropByCoordinates(ElementOfDesigner.START, "270", "375");
        ActiveElement end = editorPage.dragNDropByCoordinates(ElementOfDesigner.END, "580", "375");
        ActiveElement userTask = editorPage.dragNDropByCoordinates(ElementOfDesigner.USER_TASK, "290", "270");
        Link linkBetweenUserTaskAndEnd = editorPage.bindElements(userTask, end);
        Link linkBetweenStartAndUserTask = editorPage.bindElements(start, userTask);
        linkBetweenUserTaskAndEnd.setLabel("User Task");
        linkBetweenStartAndUserTask.setLabel("User Task");
        validationIssuesTab = new ValidationIssuesTab(wd, editorPage).clickToValidationTab();
        validationIssuesTab.refreshIssuesList();
        validationIssuesTab.verifyIssueNotExists("Label must be unique.");
        definitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US203478: groupHeadersInValidationsIssuesTabIfLabelIsNotPresentForEventsAndGateways")
    @Features("Inspector")
    @Stories("US203478")
    @GUID("5b92fe49-f365-45a9-96d7-97d6a916ea32")
    @Bug("")
    public void groupHeadersInValidationsIssuesTabIfLabelIsNotPresentForEventsAndGateways() {
        definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editorPage = definitionsTabPage.initiateNewProcess();
        ElementOfDesigner[] elements = {ElementOfDesigner.START, ElementOfDesigner.END,
                ElementOfDesigner.PARALLEL_GATEWAY, ElementOfDesigner.EXCLUSIVE_GATEWAY};
        editorPage.clearCanvas();
        List<ActiveElement> activeElements = Arrays.asList(elements).stream()
                .map(el -> editorPage.dragAndDropToCanvas(el)).collect(Collectors.toList());
        validationIssuesTab = new ValidationIssuesTab(wd, editorPage).clickToValidationTab();
        validationIssuesTab.refreshIssuesList();
        activeElements.stream().forEach(activeElement -> validationIssuesTab.verifyIssueGroupPresent(activeElement));
        definitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US203478: groupHeadersInDataDictionary")
    @Features("Inspector")
    @Stories("US203478")
    @GUID("a57fb00c-5e61-4f00-b78a-d3225260426c")
    @Bug("")
    public void groupHeadersInDataDictionary() {
        definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editorPage = definitionsTabPage.initiateNewProcess();
        ActiveElement createTask = editorPage.dragNDropByCoordinates(CREATE_TASK, "170", "300");
        ActiveElement createTask2 = editorPage.dragNDropByCoordinates(CREATE_TASK, "270", "220");
        ActiveElement userTask = editorPage.dragNDropByCoordinates(USER_TASK, "380", "150");
        ElementPropertiesTab elTab = editorPage.getElementPropertiesTab();
        elTab.setPropertyValue(createTask2, InspectorGroup.PROPERTIES, "Label:", "New label for the element");
        elTab.setPropertyValue(userTask, InspectorGroup.PROPERTIES, "Label:", "1");
        userTask.setProperty(ElementProperties.RECORD_DEFINITION, "Task");
        ConditionsEditor conditionsEditor = userTask.openConditionsEditorFor(ElementProperties.COMPLETION_CRITERIA);
        conditionsEditor.verifySubGroupsPresentInActivitiesGroup(createTask.getName(), "New label for the element", "1");
        conditionsEditor.close();
        definitionsTabPage.navigateToPage();
    }
}