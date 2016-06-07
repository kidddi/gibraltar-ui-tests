package com.bmc.gibraltar.automation.tests.businesslogic.ui.rule.designer;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.rule.RuleAction;
import com.bmc.gibraltar.automation.items.rule.RuleStencilGroup;
import com.bmc.gibraltar.automation.pages.JsonEditorPage;
import com.bmc.gibraltar.automation.pages.RuleDefinitionsTabPage;
import com.bmc.gibraltar.automation.pages.RuleEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.rule.RuleAction.*;
import static com.bmc.gibraltar.automation.items.rule.RuleStencilGroup.PLATFORM_ACTIONS;
import static com.bmc.gibraltar.automation.items.rule.RuleStencilGroup.QUALIFICATION;
import static com.bmc.gibraltar.automation.items.tab.Palette.DEFAULT_TAB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

public class RuleDesignerTests extends AppManagerBaseTest {
    private RuleDefinitionsTabPage ruleTab;
    private RuleEditorPage ruleEditor;
    private List<String> groupsInStencil;
    private Set<RuleStencilGroup> expectedGroups = EnumSet.allOf(RuleStencilGroup.class);

    @BeforeClass
    protected void goToRuleDesigner() {
        log.info("BeforeClass start.");
        ruleTab = new RuleDefinitionsTabPage(wd).navigateToPage();
        ruleEditor = ruleTab.initiateNewRule();
        log.info("BeforeClass end.");
        groupsInStencil = ruleEditor.getProcessTab(DEFAULT_TAB).getGroupsList();
    }

    @AfterClass
    protected void closeRuleDesigner() {
        ruleEditor.closeRule();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "ruleDesignerJsonEditor")
    @Features("P541 - Rule Designer")
    @Stories("US207608")
    @Issue("SW00498841")
    @GUID("85a047f5-db66-4dd0-a1a0-ee22ab8127d1")
    @Bug("SW00498841")
    public void ruleDesignerJsonEditor() {
        JsonEditorPage jsonEditorPage = ruleEditor.goToJsonEditor();
        verifyTrue(jsonEditorPage.isJsonEditorViewEnabled(), "The Json editor is not present.");
        verifyTrue(jsonEditorPage.isJsonEditorReadonly(), "The Json editor is not readonly.");
        jsonEditorPage.clickBtnToggle();
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "stencilGroupsInRuleDesigner")
    @Features("P541 - Rule Designer")
    @Stories("US207608")
    @GUID("c943dcf4-2537-4fcf-8633-7f6e97e5aab9")
    public void stencilGroupsInRuleDesigner() {
        expectedGroups.stream().map(group -> group.getGroupName().toUpperCase())
                .forEach(group -> assertTrue(groupsInStencil.contains(group),
                        "Group " + group + " is not present!"));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "numberOfGroupsInStencil")
    @Features("[P541] Rule Designer")
    @Stories("US207608")
    @GUID("d1bd6401-56ab-43df-842b-262b0ef09d9e")
    public void numberOfGroupsInStencil() {
        assertEquals(groupsInStencil.size(), RuleStencilGroup.values().length,
                "There are additional groups in Stencil.");
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "triggerElementCannotBeRemoved")
    @Features("[P541] Rule Designer")
    @Stories("US207608")
    @GUID("002f1b9b-406c-4231-b35a-402c799cd28c")
    public void triggerElementCannotBeRemoved() {
        assertTrue(ruleEditor.isTriggerElementPresent(), "Trigger element is not present.");
        ruleEditor.clearCanvas();
        assertTrue(ruleEditor.isTriggerElementPresent(), "Trigger element was removed.");
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "firstGroupInStencilIsQualification")
    @Features("[P541] Rule Designer")
    @GUID("82260a0e-7c41-45bf-ba11-f7655b024034")
    public void firstGroupInStencilIsQualification() {
        assertEquals(groupsInStencil.get(0), QUALIFICATION.getGroupName().toUpperCase(),
                "'Qualification' is not the first group in Stencil.");
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "secondGroupInStencilIsPlatformActions")
    @Features("[P541] Rule Designer")
    @GUID("a1acaba0-5fa8-4b4d-869d-51149283c617")
    public void secondGroupInStencilIsPlatformActions() {
        assertEquals(groupsInStencil.get(1), PLATFORM_ACTIONS.getGroupName().toUpperCase(),
                "'Platform Actions' is not the second group in Stencil.");
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "customActionGroupsSortedAlphabeticallyByServiceName")
    @Features("[P541] Rule Designer")
    @GUID("92d7e6c1-b7fd-432f-9de5-86ea86a22267")
    public void customActionGroupsSortedAlphabeticallyByServiceName() {
        assertTrue(isAlphabetical(groupsInStencil.subList(2, groupsInStencil.size())),
                "Custom action groups are not sorted alphabetically.");
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "platformActionsOrderInStencil")
    @Features("[P541] Rule Designer")
    @GUID("082a7ebc-e9bb-4e4c-9866-82ffda019465")
    public void platformActionsOrderInStencil() {
        List<String> actualElementsInGroup = ruleEditor.getProcessTab(DEFAULT_TAB).getElementsPresentInGroup(PLATFORM_ACTIONS);
        assertTrue(isAlphabetical(actualElementsInGroup), "Order of the element in the 'Platform Action' group is not alphabetical.");
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "platformActionsElementPresence")
    @Features("[P541] Rule Designer")
    @Stories("US209214: Custom actions in the Rule Designer")
    @TestCaseId("TC945877, TC952779")
    @GUID("53f7c170-fd68-46ce-8b9e-df319381c0f7")
    public void platformActionsElementPresence() {
        List<RuleAction> actualElementsInGroup = ruleEditor.getProcessTab(DEFAULT_TAB)
                .getElementsPresentInGroup(PLATFORM_ACTIONS)
                .stream().map(RuleAction::getElementByName).collect(Collectors.toList());
        assertThat("Not all expected element are present in the 'Platform Action' group.", actualElementsInGroup,
                hasItems(CREATE_RECORD_INSTANCE, SET_FIELD, SHOW_MESSAGE, UPDATE_RECORD_INSTANCE, START_PROCESS));
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "errorDisplayedIfRecordDefinitionForTriggerIsNotSpecified")
    @Features("[P541] Rule Designer")
    @Stories("US209214: Custom actions in the Rule Designer")
    @TestCaseId("TC945882")
    @GUID("ac916325-e696-4a07-9f8a-52a0d9fd4333")
    public void errorDisplayedIfRecordDefinitionForTriggerIsNotSpecified() {
        ruleEditor.dragAndDropElements(CREATE_RECORD_INSTANCE);
        assertTrue(ruleEditor.getAllErrorMessages().contains(
                        "You must select a record definition before adding actions to the rule."),
                "Error message is not displayed if a Rule Action element is added to the Rule " +
                        "before the Rule Definition is associated with any record definitions");
        ruleEditor.selectPrimaryRecordDefinition("Task");
        ruleEditor.dragAndDropElement(CREATE_RECORD_INSTANCE);
        assertTrue(ruleEditor.getAllErrorMessages().isEmpty());
    }
}