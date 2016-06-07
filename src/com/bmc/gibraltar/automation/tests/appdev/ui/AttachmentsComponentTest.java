package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.utils.FileUtils;
import com.bmc.gibraltar.automation.framework.utils.PropertiesUtils;
import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import com.bmc.gibraltar.automation.items.component.AttachmentsPanel;
import com.bmc.gibraltar.automation.pages.TaskDetailsPage;
import com.bmc.gibraltar.automation.pages.ViewRunTimeModePage;
import com.bmc.gibraltar.automation.tests.TaskManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.allure.annotations.Stories;

import java.io.File;
import java.util.List;

import static com.jayway.restassured.path.json.JsonPath.from;

public class AttachmentsComponentTest extends TaskManagerBaseTest {
    private RestDataProvider dataProvider;
    private String taskGuid;
    private String componentId;
    private String taskDetailsUrlInTaskManager;
    private AttachmentsPanel attachmentsPanel;
    private List<String> fileNamesInOldView;
    private List<String> filesNamesInNewView;
    private String taskEditorViewUrl;
    private File imageFile;
    private File pdfFile;

    //TODO review this test
    private String appServer;

    @BeforeClass
    protected void prepareData() {
        log.info("BeforeClass start");
        appServer = PropertiesUtils.getAppServerUrl();
        dataProvider = new RestDataProvider();
        String taskJson = new CommonSteps(dataProvider).createTask();
        taskGuid = from(taskJson).get("fieldInstances.179.value");
        String taskEditorViewDefinitionId = dataProvider.getViewDefinitionId("Edit Task");
        componentId = dataProvider.getComponentDefinitionId(taskEditorViewDefinitionId, "rx-attachment-panel");
        //TODO: old view is not present in the Task Manager anymore, test needs to be updated
        taskDetailsUrlInTaskManager = appServer + "/tms/task/" + taskGuid;
        WebUtils.openPageAndWaitForLoad(wd, taskDetailsUrlInTaskManager);
        TaskDetailsPage detailsPage = new TaskDetailsPage(wd);
        detailsPage.waitForPageLoaded();

        // adding an attachment in the old task view
        // to open the old task view, click on the task ID
        imageFile = FileUtils.generateRandomFile(RandomStringUtils.randomAlphanumeric(10) + ".jpg", 100);
        attachmentsPanel = detailsPage.getAttachmentsPanel();
        attachmentsPanel.addAttachment(imageFile);
        fileNamesInOldView = attachmentsPanel.getAttachmentNames();

        // adding an attachment in the new task view in the Task Manager
        // to open the new task view, click on the task name
        taskEditorViewUrl = appServer + "/view/" + taskEditorViewDefinitionId + "?param=" + taskGuid;
        pdfFile = FileUtils.generateRandomFile(RandomStringUtils.randomAlphanumeric(10) + ".pdf", 200);
        attachmentsPanel = uploadFileInRuntimeMode(pdfFile);
        filesNamesInNewView = attachmentsPanel.getAttachmentNames();
        log.info("BeforeClass end");
    }

    @Test
    @Features("[P519] Developer uses the SDK")
    @Stories("US207665: The reference application includes a configurable Task view")
    public void attachmentsInTaskManager() {
        verifyTrue(filesNamesInNewView.size() == fileNamesInOldView.size() + 1);
        verifyTrue(filesNamesInNewView.containsAll(fileNamesInOldView),
                "Attachments present in the Task Manager old view and new views are not the same. " +
                        ". Attachments present in the new task view are: " + filesNamesInNewView);
    }

    @Test
    @Features("[P519] Developer uses the SDK")
    @Stories("US207665: The reference application includes a configurable Task view")
    public void addingAttachmentsInApplicationManager() {
        taskEditorViewUrl = taskEditorViewUrl.replace("task-manager", "application-manager");
        File docFile = FileUtils.generateRandomFile(RandomStringUtils.randomAlphanumeric(10) + ".doc", 300);
        attachmentsPanel = uploadFileInRuntimeMode(docFile);
        List<String> attachmentsInAppManager = attachmentsPanel.getAttachmentNames();
        verifyTrue(attachmentsInAppManager.size() == filesNamesInNewView.size() + 1);
        verifyTrue(attachmentsInAppManager.containsAll(filesNamesInNewView),
                "The Application Manager does not contain a file posted via the Task Manager." +
                        ". File present in the Application Manager are: " + attachmentsInAppManager);

        // return back to the Task Manager to ensure the uploaded file is present
        WebUtils.openPageAndWaitForLoad(wd, taskDetailsUrlInTaskManager);
        List<String> files = attachmentsPanel.getAttachmentNames();
        verifyTrue(files.containsAll(attachmentsInAppManager),
                "The Task Manager does not contain a file posted via the Application Manager." +
                        ". Files present in the Task Manager are: " + files);
        File downloadedDocFile = dataProvider.getTaskAttachment(taskGuid, docFile.getName());
        compareFilesHashSum(docFile, dataProvider.getTaskAttachment(taskGuid, docFile.getName()));
        downloadedDocFile.delete();
    }

    @Test
    public void checkFilesHashSums() {
        File downloadedImgFile = dataProvider.getTaskAttachment(taskGuid, imageFile.getName());
        File downloadedPdfFile = dataProvider.getTaskAttachment(taskGuid, pdfFile.getName());
        compareFilesHashSum(imageFile, downloadedImgFile);
        compareFilesHashSum(pdfFile, downloadedPdfFile);
        downloadedImgFile.delete();
        downloadedPdfFile.delete();
    }

    @Step
    private AttachmentsPanel uploadFileInRuntimeMode(File file) {
        WebUtils.openPageAndWaitForLoad(wd, taskEditorViewUrl);
        ViewRunTimeModePage runTime = new ViewRunTimeModePage(wd);
        runTime.waitForPageLoaded();
        runTime.waitForComponentPresent(componentId);
        return runTime.getAttachmentsPanel().addAttachment(file);
    }

    @Step
    private void compareFilesHashSum(File f1, File f2) {
        log.info("Comparing the hash sum of files.");
        verifyEquals(FileUtils.getFileHashSum(f1.getAbsolutePath()), FileUtils.getFileHashSum(f2.getAbsolutePath()));
        log.info("End of the comparison.");
    }
}
