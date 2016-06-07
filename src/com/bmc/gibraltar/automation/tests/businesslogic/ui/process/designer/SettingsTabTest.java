package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.PaletteGroup;
import com.bmc.gibraltar.automation.items.tab.Palette;
import com.bmc.gibraltar.automation.items.tab.PaletteForProcess;
import com.bmc.gibraltar.automation.items.tab.SettingsTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.tab.Palette.*;

public class SettingsTabTest extends AppManagerBaseTest {
    private static PaletteGroup[] processDesignerGroups = PaletteGroup.values();
    private ProcessDefinitionEditorPage processDefinitionEditorPage;
    private SettingsTab settingsTab;

    @DataProvider(name = "tabNames")
    public static Object[][] tabNames() {
        return new Object[][]{
                new Object[]{DEFAULT_TAB, ElementOfDesigner.values().length},
                new Object[]{FAVORITES_TAB, 0}
        };
    }

    @Test(dataProvider = "tabNames", alwaysRun = true, groups = Groups.CATEGORY_FULL, description = "selectElementsInSettingsTab")
    @Features("Palette")
    @Stories("US203480")
    @Issue("SW00491373: Checkbox for 'All' line item is not displaying a partial selection")
    @GUID("9fd26a2e-9dd1-4ada-a0f8-6b98ee501472")
    @Bug("SW00491373")
    public void selectElementsInSettingsTab(Palette tabName, int defaultCountOfElementsInTab) {
        testDescription = "Verify selectElementsInSettingsTab: " + "Palette: " + tabName.name()
                + ". defaultCountOfElementsInTab: " + defaultCountOfElementsInTab;
        ProcessDefinitionsTabPage definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processDefinitionEditorPage = definitionsTabPage.initiateNewProcess();
        processDefinitionEditorPage.waitForPageLoaded();
        settingsTab = processDefinitionEditorPage.getTab(SETTINGS_TAB).switchToSettingsTab();
        PaletteForProcess tab = processDefinitionEditorPage.getTab(tabName);
        tab.switchToTab();
        verifyEquals(tab.getAllElementsTagsFromPalette().size(), defaultCountOfElementsInTab);
        if (tabName.equals(DEFAULT_TAB)) {
            deselectElementsPart(tab);
            selectElementsPart(tab);
        } else {
            selectElementsPart(tab);
            deselectElementsPart(tab);
        }
        returnToDefaultState();
        processDefinitionEditorPage.closeProcess();
    }

    @Test(dataProvider = "tabNames", alwaysRun = true, groups = Groups.CATEGORY_FULL, description = "selectGroupInSettingsTab")
    @Features("Palette")
    @Stories("US203480")
    @Issue("SW00491373: Checkbox for 'All' line item is not displaying a partial selection")
    @GUID("d6b20711-2e84-4116-a45a-f4991cec00f9")
    @Bug("SW00491373")
    public void selectGroupInSettingsTab(Palette tabName, int defaultCountOfElementsInTab) {
        testDescription = "Verify selectGroupInSettingsTab: " + "Palette: " + tabName.name()
                + ". defaultCountOfElementsInTab: " + defaultCountOfElementsInTab;
        ProcessDefinitionsTabPage definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processDefinitionEditorPage = definitionsTabPage.initiateNewProcess();
        processDefinitionEditorPage.waitForPageLoaded();
        settingsTab = processDefinitionEditorPage.getTab(SETTINGS_TAB).switchToSettingsTab();
        settingsTab.switchToTab();
        settingsTab.deselectGroupForTab(tabName, processDesignerGroups);
        PaletteForProcess tab = processDefinitionEditorPage.getTab(tabName);
        tab.switchToTab();
        for (PaletteGroup group : processDesignerGroups) {
            List<String> elements = Arrays.asList(ElementOfDesigner.getElementsByGroup(group))
                    .stream().map(ElementOfDesigner::getName).collect(Collectors.toList());
            String[] elementsToBePresent = elements.toArray(new String[elements.size()]);
            settingsTab.selectGroupForTab(tabName, new PaletteGroup[]{group});
            settingsTab.verifyElementsSelectedForTab(tabName, elementsToBePresent);

            tab.switchToTab();
            tab.verifyPaletteHasElements(elementsToBePresent);

            settingsTab.switchToTab();
            settingsTab.deselectGroupForTab(tabName, new PaletteGroup[]{group});
            settingsTab.verifyElementsNotSelectedForTab(tabName, elementsToBePresent);

            tab.switchToTab();
            verifyEquals(tab.getAllElementsFromPalette().size(), 0);
            settingsTab.switchToTab();
        }
        returnToDefaultState();
        processDefinitionEditorPage.closeProcess();
    }

    @Test(dataProvider = "tabNames", alwaysRun = true, groups = Groups.CATEGORY_FULL, description = "selectAllInSettingsTab")
    @Features("Palette")
    @Stories("US203480")
    @GUID("bf0062bf-1a7f-4cf4-9e30-b02de2575cbc")
    public void selectAllInSettingsTab(Palette tabName, int defaultCountOfElementsInTab) {
        testDescription = "Verify selectAllInSettingsTab: " + "Palette: " + tabName.name()
                + ". defaultCountOfElementsInTab: " + defaultCountOfElementsInTab;
        ProcessDefinitionsTabPage definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processDefinitionEditorPage = definitionsTabPage.initiateNewProcess();
        processDefinitionEditorPage.waitForPageLoaded();
        PaletteForProcess tab = processDefinitionEditorPage.getTab(tabName);
        settingsTab = processDefinitionEditorPage.getTab(SETTINGS_TAB).switchToSettingsTab();
        settingsTab.switchToTab();

        if (tabName.equals(DEFAULT_TAB)) {
            settingsTab.selectAllForTab(DEFAULT_TAB);
            deselectAll(tab);
        } else {
            selectAll(tab);
            deselectAll(tab);
        }
        returnToDefaultState();
        processDefinitionEditorPage.closeProcess();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "defaultStateOfSettingsTab")
    @Features("Palette")
    @Stories("US203480")
    @Issue("SW00491373: Checkbox for 'All' line item is not displaying a partial selection")
    @GUID("db85ed7e-aa45-4707-b498-377f575915a9")
    @Bug("SW00491373")
    public void defaultStateOfSettingsTab() {
        ProcessDefinitionsTabPage definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processDefinitionEditorPage = definitionsTabPage.initiateNewProcess();
        processDefinitionEditorPage.waitForPageLoaded();
        String[] presentElements = ElementOfDesigner.getValues().stream().toArray(String[]::new);

        SettingsTab settingsTab = processDefinitionEditorPage.getTab(SETTINGS_TAB).switchToSettingsTab();
        settingsTab.verifyGroupsSelectedForTab(DEFAULT_TAB, processDesignerGroups);
        settingsTab.verifyElementsSelectedForTab(DEFAULT_TAB, presentElements);

        settingsTab.verifyGroupsNotSelectedForTab(FAVORITES_TAB, processDesignerGroups);
        settingsTab.verifyElementsNotSelectedForTab(FAVORITES_TAB, presentElements);

        settingsTab.verifyCheckboxForAllSelected(DEFAULT_TAB);
        settingsTab.verifyCheckboxForAllNotSelected(FAVORITES_TAB);

        processDefinitionEditorPage.closeProcess();
    }

    private void returnToDefaultState() {
        settingsTab.switchToTab();
        settingsTab.selectAllForTab(DEFAULT_TAB);
        settingsTab.deselectAllForTab(FAVORITES_TAB);
    }

    private void selectElementsPart(PaletteForProcess tab) {
        settingsTab.switchToTab();
        List<String> elements = ElementOfDesigner.getValues();
        String[] presentElements = elements.toArray(new String[elements.size()]);

        settingsTab.selectElementForTab(tab.getPaletteName(), presentElements);
        settingsTab.verifyGroupsSelectedForTab(tab.getPaletteName(), processDesignerGroups);
        settingsTab.verifyCheckboxForAllSelected(tab.getPaletteName());
        tab.switchToTab();
        verifyEquals(tab.getAllElementsTagsFromPalette().size(), elements.size());
        tab.verifyPaletteHasElements(presentElements);
    }

    private void deselectElementsPart(PaletteForProcess tab) {
        settingsTab.switchToTab();
        List<String> elements = ElementOfDesigner.getValues();
        String[] presentElements = elements.toArray(new String[elements.size()]);
        settingsTab.deselectElementForTab(tab.getPaletteName(), presentElements);
        settingsTab.verifyGroupsNotSelectedForTab(tab.getPaletteName(), processDesignerGroups);
        settingsTab.verifyCheckboxForAllNotSelected(tab.getPaletteName());
        tab.switchToTab();
        verifyEquals(tab.getAllElementsTagsFromPalette().size(), 0);
    }

    private void selectAll(PaletteForProcess tab) {
        List<String> elements = ElementOfDesigner.getValues();
        String[] presentElements = elements.toArray(new String[elements.size()]);
        settingsTab.selectAllForTab(tab.getPaletteName());
        settingsTab.verifyElementsSelectedForTab(tab.getPaletteName(), presentElements);
        settingsTab.verifyGroupsSelectedForTab(tab.getPaletteName(), processDesignerGroups);

        tab.switchToTab();
        verifyEquals(tab.getAllElementsTagsFromPalette().size(), ElementOfDesigner.getValues().size());
        tab.verifyPaletteHasElements(presentElements);
        settingsTab.switchToTab();
    }

    private void deselectAll(PaletteForProcess tab) {
        List<String> elements = ElementOfDesigner.getValues();
        String[] presentElements = elements.toArray(new String[elements.size()]);
        settingsTab.deselectAllForTab(tab.getPaletteName());
        settingsTab.verifyElementsNotSelectedForTab(tab.getPaletteName(), presentElements);
        settingsTab.verifyGroupsNotSelectedForTab(tab.getPaletteName(), processDesignerGroups);

        tab.switchToTab();
        verifyEquals(tab.getAllElementsTagsFromPalette().size(), 0);
        settingsTab.switchToTab();
    }
}