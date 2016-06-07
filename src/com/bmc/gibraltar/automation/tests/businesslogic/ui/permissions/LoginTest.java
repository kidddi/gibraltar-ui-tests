package com.bmc.gibraltar.automation.tests.businesslogic.ui.permissions;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.Application;
import com.bmc.gibraltar.automation.pages.LoginPage;
import com.bmc.gibraltar.automation.pages.TasksTabPage;
import com.bmc.gibraltar.automation.tests.BaseTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;
import java.util.stream.Stream;

public class LoginTest extends BaseTest {
    private TasksTabPage taskManagerHomePage;

    @AfterMethod(alwaysRun = true)
    public void logoutFromApp() {
        taskManagerHomePage.logout();
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "User should be able to login with correct credentials: loginToTaskManager")
    @Features("Login Page")
    @Stories("User should be able to login with correct credentials")
    @GUID("159cb9d9-cf74-440a-8d85-3764cdc5e16a")
    public void loginToTaskManager() {
        LoginPage loginPage = new LoginPage(wd, Application.TASK_MANAGER).navigateToPage();
        taskManagerHomePage = loginPage.loginTaskManager(user.getUsername(), user.getPassword());
        assertTrue(taskManagerHomePage.isPageLoaded(), "Could not login to the Task Manager.");
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "verifyTabsNames")
    @Features("Process Designer UI")
    @Stories("US202009")
    @GUID("14f7714a-23c2-4c5b-8192-7f491a566f7a")
    public void verifyTabsNames() {
        LoginPage loginPage = new LoginPage(wd, Application.TASK_MANAGER).navigateToPage();
        taskManagerHomePage = loginPage.loginTaskManager(user.getUsername(), user.getPassword());
        List<String> tabsNames = taskManagerHomePage.getAllTabsNames();
        Stream.of("Tasks", "Messages", "Approvals", "Manage Processes")
                .forEach(presentTabPage -> assertTrue(tabsNames.contains(presentTabPage)));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyTabsNames")
    @Features("Process Designer UI")
    @Stories("US202009")
    public void verifyTaskManagerDoesNotContainDesigners() {
        LoginPage loginPage = new LoginPage(wd, Application.TASK_MANAGER).navigateToPage();
        taskManagerHomePage = loginPage.loginTaskManager(user.getUsername(), user.getPassword());
        List<String> tabsNames = taskManagerHomePage.getAllTabsNames();
        Stream.of("Process Definitions", "Record Definitions")
                .forEach(notPresentTabPage -> assertFalse(tabsNames.contains(notPresentTabPage)));
    }
}