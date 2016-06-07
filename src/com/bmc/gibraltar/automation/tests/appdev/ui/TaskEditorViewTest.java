package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.utils.PropertiesUtils;
import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import com.bmc.gibraltar.automation.pages.ViewRunTimeModePage;
import com.bmc.gibraltar.automation.tests.TaskManagerBaseTest;
import com.jayway.restassured.path.json.JsonPath;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

public class TaskEditorViewTest extends TaskManagerBaseTest {
    private String taskGuid;
    private String taskEditorViewDefinitionId;
    private String taskEditorId;
    private String activityFeedComponentId;
    private String attachmentComponentId;

    // TODO review this
    private String appServer;

    @BeforeClass
    public void prepareTestData() {
        log.info("BeforeClass start");
        appServer = PropertiesUtils.getAppServerUrl();
        RestDataProvider dataProvider = new RestDataProvider();
        String createTask = new CommonSteps(dataProvider).createTask();
        taskGuid = JsonPath.from(createTask).get("fieldInstances.179.value");
        taskEditorViewDefinitionId = dataProvider.getViewDefinitionId("Edit Task");
        taskEditorId = dataProvider.getComponentDefinitionId(
                taskEditorViewDefinitionId, "rx-record-instance-editor");
        activityFeedComponentId = dataProvider.getComponentDefinitionId(
                taskEditorViewDefinitionId, "rx-activity-feed");
        attachmentComponentId = dataProvider.getComponentDefinitionId(
                taskEditorViewDefinitionId, "rx-attachment-panel");
        dataProvider.logout();
        log.info("BeforeClass end");
    }

    @Test
    @Features("[P519] Developer uses the SDK")
    @Stories("US207665: The reference application includes a configurable Task view")
    public void taskEditorViewInTaskManager() {
        // Test compares that the "Edit Task" view is same for the Task Manager and the Application Manager
        WebElement containerInTaskManager = getRuntimeContainer(appServer);
        Screenshot viewInTaskManager = new AShot().takeScreenshot(wd, containerInTaskManager);
        String appManagerUrl = appServer.replaceAll("task-manager", "application-manager");
        WebElement containerInAppManager = getRuntimeContainer(appManagerUrl);
        Screenshot viewInAppManager = new AShot().takeScreenshot(wd, containerInAppManager);
        ImageDiff diff = new ImageDiffer().makeDiff(viewInTaskManager, viewInAppManager);
        assertFalse(diff.hasDiff(), "The 'Edit Task' view is different in applications.");
    }

    private WebElement getRuntimeContainer(String baseUrl) {
        String url = baseUrl + "/view/" + taskEditorViewDefinitionId + "?param=" + taskGuid;
        WebUtils.openPageAndWaitForLoad(wd, url);
        ViewRunTimeModePage viewRunTimeModePage = new ViewRunTimeModePage(wd);
        viewRunTimeModePage.waitForComponentsPresent(new String[]{taskEditorViewDefinitionId, taskEditorId,
                activityFeedComponentId, attachmentComponentId});
        return getElement(viewRunTimeModePage.getRuntimeContainer());
    }
}