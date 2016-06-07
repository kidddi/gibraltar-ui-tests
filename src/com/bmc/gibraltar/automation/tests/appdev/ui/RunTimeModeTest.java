package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.pages.ViewDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ViewRunTimeModePage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class RunTimeModeTest extends AppManagerBaseTest {

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265")
    public void runtimeModeForNotValidTaskId() {
        appManager.navigateToPage();
        ViewDefinitionEditorPage editorPage = appManager.goInitiallyToView("Edit Task");
        ViewRunTimeModePage runTimeModePage = editorPage.goToRunTimeModeWithParams("Task Id", "abc");
        assertTrue(runTimeModePage.isErrorMessageDisplayed("ERROR (302): Entry does not exist in database; GUID : "));
        editorPage = runTimeModePage.close();
        editorPage.close();
    }
}