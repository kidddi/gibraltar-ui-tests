package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.items.component.Component;
import com.bmc.gibraltar.automation.items.record.FormField;
import com.bmc.gibraltar.automation.items.tab.ValidationIssuesTab;
import com.bmc.gibraltar.automation.pages.ViewDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ViewDefinitionsPage;
import com.bmc.gibraltar.automation.pages.WarningDialog;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ViewDefinitionsTest extends AppManagerBaseTest {
    private ViewDefinitionsPage viewDefinitionsPage;
    private ViewDefinitionEditorPage viewDefinitionEditorPage;

    @BeforeMethod
    protected void openViewDefinitionEditorPage() {
        viewDefinitionsPage = new ViewDefinitionsPage(wd).navigateToPage();
        viewDefinitionEditorPage = viewDefinitionsPage.initiateNewView();
    }

    @Test
    @Features("P530 Process owner tailors Record Instance Editor")
    @Stories("US206665")
    @TestCaseId("TC932759")
    public void verifyRecordInstanceEditorAdditionToViewDefinition() {
        String viewName = generateViewDefinitionName();
        viewDefinitionEditorPage.dragAndDropComponentToCanvas(Component.RECORD_INSTANCE_EDITOR);
        setViewAndRecordDefinition(viewName);
        String recordDefinitionName = viewDefinitionEditorPage.getAvailableRecordDefinitionNames().get(1);
        viewDefinitionEditorPage.saveViewDefinition()
                .close();
        viewDefinitionsPage.refresh();
        viewDefinitionsPage.openView(viewName);
        List<String> recordName = viewDefinitionEditorPage.getComponentNames();
        assertThat("Wrong Record Definition Name", recordName, hasItem(recordDefinitionName));
    }

    @Test
    @Features("P530 Process owner tailors Record Instance Editor")
    @Stories("US206665")
    @TestCaseId("TC932759")
    public void verifyNameChangeOfViewDefinition() {
        viewDefinitionEditorPage.dragAndDropComponentToCanvas(Component.RECORD_INSTANCE_EDITOR)
                .dragAndDropComponentToCanvas(Component.RECORD_INSTANCE_EDITOR);
        viewDefinitionEditorPage.setViewDefinitionName(generateViewDefinitionName());
        String recordDefinitionName = viewDefinitionEditorPage.getAvailableRecordDefinitionNames().get(1);
        String newRecordDefinitionName = viewDefinitionEditorPage.getAvailableRecordDefinitionNames().get(2);
        viewDefinitionEditorPage.goToComponentInfoTab()
                .setRecordDefinitionName(recordDefinitionName)
                .setRecordDefinitionName(newRecordDefinitionName);
        new WarningDialog<>(wd, viewDefinitionEditorPage).clickCancelButton();
        assertThat("Wrong Record Definition Name",
                viewDefinitionEditorPage.getComponentNames(), hasItem(recordDefinitionName));
        viewDefinitionEditorPage.goToComponentInfoTab()
                .setRecordDefinitionName(newRecordDefinitionName);
        new WarningDialog<>(wd, viewDefinitionEditorPage).clickOkButton();
        assertThat("Wrong Record Definition Name",
                viewDefinitionEditorPage.getComponentNames(), hasItem(newRecordDefinitionName));
        viewDefinitionEditorPage.clickUndo();
        assertThat("Wrong Record Definition Name",
                viewDefinitionEditorPage.getComponentNames(), hasItem(recordDefinitionName));
        viewDefinitionEditorPage.clickRedo();
        assertThat("Wrong Record Definition Name",
                viewDefinitionEditorPage.getComponentNames(), hasItem(newRecordDefinitionName));
    }

    @Test
    @Features("P530 Process owner tailors Record Instance Editor")
    @Stories("US206665")
    @TestCaseId("TC932784")
    public void checkRecordInstanceEditorCustomizableFields() {
        viewDefinitionEditorPage.dragAndDropComponentToCanvas(Component.RECORD_INSTANCE_EDITOR);
        setViewAndRecordDefinition(generateViewDefinitionName());
        viewDefinitionEditorPage.dragAndDropFieldsToSelectedEditor(FormField.DROPDOWN, FormField.TEXT_AREA)
                .removeFieldFromRecordEditor(1);
        int numberOfFields = viewDefinitionEditorPage.getNumberOfFields();
        assertEquals(1, numberOfFields, "Wrong number of fields");
    }

    @Test
    @Features("P530 Process owner tailors Record Instance Editor")
    @Stories("US206665")
    @Issue("SW00502868 View Designer: Record Instance Editor: 'Correct' links are broken")
    @TestCaseId("TC940400")
    public void checkValidationIssuesInViewDefinition() {
        viewDefinitionEditorPage.dragAndDropComponentToCanvas(Component.RECORD_INSTANCE_EDITOR);
        String recordDefinitionName = viewDefinitionEditorPage.getAvailableRecordDefinitionNames().get(1);

        ValidationIssuesTab validationIssuesTab = viewDefinitionEditorPage.saveViewDefinition()
                .goToValidationIssuesTab();
        assertTrue(validationIssuesTab.isValidationTabContainErrors(), "Validation tab does not contain issues");
        validationIssuesTab.verifyIssueExists("Record Definition Name cannot be blank.")
                .clickCorrectLink();
        viewDefinitionEditorPage.setRecordDefinitionName(recordDefinitionName)
                .setRecordInstanceId("1")
                .setViewDefinitionName(generateViewDefinitionName())
                .saveViewDefinition();
        validationIssuesTab.verifyValidationTabHasNoErrors();
    }

    @Test
    @Features("P530 Process owner tailors Record Instance Editor")
    @Stories("US206665")
    @TestCaseId("TC932784")
    public void checkFieldsMovingInRecordEditor() {
        String viewName = generateViewDefinitionName();
        viewDefinitionEditorPage.dragAndDropComponentToCanvas(Component.RECORD_INSTANCE_EDITOR);
        setViewAndRecordDefinition(viewName);
        viewDefinitionEditorPage.dragAndDropFieldsToSelectedEditor(FormField.INTEGER_FIELD, FormField.DROPDOWN);
        viewDefinitionEditorPage.moveField(1, 0)
                .saveViewDefinition()
                .close();
        viewDefinitionsPage.refresh();
        viewDefinitionsPage.openView(viewName);
        String firstField = viewDefinitionEditorPage.getFieldName(0);
        assertThat("First field should have another name", firstField, equalTo(FormField.INTEGER_FIELD.getName()));
    }

    @Test
    @Features("P530 Process owner tailors Record Instance Editor")
    @Stories("US206665")
    @TestCaseId("TC932890")
    public void checkMultipleRecordInstances() {
        String viewName = generateViewDefinitionName();
        viewDefinitionEditorPage.setViewDefinitionName(viewName);
        IntStream.range(0, 2).forEach(i -> {
            viewDefinitionEditorPage.dragAndDropComponentToCanvas(Component.RECORD_INSTANCE_EDITOR);
            String recordDefinitionName = viewDefinitionEditorPage.getAvailableRecordDefinitionNames().get(i + 1);
            viewDefinitionEditorPage.goToComponentInfoTab()
                    .setRecordDefinitionName(recordDefinitionName)
                    .setRecordInstanceId("1")
                    .dragAndDropFieldsToSelectedEditor(i, FormField.INTEGER_FIELD, FormField.DROPDOWN);
        });
        viewDefinitionEditorPage.moveField(1, 0);
        viewDefinitionEditorPage.removeFieldFromRecordEditor(1, 1)
                .saveViewDefinition()
                .close();
        viewDefinitionsPage.refresh();
        viewDefinitionsPage.openView(viewName);
        String firstField = viewDefinitionEditorPage.getFieldName(0);
        assertEquals(firstField, FormField.INTEGER_FIELD.getName(), "First field should have another name");
        assertEquals(viewDefinitionEditorPage.getNumberOfFields(1), 1, "Number of fields is wrong");
    }

    @Test
    @Features("P530 Process owner tailors Record Instance Editor")
    @Stories("US206665")
    @Issue("SW00501807 Fields are not removed when Record Definition name is changed")
    @TestCaseId("TC940177")
    public void checkFieldsChangeAfterRecordDefinitionChange() {
        viewDefinitionEditorPage.dragAndDropComponentToCanvas(Component.RECORD_INSTANCE_EDITOR);
        setViewAndRecordDefinition(generateViewDefinitionName());
        viewDefinitionEditorPage.dragAndDropFieldsToSelectedEditor(FormField.DATE_FIELD, FormField.DROPDOWN)
                .saveViewDefinition();
        assertEquals(viewDefinitionEditorPage.getNumberOfFields(), 2, "Number of fields is not 2");
        String recordDefinitionName = viewDefinitionEditorPage.selectFirstRecordEditorOnCanvas()
                .getAvailableRecordDefinitionNames().get(2);
        viewDefinitionEditorPage.goToComponentInfoTab()
                .setRecordDefinitionName(recordDefinitionName);
        WarningDialog warningDialog = new WarningDialog<>(wd, viewDefinitionEditorPage);
        warningDialog.clickOkButton();
        assertEquals(viewDefinitionEditorPage.getNumberOfFields(), 0, "Number of fields is not zero");
    }

    @Test
    @Features("P530 Process owner tailors Record Instance Editor")
    @Stories("US206665")
    @Issue("SW00502201: Record Instance Editor cannot be removed")
    @TestCaseId("TC932890")
    public void checkRemovalOfRecordEditor() {
        viewDefinitionEditorPage.dragAndDropComponentToCanvas(Component.RECORD_INSTANCE_EDITOR);
        setViewAndRecordDefinition(generateViewDefinitionName());
        viewDefinitionEditorPage.dragAndDropFieldsToSelectedEditor(FormField.DATE_FIELD, FormField.DROPDOWN)
                .saveViewDefinition()
                .removeFirstRecordEditor()
                .saveViewDefinition();
        assertEquals(viewDefinitionEditorPage.getNumberOfComponentsOnCanvas(), 0, "Number of present editors is wrong");
    }

    private void setViewAndRecordDefinition(String viewName) {
        viewDefinitionEditorPage.setViewDefinitionName(viewName);
        String recordDefinitionName = viewDefinitionEditorPage.getAvailableRecordDefinitionNames().get(1);
        viewDefinitionEditorPage.goToComponentInfoTab()
                .setRecordDefinitionName(recordDefinitionName)
                .setRecordInstanceId("1");
    }

    private String generateViewDefinitionName() {
        return "test_" + RandomStringUtils.randomAlphabetic(10);
    }
}