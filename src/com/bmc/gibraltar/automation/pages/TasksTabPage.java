package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.items.Task;
import com.bmc.gibraltar.automation.items.TaskStatus;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.List;

import static com.bmc.gibraltar.automation.items.ActionBar.Action.NEW;

public class TasksTabPage extends TaskManagerHomePage {
    private String headerMyTasks = "xpath=//h1[text()='My Tasks']";
    private String field = "xpath=//*[text() ='%s']/..//%s";
    private String saveButton = "xpath=//button[@ng-click='saveTask()']";
    private String closeButton = "xpath=//button[@ng-click='closeEditor()']";

    public TasksTabPage(WebDriver driver) {
        super(driver);
        tabName = "Tasks";
    }

    @Override
    public String getPageUrl() {
        return TASK_MANAGER_URL + "/tms/tasks";
    }

    @Override
    public boolean isPageLoaded() {
        boolean loaded = isElementPresent(headerMyTasks);
        if (loaded) {
            log.info(" ViewUpdateTabPage is loaded");
        } else {
            log.info("ViewUpdateTabPage - not  loaded");
        }
        return loaded;
    }

    @Override
    public String getTabName() {
        return "Tasks";
    }

    @Step
    public TasksTabPage save() {
        click(saveButton);
        waitForElement(headerMyTasks);
        return this;
    }

    @Step
    public TasksTabPage clickOnNewButton() {
        actionBar(NEW).click();
        return this;
    }

    @Step
    public TasksTabPage fillFieldsByDefault() {
        waitForElement(closeButton);
        for (TaskFields fld : TaskFields.values()) {
            String locator = String.format(field, fld.fieldName, fld.fieldType);
            if (fld.fieldType.equals("select")) {
                selectDropDown(locator, fld.defaultValue);
            } else {
                typeKeysWithEnter(locator, fld.defaultValue);
            }
        }
        return this;
    }

    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        String tasksPath = "xpath=//div[@class='ngCanvas']/div";
        List<WebElement> tasks = getElements(tasksPath);
        for (int i = 1; i <= tasks.size(); i++) {
            String taskId = getElement(tasksPath + "[" + i + "]/div[1]").getText();
            String taskName = getElement(tasksPath + "[" + i + "]/div[2]").getText();
            String assignee = getElement(tasksPath + "[" + i + "]/div[3]").getText();
            String status = getElement(tasksPath + "[" + i + "]/div[4]").getText();
            String dueDate = getElement(tasksPath + "[" + i + "]/div[5]").getText();
            Task task = new Task(taskId, taskName, assignee, status, dueDate);
            allTasks.add(task);
        }
        return allTasks;
    }

    public boolean isTaskExists(String taskName) {
        boolean result = false;
        List<Task> tasks = getAllTasks();
        for (Task task : tasks) {
            if (task.getTaskName().contains(taskName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Step
    public int getTasksCount(TaskStatus status) {
        String countOfTasks = getText(status.getPath()).replaceAll("[^0-9]", "");
        return Integer.parseInt(countOfTasks);
    }

    public enum TaskFields {
        TASK_NAME("Task Name", "input", "Name"),
        PRIORITY("Priority", "select", "Low"),
        STATUS("Status", "select", "Assigned"),
        NOTES("Notes", "textarea", "some text"),
        DUE_DATE("Due Date", "input[@ng-model='dueDateField.value']", "Jun 19, 2016"),
        ASSIGNED_TO("Assigned To", "input", "Demo");

        String fieldName;
        String fieldType;
        String defaultValue;

        TaskFields(String fieldName, String fieldType, String defaultValue) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.defaultValue = defaultValue;
        }
    }
}