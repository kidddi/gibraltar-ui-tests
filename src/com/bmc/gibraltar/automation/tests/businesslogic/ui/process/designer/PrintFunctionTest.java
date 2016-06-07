package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.processdesigner.ToolbarItem;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class PrintFunctionTest extends AppManagerBaseTest {

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyPrintFunctionPresence")
    @Features("Process Designer UI")
    @Stories("US200202")
    @GUID("dd17735a-0b0d-4cac-9874-4f12d158621e")
    public void verifyPrintFunctionPresence() {
        ProcessDefinitionEditorPage editView = new ProcessDefinitionEditorPage(wd).navigateToPage();
        editView.dragAndDropFromCentre(ElementOfDesigner.EXCLUSIVE_GATEWAY, 400, 100);

        //redefining js window.print() method so it issues browser alert window WebDriver can work with
        executeJS("window.print = function () {alert('Print');};");
        editView.executeToolbarAction(ToolbarItem.PRINT);
        wd.switchTo().alert().accept();
        verifyTrue(editView.isPageLoaded(), "Issue with opening/closing Print alert");
    }
}