package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.element.Link;
import com.bmc.gibraltar.automation.items.processdesigner.ToolbarItem;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;
import java.util.Optional;

public class UndoRedoWithElementsTest extends AppManagerBaseTest {
    private ProcessDefinitionEditorPage processDefinitionEditorPage;
    private ProcessDefinitionsTabPage processDefinitionsTabPage;

    @Test(groups = Groups.CATEGORY_FULL, description = "changeCalledProcess")
    @Features("Canvas")
    @Stories("US201255")
    @GUID("66fee179-4409-4609-afa3-278767b81851")
    public void changeCalledProcess() {
        String expectedDialogMessage = "Input Map and Output Map parameters will be cleared. Do you want to continue?";
        processDefinitionsTabPage = Optional.ofNullable(processDefinitionsTabPage)
                .orElse(new ProcessDefinitionsTabPage(wd));
        processDefinitionsTabPage.navigateToPage();
        processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        ActiveElement element = processDefinitionEditorPage
                .dragNDropByCoordinates(ElementOfDesigner.CALL_ACTIVITY, "260", "290");
        List<String> processDef = new RestDataProvider().getProcessDefinitionsNamesByState(true);
        element.setProperty(ElementProperties.CALLED_PROCESS, processDef.get(0));
        element.setProperty(ElementProperties.CALLED_PROCESS, processDef.get(1));
        processDefinitionEditorPage.verifyModalDialog(expectedDialogMessage, true);
        processDefinitionEditorPage.confirmModalDialog(true);
        processDefinitionEditorPage.executeToolbarAction(ToolbarItem.UNDO);
        processDefinitionEditorPage.verifyModalDialog(expectedDialogMessage, true);
        processDefinitionEditorPage.confirmModalDialog(true);
        processDefinitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "undoRedoForExpandingAndCollapsingCallActivity")
    @Features("Canvas")
    @Stories("US201255")
    @GUID("c6ac1a25-0a2e-4ce1-96b1-2f6ba5c14b57")
    public void undoRedoForExpandingAndCollapsingCallActivity() {
        processDefinitionsTabPage = Optional.ofNullable(processDefinitionsTabPage)
                .orElse(new ProcessDefinitionsTabPage(wd));
        processDefinitionsTabPage.navigateToPage();
        processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        ActiveElement element = processDefinitionEditorPage.dragAndDropToCanvas(ElementOfDesigner.CALL_ACTIVITY);
        processDefinitionEditorPage.clickOnElement(element);
        int initialX = Integer.parseInt(element.getPropertyValue(ElementProperties.X));
        int initialY = Integer.parseInt(element.getPropertyValue(ElementProperties.Y));
        List<String> processDef = new RestDataProvider().getProcessDefinitionsNamesByState(true);
        element.setProperty(ElementProperties.CALLED_PROCESS, processDef.get(0));
        processDefinitionEditorPage.doubleClick(element.getXPath()); // this will expand the Call Activity
        int expandedX = Integer.parseInt(element.getPropertyValue(ElementProperties.X));
        int expandedY = Integer.parseInt(element.getPropertyValue(ElementProperties.Y));
        processDefinitionEditorPage.doubleClick(element.getXPath()); // this will collapse the Call Activity
        processDefinitionEditorPage.executeToolbarAction(ToolbarItem.UNDO);
        processDefinitionEditorPage.verifyElementPosition(element, expandedX, expandedY);
        processDefinitionEditorPage.executeToolbarAction(ToolbarItem.REDO);
        processDefinitionEditorPage.verifyElementPosition(element, initialX, initialY);
        processDefinitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "undoRedoForElementsCopying")
    @Features("Canvas")
    @Stories("US201255")
    @GUID("326a6e49-3906-458c-8543-c819f1a9b2f0")
    public void undoRedoForElementsCopying() {
        processDefinitionsTabPage = Optional.ofNullable(processDefinitionsTabPage)
                .orElse(new ProcessDefinitionsTabPage(wd));
        processDefinitionsTabPage.navigateToPage();
        processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        ActiveElement endOriginal = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.END);
        ElementPropertiesTab elTab = processDefinitionEditorPage.getElementPropertiesTab();
        elTab.setPropertyValue(endOriginal, InspectorGroup.PROPERTIES, ElementProperties.LABEL.getName(), "This is a label.");
        endOriginal.setProperty(ElementProperties.DESCRIPTION, "helpText");
        ActiveElement endCopy = processDefinitionEditorPage.copyElement(endOriginal);
        processDefinitionEditorPage.executeToolbarAction(ToolbarItem.UNDO, 5);
        processDefinitionEditorPage.executeToolbarAction(ToolbarItem.REDO, 5);
        assertEquals(endCopy.getLabel(), "This is a label.");
        processDefinitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "undoRedoForElementsBinding")
    @Features("Canvas")
    @Stories("US201255")
    @GUID("786deb0d-005c-4f86-986b-85d5b0f07253")
    public void undoRedoForElementsBinding() {
        processDefinitionsTabPage = Optional.ofNullable(processDefinitionsTabPage)
                .orElse(new ProcessDefinitionsTabPage(wd));
        processDefinitionsTabPage.navigateToPage();
        processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        ActiveElement start = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.START);
        ActiveElement end = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.END);
        Link link = processDefinitionEditorPage.bindElements(start, end);
        processDefinitionEditorPage.executeToolbarAction(ToolbarItem.UNDO);
        verifyTrue(!isElementPresent(link.getModelId()));
        processDefinitionEditorPage.executeToolbarAction(ToolbarItem.REDO);
        assertTrue(isElementPresent(link.getModelId()));
        processDefinitionsTabPage.navigateToPage();
    }
}