package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.items.Task;
import com.bmc.gibraltar.automation.items.component.ActivityFeed;
import com.bmc.gibraltar.automation.items.component.AttachmentsPanel;
import org.openqa.selenium.WebDriver;

public class TaskDetailsPage extends BasePage {
    //TODO: remove this class, as this old view is removed from the Task Manager
    private String taskPageHeader = "xpath=//div[contains(@class, 'tms-task-editor')]";
    private String taskName = "xpath=//div[contains(@class, 'tms-task-name')]";
    private String taskId = "xpath=//div[contains(@class, 'tms-task-id')]";
    private String assignedTo = "xpath=//div[contains(@class, 'tms-task-assigned-to')]";
    private String status = "xpath=//div[contains(@class, 'tms-task-status')]/a";
    private String dueDate = "xpath=//div[contains(@class, 'tms-task-due-date')]";
    private String priority = "xpath=//span[contains(@class, 'tms-task-priority')]";
    private String notes = "xpath=//div[contains(@class, 'tms-task-notes')]";
    private String notesContainer = "//*[contains(local-name(), 'tms-task-note-container')]";
    private String attachmentsContainer = "//*[contains(local-name(), 'rx-attachment-panel-old')]";
    private AttachmentsPanel attachments;
    private ActivityFeed feed;

    public TaskDetailsPage(WebDriver driver) {
        super(driver);
        feed = new ActivityFeed(wd, notesContainer);
        attachments = new AttachmentsPanel(wd, attachmentsContainer);
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(taskPageHeader);
    }

    public Task getTaskInfo() {
        String tName = getElement(taskName).getText();
        String tId = getElement(taskId).getText().replaceAll("Task ", "");
        String assignee = getElement(assignedTo).getText();
        String tStatus = getElement(status).getText();
        String tDueDate = getElement(dueDate).getText();
        String tPriority = getElement(priority).getText();
        String tNotes = getElement(notes).getText();
        return new Task(tName, tId, assignee, tStatus, tDueDate, tPriority, tNotes);
    }

    public ActivityFeed getFeed() {
        return feed;
    }

    public AttachmentsPanel getAttachmentsPanel() {
        return attachments;
    }
}
