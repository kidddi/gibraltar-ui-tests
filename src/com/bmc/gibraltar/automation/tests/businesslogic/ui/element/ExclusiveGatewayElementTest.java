package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.element.Link;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ValidationIssuesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class ExclusiveGatewayElementTest extends AppManagerBaseTest {
    private ProcessDefinitionsTabPage processDefinitionsTabPage;
    private ProcessDefinitionEditorPage editView;

    @Test(groups = Groups.CATEGORY_FULL, description = "US199762: exclusiveGatewayElementCorrectRoles")
    @Features("Inspector")
    @Stories("US199762")
    @GUID("96cf5fdd-9d8f-4adc-8968-a9a34a191447")
    @Bug("")
    public void exclusiveGatewayElementCorrectRoles() {
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = processDefinitionsTabPage.initiateNewProcess();
        editView.clearCanvas();
        ActiveElement start = editView.dragNDropByCoordinates(ElementOfDesigner.START, "270", "375");
        ActiveElement endFirst = editView.dragNDropByCoordinates(ElementOfDesigner.END, "580", "375");
        ActiveElement exclusiveGateway = editView
                .dragNDropByCoordinates(ElementOfDesigner.EXCLUSIVE_GATEWAY, "410", "280");
        editView.bindElements(start, exclusiveGateway, endFirst);
        ValidationIssuesTab validations = new ValidationIssuesTab(wd, editView);
        validations.clickToValidationTab().verifyIssueExists("The number of connections is invalid.");
        ActiveElement endSecond = editView.dragAndDropToCanvas(ElementOfDesigner.END);
        endSecond.setElementPosition(420, 450);
        Link link = editView.bindElements(exclusiveGateway, endSecond);
        validations.clickToValidationTab()
                .verifyIssueExists("Exclusive Gateway must have one outgoing Sequence Flow with no Condition specified");
        editView.getElementPropertiesTab().verifyPropertyMarkedAsRequired(link, InspectorGroup.PROPERTIES, false,
                ElementProperties.CONDITION.getName());
        link.typeCondition("${userTask.Status}=\"Assigned\"\n");
        validations.verifyValidationTabHasNoErrors();
        processDefinitionsTabPage.navigateToPage();
    }
}