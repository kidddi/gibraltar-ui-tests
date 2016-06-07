package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.items.component.ActivityFeed;
import com.bmc.gibraltar.automation.items.component.AttachmentsPanel;
import com.bmc.gibraltar.automation.items.component.Component;
import com.bmc.gibraltar.automation.items.component.runtime.RecordEditor;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class ViewRunTimeModePage extends BasePage {
    private String runtimeContainer = "xpath=//*[contains(local-name(), 'rx-runtime-view')]";
    private String winHandleBefore;
    private String componentPlaceholder = "//div[@rx-view-component-id='%s']";
    private String activityFeed = Component.ACTIVITY_FEED.getLocatorInRuntime();
    private String attachmentsContainer = Component.ATTACHMENTS.getLocatorInRuntime();
    private RecordEditor recordInstanceEditorInRunTime;
    private ActivityFeed activityFeedInRunTime;
    private AttachmentsPanel attachmentsPanel;
    private String disabledFieldLocator = "xpath=(//label[*[text()='%s']])[%s]//input[@disabled='disabled']";
    private String hiddenFieldLocator = "xpath=(//div[label[*[text()='%s']]])[%s]";

    public ViewRunTimeModePage(WebDriver driver) {
        super(driver);
    }

    public ViewRunTimeModePage(WebDriver driver, String winHandleBefore) {
        super(driver);
        this.winHandleBefore = winHandleBefore;
    }

    public String getRuntimeContainer() {
        return runtimeContainer;
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(runtimeContainer);
    }

    @Step
    public ViewDefinitionEditorPage close() {
        log.info("Closing the run time mode.");
        wd.close();
        wd.switchTo().window(winHandleBefore);
        ViewDefinitionEditorPage editorPage = new ViewDefinitionEditorPage(wd);
        editorPage.waitForPageLoaded();
        return editorPage;
    }

    @Step
    public List<Component> getComponents() {
        List<Component> componentsInRunTime = Arrays.asList(Component.values()).stream().filter(c
                -> isElementPresent(runtimeContainer + c.getLocatorInRuntime())).collect(Collectors.toList());
        return componentsInRunTime;
    }

    @Step
    public ActivityFeed getActivityFeed() {
        activityFeedInRunTime = new ActivityFeed(wd, activityFeed);
        waitForElementPresent(runtimeContainer + activityFeed, 10);
        return activityFeedInRunTime;
    }

    @Step
    public RecordEditor getRecordEditor() {
        recordInstanceEditorInRunTime = new RecordEditor(wd);
        waitForElementPresent(recordInstanceEditorInRunTime.getRecordInsContainer());
        return recordInstanceEditorInRunTime;
    }

    @Step
    public AttachmentsPanel getAttachmentsPanel() {
        attachmentsPanel = new AttachmentsPanel(wd, attachmentsContainer);
        waitForElementPresent(runtimeContainer + attachmentsContainer, 10);
        return attachmentsPanel;
    }

    @Step
    public ViewRunTimeModePage waitForComponentPresent(String componentId) {
        waitForElementPresent(runtimeContainer + format(componentPlaceholder, componentId), 15);
        return this;
    }

    @Step
    public ViewRunTimeModePage waitForComponentsPresent(String[] componentIds) {
        for (String componentId : componentIds) {
            waitForElementPresent(runtimeContainer + format(componentPlaceholder, componentId), 15);
        }
        return this;
    }

    /**
     * @param fieldNumber index in DOM. Starts from 1.
     */
    @Step
    public boolean isFieldDisabled(String fieldName, int fieldNumber) {
        return isElementPresent(format(disabledFieldLocator, fieldName, fieldNumber));
    }

    /**
     * @param fieldNumber index in DOM. Starts from 1.
     */
    @Step
    public boolean isFieldHidden(String fieldName, int fieldNumber) {
        return !getElement(format(hiddenFieldLocator, fieldName, fieldNumber)).isDisplayed();
    }

}
