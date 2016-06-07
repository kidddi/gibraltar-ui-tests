package com.bmc.gibraltar.automation.tests.businesslogic.ui.rule.designer;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ElementAction;
import com.bmc.gibraltar.automation.items.rule.ActiveRuleAction;
import com.bmc.gibraltar.automation.items.rule.TriggerEvent;
import com.bmc.gibraltar.automation.items.tab.PaletteForProcess;
import com.bmc.gibraltar.automation.pages.RuleDefinitionsTabPage;
import com.bmc.gibraltar.automation.pages.RuleEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;

import static com.bmc.gibraltar.automation.items.rule.RuleAction.*;
import static com.bmc.gibraltar.automation.items.tab.Palette.*;
import static java.util.Arrays.asList;

public class RuleStencilTabTest extends AppManagerBaseTest {
    private RuleDefinitionsTabPage ruleTab;
    private RuleEditorPage ruleEditor;

    @BeforeClass
    protected void goToRuleDesigner() {
        log.info("BeforeClass start.");
        ruleTab = new RuleDefinitionsTabPage(wd).navigateToPage();
        ruleEditor = ruleTab.initiateNewRule();
        ruleEditor.selectPrimaryRecordDefinition("Task");
        ActiveRuleAction trigger = ruleEditor.getDroppedComponent(TRIGGER);
        ruleEditor.selectRuleElementOnCanvas(trigger);
        ruleEditor.setTriggerEvent(TriggerEvent.ON_CREATE);
        log.info("BeforeClass end.");
    }

    @AfterClass
    protected void closeRuleDesigner() {
        ruleEditor.closeRule();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "recentlyUsedTabInStencil")
    @Features("[P541] Rule Designer")
    @Stories("US207608")
    @GUID("2e3a9290-7f56-4d77-888c-62b6711c5636")
    public void recentlyUsedTabInStencil() {
        ElementAction[] elementsForTest = ruleEditor.dragAndDropElements(CREATE_BASECAMP_PROJECT, BUILD_COMPLETION_CONDITION);
        ruleEditor.verifyElementsArePresentOnCanvas(elementsForTest);
        ruleEditor.clearCanvas();
        PaletteForProcess palette = ruleEditor.switchToTab(RECENT_TAB);
        assertEquals(palette.getAllElementsFromPalette(), asList(elementsForTest),
                "Elements on RECENT_TAB are not the same as were dropped to Canvas.");
        ruleEditor.dragAndDropElements(elementsForTest);
        ruleEditor.verifyElementsArePresentOnCanvas(elementsForTest);
        palette.clearRecentsButton();
        assertTrue(palette.getAllElementsTagsFromPalette().isEmpty());
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "tabsInStencil")
    @Features("[P541] Rule Designer")
    @Stories("US207608")
    @GUID("71e157e0-ca03-4bb9-89aa-cedbfe72e394")
    public void tabsInStencil() {
        List<String> elementsForFavoriteTab = ruleEditor.switchToTab(DEFAULT_TAB).getAllElementsTagsFromPalette();
        PaletteForProcess palette = ruleEditor.switchToTab(SETTINGS_TAB);
        palette.switchToSettingsTab().deselectAllForTab(DEFAULT_TAB).selectAllForTab(FAVORITES_TAB);
        verifyTrue(ruleEditor.switchToTab(DEFAULT_TAB).getAllElementsTagsFromPalette().isEmpty());
        palette = ruleEditor.switchToTab(FAVORITES_TAB);
        verifyEquals(palette.getAllElementsTagsFromPalette(), elementsForFavoriteTab, "Elements on FAVORITES_TAB");
        palette = ruleEditor.switchToTab(SETTINGS_TAB);
        palette.switchToSettingsTab().deselectAllForTab(FAVORITES_TAB).selectAllForTab(DEFAULT_TAB);
    }
}