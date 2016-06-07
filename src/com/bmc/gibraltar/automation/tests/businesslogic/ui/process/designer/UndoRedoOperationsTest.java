package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.processdesigner.ToolbarItem;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionJsonEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class UndoRedoOperationsTest extends AppManagerBaseTest {
    private ProcessDefinitionJsonEditorPage processDefinitionJsonEditorPage;
    private ProcessDefinitionEditorPage procDefEditPage;

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyUndoRedoOptionsAreDisabled")
    @Features("Canvas")
    @Stories("US201255")
    @GUID("65d91ba6-d191-4b71-8369-a76ca4b48f3f")
    public void verifyUndoRedoOptionsAreDisabled() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = processDefinitionsTabPage.initiateNewProcess();
        procDefEditPage.verifyButtonOnCanvasToolbarIsDisabled(ToolbarItem.REDO);
        procDefEditPage.verifyButtonOnCanvasToolbarIsDisabled(ToolbarItem.UNDO);
        processDefinitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "enableJsonEditor")
    @Features("JSon editor")
    @Stories("US200590")
    @GUID("63393c92-7df3-48bc-98d4-7c7561e290c4")
    public void enableJsonEditor() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = processDefinitionsTabPage.initiateNewProcess();
        procDefEditPage.dragAndDropToCanvas(ElementOfDesigner.SEND_MESSAGE);
        procDefEditPage.clearCanvas(true);
        procDefEditPage.verifyCanvasIsEmpty();
        procDefEditPage.clickBtnToggle();
        processDefinitionJsonEditorPage = new ProcessDefinitionJsonEditorPage(wd);
        processDefinitionJsonEditorPage.verifyJsonEditorIsEnabled();
        processDefinitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "undoRedoOptionsForRemoveAndAddBackElements")
    @Features("Canvas")
    @Stories("US201255")
    @GUID("3de059a2-2aca-4f08-bc82-1729f14d18ad")
    public void undoRedoOptionsForRemoveAndAddBackElements() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = processDefinitionsTabPage.initiateNewProcess();
        procDefEditPage.dragAndDropToCanvas(ElementOfDesigner.SEND_MESSAGE);

        procDefEditPage.clearCanvas(true);
        procDefEditPage.verifyCanvasIsEmpty();
        procDefEditPage.executeToolbarAction(ToolbarItem.UNDO);
        procDefEditPage.verifyElementsCountOnCanvas(3);

        procDefEditPage.executeToolbarAction(ToolbarItem.REDO);
        procDefEditPage.verifyElementsCountOnCanvas(0);

        procDefEditPage.executeToolbarAction(ToolbarItem.UNDO);
        procDefEditPage.deleteElement(procDefEditPage.getDroppedElement(ElementOfDesigner.START));
        procDefEditPage.verifyButtonOnCanvasToolbarIsDisabled(ToolbarItem.REDO);
        processDefinitionsTabPage.navigateToPage();
    }
}