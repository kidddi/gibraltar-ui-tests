package com.bmc.gibraltar.automation.tests.businesslogic.ui.rule;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.ActionBar;
import com.bmc.gibraltar.automation.pages.RuleDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import com.bmc.gibraltar.automation.utils.JsonUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Arrays;
import java.util.List;

import static com.bmc.gibraltar.automation.items.ActionBar.Action.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class RuleDefinitionsTabTest extends AppManagerBaseTest {
    private RuleDefinitionsTabPage ruleTab;

    @BeforeClass
    protected void goToRuleDesigner() {
        log.info("BeforeClass for " + getClass().getName());
        ruleTab = new RuleDefinitionsTabPage(wd).navigateToPage();
        log.info("BeforeClass end.");
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "deleteRuleDefinition")
    @Features("[P541] Rule Designer")
    @Stories("US207608")
    @Issue("SW00498841: Rule Definitions. Created rule definitions are not removed by Delete button")
    @GUID("a9f9e80b-45ec-43be-b7e7-0ad0c4444e6f")
    @Bug("SW00498841")
    public void deleteRuleDefinition() {
        String ruleName = "Rule" + RandomStringUtils.randomAlphanumeric(6);
        RestDataProvider rest = new RestDataProvider();
        // TODO: remove hardcoded record definition name
        if (!rest.getRecordDefinitionNames().contains("MesageTask")) {
            String jsonOfRecordDefinition = JsonUtils.getJsonFromFile("RecordDefinitionForRules.json");
            rest.createRecordDefinition(jsonOfRecordDefinition);
        }
        rest.createRuleByTemplate(ruleName);
        ruleTab.refresh();
        ruleTab.selectRule(ruleName);
        ruleTab.actionBar(DELETE).verifyActionEnabled();
        ruleTab.deleteRule(ruleName);
        ruleTab.refresh();
        assertFalse(ruleTab.isRuleExistent(ruleName), "Rule definition was not deleted.");
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "columnNamesOnRuleDefinitionsPage")
    @Features("[P541] Rule Designer")
    @Stories("US207608")
    @GUID("4f9db075-11c7-445a-8b0d-3b0a142afd86")
    public void columnNamesOnRuleDefinitionsPage() {
        assertThat("Not all expected columns are present.", ruleTab.getColumnNames(),
                hasItems("Rule Definition Name", "Owner", "Modified Date", "Enabled"));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "actionsOnRuleDefinitionsPage")
    @Features("[P541] Rule Designer")
    @Stories("US207608")
    @GUID("8a92e983-2bae-4bf4-8663-37c8dc7cfc13")
    public void actionsOnRuleDefinitionsPage() {
        List<ActionBar.Action> actions = Arrays.asList(NEW, DELETE, REFRESH);
        actions.stream().forEach(action -> assertTrue(ruleTab.actionBar(action).isActionPresent(),
                action + " action is not present on the Rule Definitions page."));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "defaultStateOfActionsOnRuleDefinitionsPage")
    @Features("[P541] Rule Designer")
    @Stories("US207608")
    @GUID("cfc249d2-b996-4daa-bbbb-437b381209b6")
    public void defaultStateOfActionsOnRuleDefinitionsPage() {
        assertFalse(ruleTab.actionBar(DELETE).isActionEnabled(), "Delete button is enabled.");
    }
}