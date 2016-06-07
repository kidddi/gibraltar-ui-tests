package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.items.component.Component;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ViewDesignerInspectorTabs;
import com.bmc.gibraltar.automation.pages.ViewDefinitionEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RecordInsEditorComponentTest extends AppManagerBaseTest {
    private ViewDefinitionEditorPage editorPage;
    private ViewDesignerInspectorTabs componentInfo;
    private String[] expectedDefaultFields = {"Priority", "Task Name", "Status", "Notes", "Due Date", "Assigned To"};

    @BeforeClass
    protected void goToView() {
        log.info("BeforeClass start");
        editorPage = appManager.goInitiallyToView("Edit Task");
        editorPage.selectComponentOnCanvas(Component.ACTIVITY_FEED);
        editorPage.selectComponentOnCanvas(Component.RECORD_INSTANCE_EDITOR);
        componentInfo = editorPage.toInspectorTab(ViewDesignerInspectorTabs.Tab.COMPONENT_INFORMATION);
        log.info("BeforeClass end");
    }

    @AfterClass
    protected void closeDesigner() {
        log.info("AfterClass start");
        editorPage.close();
        appManager.navigateToPage();
        log.info("AfterClass end");
    }

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265, US207665")
    public void groupsOfRecordInstanceComponentInTaskEditorView() {
        List<InspectorGroup> inspectorGroups = componentInfo.getInspectorGroupsList();
        assertThat("Component Information tab in the Inspector doesn't contain all expected groups", inspectorGroups,
                hasItems(InspectorGroup.PROPERTIES, InspectorGroup.VISIBLE_FIELDS));
    }

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265, US207665")
    public void propertiesOfRecordInstanceComponentInTaskEditorView() {
        List<String> properties = componentInfo.getPropertiesList(InspectorGroup.PROPERTIES);
        assertThat("Properties group in the Component Information tab in the Inspector " +
                        "doesn't contain all expected properties", properties,
                hasItems("Record Definition Name:", "Record Instance Id:"));
    }

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265, US207665")
    public void defaultFieldsInInspectorInTaskEditorView() {
        List<String> fieldsInInspector = componentInfo.getListOfParameters(InspectorGroup.VISIBLE_FIELDS);
        assertThat("Expected default fields are not visible in Inspector.", fieldsInInspector,
                hasItems(expectedDefaultFields));
    }

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265, US207665")
    public void defaultFieldsOnCanvasInTaskEditorView() {
        List<String> fieldsOnCanvas = editorPage.getFieldsOnCanvas(Component.RECORD_INSTANCE_EDITOR);
        assertThat("Canvas doesn't contain expected default fields.", fieldsOnCanvas,
                hasItems(expectedDefaultFields));
    }

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265, US207665")
    public void defaultFieldsInInspectorAndCanvasAreSameInTaskEditorView() {
        List<String> fieldsOnCanvas = editorPage.getFieldsOnCanvas(Component.RECORD_INSTANCE_EDITOR);
        List<String> fieldsInInspector = componentInfo.getListOfParameters(InspectorGroup.VISIBLE_FIELDS);
        assertThat("Number of fields on Canvas are not the same as in Inspector", fieldsOnCanvas,
                hasSize(fieldsInInspector.size()));
        assertThat("Fields in Inspector are not the same as on Canvas", fieldsOnCanvas,
                equalTo(fieldsInInspector));
    }

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265, US207665")
    public void defaultComponentsInTaskEditorView() {
        List<Component> components = editorPage.getComponentsOnCanvas();
        Component[] expectedDefaultPresentComponents = {
                Component.RECORD_INSTANCE_EDITOR, Component.ATTACHMENTS, Component.ACTIVITY_FEED};
        assertThat("Not all expected components are present on the Canvas", components,
                hasItems(expectedDefaultPresentComponents));
    }

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265, US207665")
    public void recordInstanceHeaderInTaskEditorView() {
        String componentHeader = editorPage.getComponentHeaderOnCanvas(Component.RECORD_INSTANCE_EDITOR);
        assertThat("Component header is not equal to Task.", componentHeader, equalTo("Task"));
    }
}