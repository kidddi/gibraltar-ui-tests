package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.items.component.Component;
import com.bmc.gibraltar.automation.items.parameter.DataType;
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

public class RecordFieldsInTaskViewTest extends AppManagerBaseTest {
    private ViewDefinitionEditorPage editorPage;
    private ViewDesignerInspectorTabs componentInfo;

    @BeforeClass
    protected void goToView() {
        log.info("BeforeClass start");
        editorPage = appManager.goInitiallyToView("Edit Task");
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

    @Test(enabled = false, description = "Need to update code to support a new functionality of adding record fields.")
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265")
    public void addingRecordFieldsToView() {
        List<String> fieldsInInspector = componentInfo.getListOfParameters(InspectorGroup.VISIBLE_FIELDS);
        List<String> fieldsOnCanvas = editorPage.getFieldsOnCanvas(Component.RECORD_INSTANCE_EDITOR);
        String newVisibleField = getFieldToBeAddedAndExcludeExistingFields(fieldsOnCanvas);
        log.info("Adding a new field: " + newVisibleField);

        // TODO: drag-n-drop 'Text Field' component from Palette, and set a field name
        // get parameters count in Inspector after adding a field
        int parametersCountInInspector = componentInfo.getParametersCount(InspectorGroup.VISIBLE_FIELDS);
        assertThat("Total count of parameters in the Inspector is wrong.", parametersCountInInspector,
                equalTo(fieldsInInspector.size() + 1));
        assertThat("Total count of parameters on the Canvas is wrong.", parametersCountInInspector,
                equalTo(fieldsOnCanvas.size() + 1));
        // updating the list of parameters both in Inspector and Canvas
        fieldsInInspector = componentInfo.getListOfParameters(InspectorGroup.VISIBLE_FIELDS);
        fieldsOnCanvas = editorPage.getFieldsOnCanvas(Component.RECORD_INSTANCE_EDITOR);
        assertThat(String.format("%s isn't displayed in the Inspector.", newVisibleField), fieldsInInspector,
                hasItem(newVisibleField));
        assertThat(String.format("%s isn't displayed on the Canvas.", newVisibleField), fieldsOnCanvas,
                hasItem(newVisibleField));
    }

    private String getFieldToBeAddedAndExcludeExistingFields(List<String> excludedFields) {
        RestDataProvider data = new RestDataProvider();
        String recordName = componentInfo.getPropertyValue(InspectorGroup.PROPERTIES, "Record Definition Name:");
        List<String> recordFields = data.getListOfFieldsByType(recordName, DataType.TEXT);
        recordFields.removeAll(excludedFields);
        String newVisibleField = excludedFields.get(0);
        if (!recordFields.isEmpty()) {
            newVisibleField = recordFields.get(0);
        }
        return newVisibleField;
    }

    @Test
    @Features("[P530] Process owner tailors Record Instance Editor")
    @Stories("US207265")
    public void removingRecordFieldsFromCanvas() {
        List<String> fieldsOnCanvas = editorPage.getFieldsOnCanvas(Component.RECORD_INSTANCE_EDITOR);
        String fieldToRemove = fieldsOnCanvas.get(0);
        log.info("Field [" + fieldToRemove + "] is chosen to be removed.");
        editorPage.removeFieldFromCanvas(fieldToRemove);
        List<String> fieldsOnCanvasAfterRemoval = editorPage.getFieldsOnCanvas(Component.RECORD_INSTANCE_EDITOR);
        assertThat("Number of fields on canvas is wrong", fieldsOnCanvasAfterRemoval.size(),
                equalTo(fieldsOnCanvas.size() - 1));
        assertThat(String.format("%s is displayed on the canvas", fieldToRemove), fieldsOnCanvasAfterRemoval,
                not(hasItem(fieldToRemove)));

        editorPage.selectComponentOnCanvas(Component.RECORD_INSTANCE_EDITOR);
        List<String> fieldsInInspectorAfterRemoval = componentInfo.getListOfParameters(InspectorGroup.VISIBLE_FIELDS);
        assertThat(String.format("%s is displayed in the Inspector", fieldToRemove), fieldsInInspectorAfterRemoval,
                not(hasItem(fieldToRemove)));
        assertThat("Order of parameters on canvas and inspector isn't equal.", fieldsInInspectorAfterRemoval,
                equalTo(fieldsOnCanvasAfterRemoval));
    }

    // TODO: save, reopen view and verify count of the fields, then return the view to default state
}