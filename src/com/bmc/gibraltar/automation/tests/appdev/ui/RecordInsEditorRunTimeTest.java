package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.items.component.Component;
import com.bmc.gibraltar.automation.items.component.runtime.RecordEditor;
import com.bmc.gibraltar.automation.items.record.RecordField;
import com.bmc.gibraltar.automation.items.tab.ViewDesignerInspectorTabs;
import com.bmc.gibraltar.automation.pages.ViewDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ViewRunTimeModePage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.tab.InspectorGroup.VISIBLE_FIELDS;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.apache.commons.collections.ListUtils.isEqualList;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecordInsEditorRunTimeTest extends AppManagerBaseTest {
    private RestDataProvider dataProvider = new RestDataProvider();
    private ViewDefinitionEditorPage editorPage;
    private List<String> fieldsInInspector;
    private List<Component> componentsOnCanvas;
    private ViewRunTimeModePage runTimeModePage;
    private RecordEditor recordInstanceInRunTime;
    private List<String> fieldsInRuntime;

    @BeforeClass
    public void goToRunTime() {
        appManager.navigateToPage();
        editorPage = appManager.goInitiallyToView("Edit Task");
        String taskJson = new CommonSteps(dataProvider).createTask();
        String taskGuid = from(taskJson).get("fieldInstances.179.value");
        String taskEditorViewDefinitionId = dataProvider.getViewDefinitionId("Edit Task");
        String taskEditorId = dataProvider.getComponentDefinitionId(
                taskEditorViewDefinitionId, "rx-record-instance-editor");

        editorPage.selectComponentOnCanvas(Component.RECORD_INSTANCE_EDITOR);
        ViewDesignerInspectorTabs componentInfo = editorPage
                .toInspectorTab(ViewDesignerInspectorTabs.Tab.COMPONENT_INFORMATION);
        fieldsInInspector = componentInfo.getListOfParameters(VISIBLE_FIELDS);
        componentsOnCanvas = editorPage.getComponentsOnCanvas();

        runTimeModePage = editorPage.goToRunTimeModeWithParams("Task Id", taskGuid);
        // waiting for a component with specific ID
        // because the component placeholder appears before data inside the placeholder
        runTimeModePage.waitForComponentPresent(taskEditorId);
        recordInstanceInRunTime = runTimeModePage.getRecordEditor();
        fieldsInRuntime = recordInstanceInRunTime.getListOfFields()
                .stream().map(field -> field.replaceAll(":", "")).collect(Collectors.toList());
    }

    @AfterClass
    public void logoutRuntimeMode() {
        editorPage = runTimeModePage.close();
        editorPage.close();
    }

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265")
    public void fieldsInRuntimeModeForValidTaskId() {
        assertThat("Fields in design and in runtime mode are not the same.",
                isEqualList(fieldsInInspector, fieldsInRuntime));
    }

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265")
    public void fieldsMarkedAsRequiredInRuntimeModeForValidTaskId() {
        List<String> requiredFields = dataProvider
                .getRecordFieldsNamesByFieldOption(RecordField.Option.REQUIRED, "Task");
        fieldsInRuntime.stream().filter(requiredFields::contains)
                .forEach(fieldInRunTime -> assertTrue(recordInstanceInRunTime.isFieldRequired(fieldInRunTime)));
    }

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265")
    public void componentsInRuntimeModeForValidTaskId() {
        List<Component> componentsInRuntime = runTimeModePage.getComponents();
        assertThat("Components in design and in runtime mode are not the same.",
                isEqualList(componentsOnCanvas, componentsInRuntime));
    }

    // TODO: add steps to change task properties and verify that these changes were applied
}