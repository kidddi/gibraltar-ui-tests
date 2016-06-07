package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.tab.Palette;
import com.bmc.gibraltar.automation.items.tab.PaletteForProcess;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionJsonEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class DefaultDesignerViewTest extends AppManagerBaseTest {
    private ProcessDefinitionJsonEditorPage processDefinitionJsonEditorPage;
    private ProcessDefinitionEditorPage procDefEditPage;
    private PaletteForProcess paletteTab;
    private ProcessPropertiesTab inspectorTab;

    @Test(groups = Groups.CATEGORY_FULL, description = "defaultDesignerViewTest")
    @Features("Canvas")
    @Stories("US199212")
    @GUID("f97acb43-2f36-45b6-b1f1-ca035f5bc2e1")
    public void defaultDesignerViewTest() {
        ProcessDefinitionsTabPage tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = tabPage.initiateNewProcess();
        procDefEditPage.verifyDesignerEditorViewEnabled();
        procDefEditPage.clickBtnToggle();
        processDefinitionJsonEditorPage = new ProcessDefinitionJsonEditorPage(wd);
        processDefinitionJsonEditorPage.verifyJsonEditorViewEnabled();
        processDefinitionJsonEditorPage.verifyJsonEditorIsReadonly();
        procDefEditPage.clickBtnToggle();
        procDefEditPage.verifyDesignerEditorViewEnabled();
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "verifyBladesAreExpandedAndOrderOfTabsInPalette")
    @Features("Inspector")
    @Stories("US199212")
    @GUID("4d4761be-481c-4ffd-bbb1-870d5dc83859")
    public void verifyBladesAreExpandedAndOrderOfTabsInPalette() {
        ProcessDefinitionsTabPage tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = tabPage.initiateNewProcess();

        paletteTab = procDefEditPage.getTab(Palette.RECENT_TAB);
        paletteTab.switchToTab();
        paletteTab.verifyTabActive();

        paletteTab = procDefEditPage.getTab(Palette.FAVORITES_TAB);
        paletteTab.switchToTab();
        paletteTab.verifyTabActive();

        paletteTab = procDefEditPage.getTab(Palette.SETTINGS_TAB);
        paletteTab.switchToTab();
        paletteTab.verifyTabActive();

        paletteTab = procDefEditPage.getTab(Palette.DEFAULT_TAB);
        paletteTab.switchToTab();
        paletteTab.verifyTabActive();
        paletteTab.verifyPanelIsExpanded();

        inspectorTab = procDefEditPage.getProcessPropertiesTab();
        inspectorTab.verifyPanelIsExpanded();
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "verifyDefaultProcessName")
    @Features("Inspector")
    @Stories("US199212")
    @GUID("9ca87027-b0ac-48af-a551-613d32550ae4")
    public void verifyDefaultProcessName() {
        ProcessDefinitionsTabPage tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = tabPage.initiateNewProcess();
        procDefEditPage.verifyCurrentProcessName("New Process");
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "verifyNewWorkflowHasStartAndEndElementOnCanvas")
    @Features("Canvas")
    @Stories("US200590")
    @GUID("0b7af448-a36f-4fc0-b6f8-d884ff180395")
    public void verifyNewWorkflowHasStartAndEndElementOnCanvas() {
        ProcessDefinitionsTabPage tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = tabPage.initiateNewProcess();
        procDefEditPage.verifyElementsCountOnCanvas(2);
        assertTrue(procDefEditPage.isElementPresentOnCanvas(ElementOfDesigner.START), "Start is not present on the canvas");
        assertTrue(procDefEditPage.isElementPresentOnCanvas(ElementOfDesigner.END), "End is not present on the canvas");
    }
}