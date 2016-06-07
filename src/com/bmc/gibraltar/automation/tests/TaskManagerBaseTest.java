package com.bmc.gibraltar.automation.tests;

import com.bmc.gibraltar.automation.items.Application;
import com.bmc.gibraltar.automation.pages.LoginPage;
import com.bmc.gibraltar.automation.pages.TasksTabPage;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class TaskManagerBaseTest extends BaseTest {

    @BeforeClass
    protected void initiateClass() {
        log.info("Logging in to Task Manager");
        loginToTaskManager(user.getUsername(), user.getPassword());
    }

    @AfterClass(alwaysRun = true)
    protected void quitClass() {
        log.info("Logging out from Task Manager.");
        logOut();
    }

    protected void loginToTaskManager(String name, String password) {
        LoginPage loginPage = new LoginPage(wd, Application.TASK_MANAGER);
        loginPage.navigateToPage();
        loginPage.loginTaskManager(name, password);
    }

    protected void logOut() {
        TasksTabPage mainPage = new TasksTabPage(wd).navigateToPage();
        mainPage.logout();
    }
}
