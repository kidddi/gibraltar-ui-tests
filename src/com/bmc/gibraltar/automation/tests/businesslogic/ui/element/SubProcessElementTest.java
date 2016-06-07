package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.processdesigner.ToolbarItem;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Optional;

public class SubProcessElementTest extends AppManagerBaseTest {
    private ProcessDefinitionsTabPage definitionsTabPage;
    private ProcessDefinitionEditorPage editorPage;

    @Test(groups = Groups.CATEGORY_FULL, description = "US203855: prePopulateSubProcessWithStartAndEnd")
    @Features("Canvas")
    @Stories("US203855")
    @GUID("c96cebcb-fb02-47a4-bad1-7a4ed048f34e")
    @Bug("")
    public void prePopulateSubProcessWithStartAndEnd() {
        definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editorPage = definitionsTabPage.initiateNewProcess();
        editorPage.waitForPageLoaded();
        editorPage.clearCanvas();

        ActiveElement subProcess = editorPage.dragAndDropToCanvas(ElementOfDesigner.SUB_PROCESS);
        editorPage.doubleClick(subProcess.getXPath());
        int xCoordinate = Integer.parseInt(subProcess.getPropertyValue(ElementProperties.X));
        int yCoordinate = Integer.parseInt(subProcess.getPropertyValue(ElementProperties.Y));
        int subProcessHeight = Integer.parseInt(subProcess.getPropertyValue(ElementProperties.HEIGHT));
        int subProcessWidth = Integer.parseInt(subProcess.getPropertyValue(ElementProperties.WIDTH));
        editorPage.verifyElementsCountOnCanvas(3);

        ElementOfDesigner[] elements = new ElementOfDesigner[]{ElementOfDesigner.START, ElementOfDesigner.END};
        for (ElementOfDesigner el : elements) {
            ActiveElement elWithinSubProcess = editorPage.getDroppedElement(el);
            int elX = Integer.parseInt(elWithinSubProcess.getPropertyValue(ElementProperties.X));
            int elY = Integer.parseInt(elWithinSubProcess.getPropertyValue(ElementProperties.Y));
            int elHeight = Integer.parseInt(elWithinSubProcess.getPropertyValue(ElementProperties.HEIGHT));
            int elWidth = Integer.parseInt(elWithinSubProcess.getPropertyValue(ElementProperties.WIDTH));
            verifyTrue(xCoordinate + subProcessWidth > elX + elWidth);
            verifyTrue(yCoordinate + subProcessHeight > elY + elHeight);
        }
    }

    @Test(dependsOnMethods = "prePopulateSubProcessWithStartAndEnd", groups = Groups.CATEGORY_FULL, description = "US203855: undoRedoOperationsWithSubProcess")
    @Features("Canvas")
    @Stories("US203855")
    @GUID("91e8d1d1-f665-46a5-b016-3f5e877037e2")
    @Bug("")
    public void undoRedoOperationsWithSubProcess() {
        editorPage = Optional.ofNullable(editorPage).orElse(new ProcessDefinitionEditorPage(wd).navigateToPage());
        editorPage.verifyElementsCountOnCanvas(3);
        editorPage.executeToolbarAction(ToolbarItem.UNDO, 3);
        editorPage.verifyElementsCountOnCanvas(0);
        editorPage.executeToolbarAction(ToolbarItem.REDO, 3);
        editorPage.verifyElementsCountOnCanvas(3);
    }
}