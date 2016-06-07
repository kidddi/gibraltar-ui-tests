package com.bmc.gibraltar.automation.tests.businesslogic.ui.permissions;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.dataprovider.UserDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.Application;
import com.bmc.gibraltar.automation.pages.*;
import com.bmc.gibraltar.automation.tests.BaseTest;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoginPermissionTest extends BaseTest {
    private RestDataProvider dataProvider;

    private static void addPageToSet(Set<TaskManagerHomePage> set, Set<String> tabNames, TaskManagerHomePage page) {
        if (!tabNames.contains(page.getTabName())) {
            set.add(page);
            tabNames.add(page.getTabName());
        }
    }

    @Test(dataProvider = "allCredentials", dataProviderClass = UserDataProvider.class, groups = Groups.CATEGORY_FULL, description = "verifyUserHasAccessToTaskManager")
    @Features("Permissions")
    @Stories("US204778")
    @GUID("5fd23d1f-234e-4cbf-97b0-03debad5667f")
    public void verifyUserHasAccessToTaskManager(String email, String password, String[] permittedGroups) {
        testDescription = "Verifies that user with email: " + email + ", that is in groups: "
                + permittedGroups + " has access to the Task Manager, according to his permitted groups.";
        dataProvider = new RestDataProvider("jonnie@pepsi.com", "password");
        dataProvider.createUser(email + " Test User", email, password, email, permittedGroups);
        LoginPage loginPage = new LoginPage(wd, Application.TASK_MANAGER).navigateToPage();
        if (!(dataProvider.isUserInComputedGroup(email, "Task User Computed")
                || dataProvider.isUserInGroup(email, "Administrator"))) {
            loginPage.login(email, password);
            loginPage.verifyUserHasNoAccess();
        } else {
            TasksTabPage taskManagerHomePage = loginPage.loginTaskManager(email, password);
            List<String> tabsNames = taskManagerHomePage.getAllTabsNames();
            Set<TaskManagerHomePage> allTabsThatShouldBePresent = getAvailableTabsForUser(email);
            // '+1' because the "Tasks" page is not included into allTabsThatShouldBePresent
            taskManagerHomePage.verifyTrue(allTabsThatShouldBePresent.size() + 1 == tabsNames.size());
            for (TaskManagerHomePage page : allTabsThatShouldBePresent) {
                try {
                    page.navigateToPage();
                } catch (TimeoutException e) {
                    log.error("Cannot navigate to " + page.getPageName());
                }
                String tabName = page.getTabName();
                taskManagerHomePage.verifyTrue(tabsNames.contains(tabName));
            }
            taskManagerHomePage.logout();
        }
        dataProvider.logout();
    }

    private Set<TaskManagerHomePage> getAvailableTabsForUser(String userName) {
        Set<TaskManagerHomePage> allTabsThatShouldBePresent = new HashSet<>();
        Set<String> tabNames = new HashSet<>();
        if (dataProvider.isUserInComputedGroup(userName, "Task User Computed")) {
            addPageToSet(allTabsThatShouldBePresent, tabNames, new MessagesPage(wd));
            addPageToSet(allTabsThatShouldBePresent, tabNames, new ApprovalsPage(wd));
        }
        if (dataProvider.isUserInComputedGroup(userName, "Process Manager Computed")) {
            addPageToSet(allTabsThatShouldBePresent, tabNames, new ManageTabPage(wd));
        }
        if (dataProvider.isUserInGroup(userName, "Administrator")) {
            addPageToSet(allTabsThatShouldBePresent, tabNames, new MessagesPage(wd));
            addPageToSet(allTabsThatShouldBePresent, tabNames, new ApprovalsPage(wd));
            addPageToSet(allTabsThatShouldBePresent, tabNames, new ManageTabPage(wd));
        }
        return allTabsThatShouldBePresent;
    }
}