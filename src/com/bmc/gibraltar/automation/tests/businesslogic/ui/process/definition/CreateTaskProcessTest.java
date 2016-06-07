package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.definition;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.TaskStatus;
import com.bmc.gibraltar.automation.pages.ManageTabPage;
import com.bmc.gibraltar.automation.pages.MessagesPage;
import com.bmc.gibraltar.automation.pages.StartProcessPage;
import com.bmc.gibraltar.automation.pages.TasksTabPage;
import com.bmc.gibraltar.automation.tests.TaskManagerBaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Optional;

public class CreateTaskProcessTest extends TaskManagerBaseTest {
    private RestDataProvider dataProvider = new RestDataProvider();
    private String processDefinitionName = "NotificationProcess" + Long.toHexString(System.currentTimeMillis());
    //TODO: add possibility to specify a custom task name, as now the task name is hardcoded in a process definition json
    private String taskName = "TaskName";

    @BeforeClass
    public void createDefinition() {
        new CommonSteps(dataProvider).createProcessDefinitionWithCreateTaskElement(processDefinitionName);
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "startProcessWithCreateTaskElement")
    @Features("Manage Processes tab")
    @Stories("US199758")
    @GUID("1f5ab81c-5f1f-4b4f-abe6-44df5b631278")
    public void startProcessWithCreateTaskElement() {
        ManageTabPage managePage = new ManageTabPage(wd).navigateToPage();
        managePage.selectProcess(processDefinitionName);
        int processCount = managePage.getTasksAmount();
        StartProcessPage startProcessPage = managePage.openProcessStart(processDefinitionName);
        startProcessPage.startTheProcess();
        managePage.verifyCountOfProcesses(processDefinitionName, ++processCount);
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "verifyTaskPresenceAndStatus")
    @Features("Tasks")
    @Stories("US199758")
    @GUID("3bcf3149-25a8-4d25-9b88-5104be7063b5")
    public void verifyTaskPresenceAndStatus() {
        int tasksCount = dataProvider.getTasksCount();
        String processDefId = dataProvider.getProcessDefinitionID(processDefinitionName);
        dataProvider.startProcess(processDefId);
        TasksTabPage updateTabPage = new TasksTabPage(wd).navigateToPage();
        assertTrue(updateTabPage.isTaskExists(taskName), "Task was not created.");
        assertEquals(updateTabPage.getTasksCount(TaskStatus.ALL), ++tasksCount);
        // TODO: add verification that all properties of the created task are correct
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyMessagesCount")
    @Features("Messages")
    @Stories("US199758")
    @GUID("6e057964-891a-4cda-98a2-cefb28db7c96")
    public void verifyMessagesCount() {
        if (!user.getUsername().equals("Demo")) {
            MessagesPage messagesPage = new MessagesPage(wd);
            int messagesCount = messagesPage.getMessagesCount();
            String processDefId = dataProvider.getProcessDefinitionID(processDefinitionName);
            dataProvider.startProcess(processDefId);
            messagesPage = Optional.ofNullable(messagesPage).orElse(new MessagesPage(wd));
            messagesPage.navigateToPage();
            messagesPage.verifyMessagesCount(messagesCount + 1);
        }
    }
}