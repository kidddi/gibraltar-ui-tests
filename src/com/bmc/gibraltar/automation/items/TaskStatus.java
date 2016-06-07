package com.bmc.gibraltar.automation.items;

public enum TaskStatus {
    ALL("", "All Tasks"),
    STAGED("staged", "Staged"),
    ASSIGNED("assigned", "Assigned"),
    WORK_IN_PROGRESS("work-in-progress", "Work In Progress"),
    BLOCKED("blocked", "Blocked"),
    CANCELLED("cancelled", "Cancelled"),
    COMPLETED("completed", "Completed");

    private String taskPath;
    private String label;

    TaskStatus(String taskPath, String label) {
        this.taskPath = taskPath;
        this.label = label;
    }

    public String getTaskPath() {
        return taskPath;
    }

    public void setTaskPath(String taskPath) {
        this.taskPath = taskPath;
    }

    public String getPath() {
        if (getLabel().equals("All Tasks")) {
            return "xpath=//rx-counter-panel/div[1]";
        }
        return "xpath=//rx-counter-panel/div[contains(concat('', @class, ''), '" + taskPath + "')]";
    }

    public String getLabel() {
        return label;
    }
}