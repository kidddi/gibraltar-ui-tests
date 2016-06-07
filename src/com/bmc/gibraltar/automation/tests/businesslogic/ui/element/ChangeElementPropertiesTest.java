package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.processdesigner.ToolbarItem;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class ChangeElementPropertiesTest extends AppManagerBaseTest {
    private ProcessDefinitionEditorPage procDefEditPage;
    private ActiveElement elementOnCanvas;
    private int defaultElementWidth;
    private int defaultElementHeight;
    private int defaultXCoordinateOnCanvas;
    private int defaultYCoordinateOnCanvas;
    private int elementWidth = 259;
    private int elementHeight = 157;
    private int elementXCoordinateOnCanvas = 100;
    private int elementYCoordinateOnCanvas = 100;

    @Test(groups = Groups.CATEGORY_FULL, description = "US199210: changeElementSize")
    @Features("Canvas")
    @Stories("US199210")
    @GUID("cbb12e50-f816-4a0d-8a19-a51a156b5da6")
    @Bug("")
    public void changeElementSize() {
        ProcessDefinitionsTabPage configurePage = new ProcessDefinitionsTabPage(wd);
        procDefEditPage = new ProcessDefinitionEditorPage(wd);
        configurePage.navigateToPage();
        configurePage.initiateNewProcess();
        elementOnCanvas = procDefEditPage.dragAndDropToCanvas(ElementOfDesigner.CREATE_TASK);
        defaultElementWidth = procDefEditPage.getElementWidth(elementOnCanvas);
        defaultElementHeight = procDefEditPage.getElementHeight(elementOnCanvas);
        ElementPropertiesTab elementTab = procDefEditPage.getElementPropertiesTab();
        elementTab.setPropertyValue(elementOnCanvas, InspectorGroup.GEOMETRY, "Width:", String.valueOf(elementWidth));
        elementTab.setPropertyValue(elementOnCanvas, InspectorGroup.GEOMETRY, "Height:", String.valueOf(elementHeight));
        procDefEditPage.verifyElementSize(elementOnCanvas, elementWidth, elementHeight);
    }

    @Test(dependsOnMethods = "changeElementSize", groups = Groups.CATEGORY_FULL, description = "US199210: changeElementPosition")
    @Features("Canvas")
    @Stories("US199210")
    @GUID("871e2f7a-0048-4e6a-b5df-98a063c3e40c")
    @Bug("")
    public void changeElementPosition() {
        elementOnCanvas = procDefEditPage.getDroppedElement(ElementOfDesigner.CREATE_TASK);
        procDefEditPage.clickOnElement(elementOnCanvas);
        defaultXCoordinateOnCanvas = Integer.valueOf(procDefEditPage.getElementPosition(elementOnCanvas)[0]);
        defaultYCoordinateOnCanvas = Integer.valueOf(procDefEditPage.getElementPosition(elementOnCanvas)[1]);
        procDefEditPage.moveElement(elementOnCanvas, elementXCoordinateOnCanvas, elementYCoordinateOnCanvas);
        procDefEditPage.clickOnFreeSpaceOnCanvas();
        procDefEditPage.verifyElementPosition(elementOnCanvas, elementXCoordinateOnCanvas + defaultXCoordinateOnCanvas,
                elementYCoordinateOnCanvas + defaultYCoordinateOnCanvas);
    }

    @Test(dependsOnMethods = "changeElementPosition", groups = Groups.CATEGORY_FULL, description = "US201255: undoOperationForChangingElementSizeAndPosition")
    @Features("Canvas")
    @Stories("US201255")
    @GUID("c653efd2-0f37-455d-b208-7a576118497e")
    @Bug("")
    public void undoOperationForChangingElementSizeAndPosition() {
        elementOnCanvas = procDefEditPage.getDroppedElement(ElementOfDesigner.CREATE_TASK);
        procDefEditPage.executeToolbarAction(ToolbarItem.UNDO, 8);
        procDefEditPage.verifyElementPosition(elementOnCanvas, defaultXCoordinateOnCanvas, defaultYCoordinateOnCanvas);
        procDefEditPage.verifyElementSize(elementOnCanvas, defaultElementWidth, defaultElementHeight);
    }

    @Test(dependsOnMethods = "undoOperationForChangingElementSizeAndPosition", groups = Groups.CATEGORY_FULL, description = "US201255: redoOperationForChangingElementSizeAndPosition")
    @Features("Canvas")
    @Stories("US201255")
    @GUID("2ea5ab51-1f76-4376-9364-f852200be03fs")
    @Bug("")
    public void redoOperationForChangingElementSizeAndPosition() {
        elementOnCanvas = procDefEditPage.getDroppedElement(ElementOfDesigner.CREATE_TASK);
        procDefEditPage.executeToolbarAction(ToolbarItem.REDO, 8);
        procDefEditPage.verifyElementPosition(elementOnCanvas, elementXCoordinateOnCanvas + defaultXCoordinateOnCanvas,
                elementYCoordinateOnCanvas + defaultYCoordinateOnCanvas);
        procDefEditPage.verifyElementSize(elementOnCanvas, elementWidth, elementHeight);
    }
}