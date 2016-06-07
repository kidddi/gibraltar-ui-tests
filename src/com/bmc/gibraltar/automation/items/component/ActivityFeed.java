package com.bmc.gibraltar.automation.items.component;

import com.bmc.gibraltar.automation.framework.utils.Bindings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class works with the activity feed both in the Task Manager and Application Manager
 */
public class ActivityFeed extends Bindings {
    private String notesContainer = "xpath=";
    private String postedNotes = "//li[contains(@class, 'note')]//span[contains(@class, 'tms-note-details') " +
            "or contains(@class, 'note-content-details')]";
    private String newNoteTextarea = "//textarea[contains(@class, 'activity-textfield') " +
            "or contains(@class, 'tms-note-current')]";
    private String postButton = "//button[contains(@ng-disabled, 'postingInProgress') " +
            "or contains(@class, 'tms-note-add')]";
    private String loadingSpinner = "xpath=//div[contains(@class, 'cg-busy-animation')]";

    public ActivityFeed(WebDriver driver) {
        wd = driver;
    }

    public ActivityFeed(WebDriver driver, String notesContainer) {
        wd = driver;
        this.notesContainer = "xpath=" + notesContainer;
    }

    @Step
    public List<String> getNotes() {
        List<String> notes = new ArrayList<>();
        List<WebElement> allNotes = getElements(notesContainer + postedNotes);
        notes.addAll(allNotes.stream().map(WebElement::getText).collect(Collectors.toList()));
        return notes;
    }

    @Step
    public ActivityFeed postNote(String note) {
        waitForElementNotPresent(loadingSpinner);
        typeKeys(notesContainer + newNoteTextarea, note);
        click(notesContainer + postButton);
        waitForElementNotPresent(loadingSpinner, 5);
        WebDriverWait wait = new WebDriverWait(wd, 5);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath(postedNotes), note));
        return this;
    }
}
