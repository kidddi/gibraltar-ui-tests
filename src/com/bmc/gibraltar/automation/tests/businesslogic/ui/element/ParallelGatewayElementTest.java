package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.tab.Palette;
import com.bmc.gibraltar.automation.items.tab.ValidationIssuesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class ParallelGatewayElementTest extends AppManagerBaseTest {
    public static ActiveElement firstParallelGateway;
    public static ActiveElement secondParallelGateway;
    public ProcessDefinitionEditorPage processDesignerPage;
    public ActiveElement start;
    public ActiveElement end;

    @Test(groups = Groups.CATEGORY_FULL, description = "US199761: validationForParallelGatewayElement")
    @Features("Canvas")
    @Stories("US199761")
    @GUID("84025298-8f0a-477f-91c5-cdd91e9c01b4")
    @Bug("")
    public void validationForParallelGatewayElement() {
        processDesignerPage = new ProcessDefinitionEditorPage(wd);
        processDesignerPage.navigateToPage();
        start = processDesignerPage.getDroppedElement(ElementOfDesigner.START);
        start.setElementPosition(120, 320);
        end = processDesignerPage.getDroppedElement(ElementOfDesigner.END);
        end.setElementPosition(520, 325);
        firstParallelGateway = processDesignerPage.dragNDropByCoordinates(ElementOfDesigner.PARALLEL_GATEWAY, 210, 315);
        processDesignerPage.bindElements(start, firstParallelGateway);
        secondParallelGateway = processDesignerPage.dragNDropByCoordinates(ElementOfDesigner.PARALLEL_GATEWAY, 410, 320);
        for (int i = 1; i < 5; i++) {
            int yCoordinate = 200 + 80 * i;
            ActiveElement sendMessage = processDesignerPage
                    .dragNDropByCoordinates(ElementOfDesigner.SEND_MESSAGE, 305, yCoordinate);
            processDesignerPage.bindElements(firstParallelGateway, sendMessage, secondParallelGateway);
        }
        processDesignerPage.bindElements(secondParallelGateway, end);
        ValidationIssuesTab validations = new ValidationIssuesTab(wd, processDesignerPage);
        validations.clickToValidationTab()
                .verifyIssueExists(ElementOfDesigner.PARALLEL_GATEWAY, "The number of connections is invalid.");
    }

    @Test(dependsOnMethods = "validationForParallelGatewayElement", groups = Groups.CATEGORY_FULL, description = "US199761: verifyParallelGatewayElementCorrectRoles")
    @Features("Canvas")
    @Stories("US199761")
    @GUID("4032b27a-ff9d-494e-8b9c-7e6a0634b9c1")
    @Bug("")
    public void verifyParallelGatewayElementCorrectRoles() {
        ProcessDefinitionEditorPage processDesignerPage = new ProcessDefinitionEditorPage(wd);
        ActiveElement sendMessage = processDesignerPage.getDroppedElement(ElementOfDesigner.SEND_MESSAGE);
        processDesignerPage.deleteElement(sendMessage);
        processDesignerPage.getTab(Palette.DEFAULT_TAB).collapsePanel();
        ValidationIssuesTab validations = new ValidationIssuesTab(wd, processDesignerPage);
        validations.verifyIssueNotExists(ElementOfDesigner.PARALLEL_GATEWAY, "The number of connections is invalid.");
    }
}