package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.framework.utils.PropertiesUtils;
import com.bmc.gibraltar.automation.pages.ViewDefinitionsPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.util.Arrays;
import java.util.List;

import static com.bmc.gibraltar.automation.items.ActionBar.Action;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

public class ViewDefinitionsTabTest extends AppManagerBaseTest {
    private ViewDefinitionsPage viewDefinitionsPage;

    @BeforeClass
    public void goToViewDefinitionsTabPage() {
        appManager.navigateToPage();
        appManager.navigateToTab("View Definitions");
        viewDefinitionsPage = new ViewDefinitionsPage(wd);
    }

    @Test
    @Features("[P528] Process owner tailors view")
    @Stories("US206930")
    @TestCaseId("TC928109")
    public void checkColumnNamesOnViewDefinitionsPage() {
        assertThat("Not all expected columns are present on the View Definitions page.",
                viewDefinitionsPage.getColumnNames(),
                hasItems("View Definition Name", "Owner", "Modified Date"));
    }

    @Test
    @Features("[P528] Process owner tailors view")
    @Stories("US206930")
    @TestCaseId("TC928108")
    public void checkIconsOnViewDefinitionsPage() {
        List<Action> actionItems = Arrays.asList(Action.NEW, Action.DELETE, Action.REFRESH);
        actionItems.stream().forEach(action -> assertTrue(isElementPresent(action.locator)));
    }

    @Test
    @Features("[P528] Process owner tailors view")
    @Stories("US206930")
    @TestCaseId("TC928108")
    //TODO Do we need such test?
    public void urlOfViewDefinitionsPage() {
        assertEquals(wd.getCurrentUrl(), PropertiesUtils.getAppServerUrl() + "/app/application/task-manager/views");
    }

    @Test
    @Features("[P528] Process owner tailors view")
    @Stories("US206930")
    @TestCaseId("TC928108")
    public void stateOfDeleteButtonOnViewDefinitionsPage() {
        assertFalse(viewDefinitionsPage.actionBar()
                .forAction(Action.DELETE).isActionEnabled(), Action.DELETE.name() + " action is enabled.");
    }

    @Test
    @Features("[P528] Process owner tailors view")
    @Stories("US206930")
    @TestCaseId("TC928108")
    public void stateOfNewButtonOnViewDefinitionsPage() {
        assertTrue(viewDefinitionsPage.actionBar()
                .forAction(Action.NEW).isActionEnabled(), Action.NEW.name() + " action is disabled.");
    }
}