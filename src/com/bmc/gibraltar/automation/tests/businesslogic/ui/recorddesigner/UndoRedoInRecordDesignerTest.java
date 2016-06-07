package com.bmc.gibraltar.automation.tests.businesslogic.ui.recorddesigner;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.record.RecordField;
import com.bmc.gibraltar.automation.pages.RecordDefinitionEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class UndoRedoInRecordDesignerTest extends AppManagerBaseTest {
    private RecordDefinitionEditorPage recordDesigner;

    @Test(groups = Groups.CATEGORY_FULL, description = "presenceUndoRedoForRecordDesigner")
    @Features("Record Definitions")
    @Stories("US204101")
    @GUID("e486e0d8-c3f2-405a-8a16-67b8cd21f2bf")
    public void presenceUndoRedoForRecordDesigner() {
        recordDesigner = new RecordDefinitionEditorPage(wd);
        recordDesigner.navigateToPage();
        recordDesigner.verifyToolBarItemActive(RecordDefinitionEditorPage.ToolBarItem.UNDO, false);
        recordDesigner.verifyToolBarItemActive(RecordDefinitionEditorPage.ToolBarItem.REDO, false);
        recordDesigner.closeRecordDefinition();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyUndoForRecordDesigner")
    @Features("Record Definitions")
    @Stories("US204101")
    @GUID("3e946d0c-34b1-4c40-a88a-fab42db053d1")
    public void verifyUndoForRecordDesigner() {
        setRecordDefinitionNameAndAddField();
        verifyTrue(recordDesigner.isRecordFieldPresent("New Field"));
        recordDesigner.undoLastChange(true);
        verifyFalse(recordDesigner.isRecordFieldPresent("New Field"));
        recordDesigner.redoLastChange(true);
        verifyTrue(recordDesigner.isRecordFieldPresent("New Field"));
        recordDesigner.closeRecordDefinition();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyUndoRedoDisabledAfterSaving")
    @Features("Record Definitions")
    @Stories("US204101")
    @GUID("2fb1114e-e96e-46c9-a11b-5269da495bb7")
    public void verifyUndoRedoDisabledAfterSaving() {
        setRecordDefinitionNameAndAddField();
        recordDesigner.saveRecord();
        recordDesigner.
                verifyToolBarItemActive(RecordDefinitionEditorPage.ToolBarItem.UNDO, false).
                verifyToolBarItemActive(RecordDefinitionEditorPage.ToolBarItem.REDO, false);
        recordDesigner.closeRecordDefinition();
    }

    private void setRecordDefinitionNameAndAddField() {
        recordDesigner = new RecordDefinitionEditorPage(wd);
        recordDesigner.navigateToPage();
        recordDesigner.setRecordName("UndoRedo_Test" + RandomStringUtils.randomAlphanumeric(3));
        RecordField recordField = new RecordField(DataType.BOOLEAN);
        recordDesigner.addRecordFieldDraft(recordField);
    }
    //TODO: add tests for Multiple Undo/Redo actions applied
}