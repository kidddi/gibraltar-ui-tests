package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.PaletteGroup;
import com.bmc.gibraltar.automation.items.tab.PaletteForProcess;
import com.bmc.gibraltar.automation.items.tab.SettingsTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import static com.bmc.gibraltar.automation.items.element.PaletteGroup.ACTIVITY;
import static com.bmc.gibraltar.automation.items.tab.Palette.FAVORITES_TAB;
import static com.bmc.gibraltar.automation.items.tab.Palette.SETTINGS_TAB;

public class FavoritesTabTest extends AppManagerBaseTest {
    private ProcessDefinitionEditorPage processDefinitionEditorPage;
    private SettingsTab settingsTab;

    @Test(groups = Groups.CATEGORY_FULL, description = "dragElementFromFavoritesTabToCanvas")
    @Features("Palette")
    @Stories("US203477")
    @GUID("80cf0ab9-2a2e-41df-9840-59a18f5eab84")
    public void dragElementFromFavoritesTabToCanvas() {
        ProcessDefinitionsTabPage definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processDefinitionEditorPage = definitionsTabPage.initiateNewProcess();
        processDefinitionEditorPage.waitForPageLoaded();
        processDefinitionEditorPage.clearCanvas();
        settingsTab = processDefinitionEditorPage.getTab(SETTINGS_TAB).switchToSettingsTab();
        PaletteForProcess tab = processDefinitionEditorPage.getTab(FAVORITES_TAB);
        tab.switchToTab();
        verifyEquals(tab.getAllElementsTagsFromPalette().size(), 0);
        settingsTab.switchToTab();
        settingsTab.selectGroupForTab(FAVORITES_TAB, new PaletteGroup[]{ACTIVITY});
        tab.switchToTab();
        processDefinitionEditorPage.dragAndDropToCanvas(FAVORITES_TAB, ElementOfDesigner.CALL_ACTIVITY);
        assertTrue(processDefinitionEditorPage.isElementPresentOnCanvas(ElementOfDesigner.CALL_ACTIVITY), "CALL_ACTIVITY is not present on the canvas");
        definitionsTabPage.navigateToPage();

        processDefinitionEditorPage = definitionsTabPage.initiateNewProcess();
        processDefinitionEditorPage.waitForPageLoaded();
        processDefinitionEditorPage.clearCanvas();
        tab.switchToTab();
        tab.verifyPaletteHasElements(ElementOfDesigner.getElementsByGroup(ACTIVITY));
        definitionsTabPage.navigateToPage();
    }
}
