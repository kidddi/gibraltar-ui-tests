package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.Application;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.tab.Palette;
import com.bmc.gibraltar.automation.items.tab.PaletteForProcess;
import com.bmc.gibraltar.automation.pages.LoginPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class RecentlyUsedTabTest extends AppManagerBaseTest {
    private ProcessDefinitionsTabPage configurePage;
    private ProcessDefinitionEditorPage processEditorView;
    private PaletteForProcess paletteTab;

    @Test(groups = Groups.CATEGORY_FULL, description = "recentlyUsedTabOfTheProcessDesignerStencil")
    @Features("Palette")
    @Stories("US203479")
    @GUID("2f8f8b3a-e056-4e73-adf8-def6e4d3729f")
    public void recentlyUsedTabOfTheProcessDesignerStencil() {
        // TODO needs to be divided into short tests
        configurePage = new ProcessDefinitionsTabPage(wd);
        processEditorView = new ProcessDefinitionEditorPage(wd);
        configurePage.navigateToPage();
        configurePage.initiateNewProcess();
        paletteTab = processEditorView.getTab(Palette.DEFAULT_TAB);
        PaletteForProcess recentUsedTab = processEditorView.getTab(Palette.RECENT_TAB);
        recentUsedTab.switchToTab();
        recentUsedTab.verifyPanelIsExpanded();
        recentUsedTab.verifyTabActive();
        recentUsedTab.clearRecentsButton();
        verifyEquals(recentUsedTab.getGroupsCount(), 0, "Number of groups is wrong.");
        recentUsedTab.verifyRecentTabElementsAmountPresent(0);
        paletteTab.switchToTab();
        ElementOfDesigner[] elements = paletteTab.dropDownFirstElements(20);
        recentUsedTab.switchToTab();
        recentUsedTab.verifyPaletteHasElements(elements);
        recentUsedTab.clearRecentsButton();
        paletteTab.switchToTab();
        elements = paletteTab.dropDownFirstElements(25);
        recentUsedTab.switchToTab();
        recentUsedTab.verifyPaletteHasElements(elements);
        paletteTab.switchToTab();
        elements = paletteTab.dropDownFirstElements(5);
        recentUsedTab.switchToTab();
        recentUsedTab.verifyPaletteHasElements(elements);
        processEditorView.closeProcess();
        configurePage.openAnySavedProcess();
        recentUsedTab.switchToTab();
        recentUsedTab.verifyPaletteHasElements(elements);
        paletteTab.switchToTab();
        elements = paletteTab.dropDownFirstElements(5);
        processEditorView.closeProcess();
        configurePage.logout();
        LoginPage loginPage = new LoginPage(wd, Application.APPLICATION_MANAGER);
        loginPage.login(user.getUsername(), user.getPassword());
        configurePage.navigateToPage();
        configurePage.initiateNewProcess();
        recentUsedTab.switchToTab();
        recentUsedTab.verifyPaletteHasElements(elements);
        recentUsedTab.isGroupsInAlphabeticalOrder();
    }
}