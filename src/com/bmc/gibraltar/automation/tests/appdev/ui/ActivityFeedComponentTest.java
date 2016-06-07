package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.utils.PropertiesUtils;
import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import com.bmc.gibraltar.automation.items.component.ActivityFeed;
import com.bmc.gibraltar.automation.pages.TaskDetailsPage;
import com.bmc.gibraltar.automation.pages.ViewRunTimeModePage;
import com.bmc.gibraltar.automation.tests.TaskManagerBaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;

import static com.jayway.restassured.path.json.JsonPath.from;

public class ActivityFeedComponentTest extends TaskManagerBaseTest {
    private String taskDetailsPageInTaskManager;
    private TaskDetailsPage detailsPage;
    private String taskEditorView;
    private List<String> notesInTaskDetailsPage;
    private List<String> notesInTaskManager;
    private List<String> notesInAppManager;
    private String activityFeedId;
    private String appServer;

    @BeforeClass
    protected void prepareData() {
        log.info("BeforeClass start");
        appServer = PropertiesUtils.getAppServerUrl();
        RestDataProvider dataProvider = new RestDataProvider();
        String taskJson = new CommonSteps(dataProvider).createTask();
        String taskGuid = from(taskJson).get("fieldInstances.179.value");
        String taskEditorViewDefinitionId = dataProvider.getViewDefinitionId("Edit Task");
        activityFeedId = dataProvider.getComponentDefinitionId(taskEditorViewDefinitionId, "rx-activity-feed");
        dataProvider.logout();
        //TODO: old view is not present in the Task Manager anymore, test needs to be updated
        taskDetailsPageInTaskManager = appServer + "/tms/task/" + taskGuid;
        WebUtils.openPageAndWaitForLoad(wd, taskDetailsPageInTaskManager);
        detailsPage = new TaskDetailsPage(wd);
        detailsPage.waitForPageLoaded();
        ActivityFeed notesContainerInTaskManager = detailsPage.getFeed();
        notesContainerInTaskManager.postNote("note");
        notesInTaskDetailsPage = notesContainerInTaskManager.getNotes();
        taskEditorView = appServer + "/view/" + taskEditorViewDefinitionId + "?param=" + taskGuid;
        notesInTaskManager = postNoteInRuntimeMode("test_note").getNotes();
        log.info("BeforeClass end");
    }

    @Test
    @Features("[P519] Developer uses the SDK")
    @Stories("US207665: The reference application includes a configurable Task view")
    public void postingNotesInTaskManager() {
        verifyTrue(notesInTaskManager.size() == notesInTaskDetailsPage.size() + 1);
        verifyTrue(notesInTaskManager.containsAll(notesInTaskDetailsPage),
                "Notes posted in the Task Manager old view and new views are not the same. " +
                        ". Notes present in the Task Manager (task view) are: " + notesInTaskManager);
    }

    @Test
    @Features("[P519] Developer uses the SDK")
    @Stories("US207665: The reference application includes a configurable Task view")
    public void postingNotesInApplicationManager() {
        taskEditorView = taskEditorView.replace("task-manager", "application-manager");
        notesInAppManager = postNoteInRuntimeMode("test_note_2").getNotes();
        verifyTrue(notesInAppManager.size() == notesInTaskManager.size() + 1);
        verifyTrue(notesInAppManager.containsAll(notesInTaskManager),
                "The Application Manager does not contain a note posted via the Task Manager." +
                        ". Notes present in the Application Manager are: " + notesInAppManager);
        // return back to the Task Manager to ensure posted notes are present
        WebUtils.openPageAndWaitForLoad(wd, taskDetailsPageInTaskManager);
        List<String> notes = detailsPage.getFeed().getNotes();
        verifyTrue(notes.containsAll(notesInAppManager),
                "The Task Manager does not contain a note posted via the Application Manager." +
                        ". Notes present in the Task Manager are: " + notes);
    }

    private ActivityFeed postNoteInRuntimeMode(String note) {
        WebUtils.openPageAndWaitForLoad(wd, taskEditorView);
        ViewRunTimeModePage runTime = new ViewRunTimeModePage(wd);
        runTime.waitForPageLoaded();
        runTime.waitForComponentPresent(activityFeedId);
        return runTime.getActivityFeed().postNote(note);
    }
}
