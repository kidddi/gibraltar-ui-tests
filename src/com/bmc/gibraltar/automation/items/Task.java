package com.bmc.gibraltar.automation.items;

public class Task {
    private String taskId;
    private String taskName;
    private String assignedTo;
    private String status;
    private String dueDate;
    private String priority;
    private String notes;

    public Task(String taskId, String taskName, String assignedTo, String status, String dueDate, String priority, String notes) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.assignedTo = assignedTo;
        this.status = status;
        this.dueDate = dueDate;
        this.priority = priority;
        this.notes = notes;
    }

    public Task(String taskId, String taskName, String assignedTo, String status, String dueDate) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.assignedTo = assignedTo;
        this.status = status;
        this.dueDate = dueDate;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
