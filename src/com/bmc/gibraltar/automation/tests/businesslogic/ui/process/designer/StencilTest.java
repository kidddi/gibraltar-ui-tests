package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

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
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.tab.Palette.SETTINGS_TAB;

public class StencilTest extends AppManagerBaseTest {
    private ProcessDefinitionEditorPage processEditorView;
    private PaletteForProcess paletteTab;

    @DataProvider(name = "processDesignerGroups")
    public static Iterator<Object[]> processDesignerGroups() {
        return Arrays.asList(PaletteGroup.values()).stream()
                .map(group -> new Object[]{group, ElementOfDesigner.getElementsByGroup(group)})
                .collect(Collectors.toList()).iterator();
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "groupsPresenceInDefaultTab")
    @Features("Palette")
    @Stories("US199209")
    @GUID("5b4f7166-a1bd-48e2-a256-889ec79495de")
    public void groupsPresenceInDefaultTab() {
        paletteTab = getDefaultTab();
        int actualGroupsCount = paletteTab.getGroupsCount();
        assertEquals(actualGroupsCount, PaletteGroup.values().length,
                "Actual count of groups in the Palette is: " + actualGroupsCount);
        processEditorView.closeProcess();
    }

    @Test(dataProvider = "processDesignerGroups", groups = Groups.CATEGORY_SANITY, description = "elementsPresenceInDefaultTab")
    @Features("Palette")
    @Stories("US199209")
    @GUID("889c6edd-5b0f-45ef-991b-7393ee1c6c5a")
    public void elementsPresenceInDefaultTab(PaletteGroup group, ElementOfDesigner[] elementsInGroup) {
        testDescription = "Verify that elements: " + elementsInGroup + " are present in : " + group.name();
        paletteTab = getDefaultTab();
        List<String> groupsInInspector = paletteTab.getGroupsList();
        verifyTrue(groupsInInspector.contains(group.getGroupName()),
                "Not all expected groups are present in the process instance viewer tab.");
        paletteTab.verifyElementsForGroup(group, elementsInGroup);
        processEditorView.closeProcess();
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "defaultStateOfSettingsTab")
    @Features("Palette")
    @Stories("US203480")
    @GUID("e9913308-3384-4516-9da8-41a4404df9c9")
    public void defaultStateOfSettingsTab() {
        getSettingsTab().verifyGroupsPresence(PaletteGroup.values());
        processEditorView.closeProcess();
    }

    @Test(dataProvider = "processDesignerGroups", groups = Groups.CATEGORY_SANITY, description = "elementPresenceInSettingsTab")
    @Features("Palette")
    @Stories("US203480")
    @GUID("98bc1109-a209-4e49-97d4-26ea02d05671")
    public void elementPresenceInSettingsTab(PaletteGroup group, ElementOfDesigner[] elementsInGroup) {
        testDescription = "Verify elementPresenceInSettingsTab: " + " PaletteGroup: " + group.name();
        Set<String> elementsOfGroup = getSettingsTab().getElementsForGroup(group);
        Arrays.asList(elementsInGroup).stream().forEach(element -> assertTrue(elementsOfGroup.contains(element.getName()),
                "Element '" + element + "' is not present in group: " + group.getGroupName()));
        processEditorView.closeProcess();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "testFilteringElementStencil")
    @Features("Palette")
    @Stories("US199209")
    @GUID("0f3f0030-cce8-4dd4-b9c6-ea72093a3458")
    public void testFilteringElementStencil() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processEditorView = processDefinitionsTabPage.initiateNewProcess();
        PaletteForProcess paletteTab = processEditorView.getTab(Palette.DEFAULT_TAB);
        paletteTab.expandAllGroups();
        paletteTab.collapseAllGroups();

        paletteTab.verifyTrue(0 == paletteTab.getNumberElementsUnderGroup(PaletteGroup.EVENT));
        paletteTab.expandPanelGroup(PaletteGroup.EVENT);
        paletteTab.verifyElementsForGroup(PaletteGroup.EVENT,
                new ElementOfDesigner[]{ElementOfDesigner.START, ElementOfDesigner.END});

        paletteTab.verifyTrue(0 == paletteTab.getNumberElementsUnderGroup(PaletteGroup.ACTIVITY));
        paletteTab.expandPanelGroup(PaletteGroup.ACTIVITY);
        paletteTab.verifyElementsForGroup(PaletteGroup.ACTIVITY,
                new ElementOfDesigner[]{ElementOfDesigner.USER_TASK, ElementOfDesigner.CALL_ACTIVITY});

        paletteTab.searchElement("Start");
        paletteTab.verifyTrue(1 == paletteTab.getNumberElementsUnderGroup(PaletteGroup.EVENT));
        paletteTab.verifyElementsForGroup(PaletteGroup.EVENT,
                new ElementOfDesigner[]{ElementOfDesigner.START});
        processEditorView.closeProcess();
    }

    private PaletteForProcess getDefaultTab() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processEditorView = processDefinitionsTabPage.initiateNewProcess();
        processEditorView.verifyPalletePanelPresent();
        paletteTab = processEditorView.getTab(Palette.DEFAULT_TAB);
        paletteTab.verifyPanelIsExpanded();
        paletteTab.verifyTabActive();
        return paletteTab;
    }

    private SettingsTab getSettingsTab() {
        ProcessDefinitionsTabPage definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processEditorView = definitionsTabPage.initiateNewProcess();
        processEditorView.waitForPageLoaded();
        return processEditorView.getTab(SETTINGS_TAB).switchToSettingsTab();
    }
}