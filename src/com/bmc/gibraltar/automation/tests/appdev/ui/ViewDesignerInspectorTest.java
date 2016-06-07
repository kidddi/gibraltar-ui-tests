package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.items.component.Component;
import com.bmc.gibraltar.automation.items.record.FormField;
import com.bmc.gibraltar.automation.pages.ViewDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ViewDefinitionsPage;
import com.bmc.gibraltar.automation.pages.ViewRunTimeModePage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Issues;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.stream.Stream;

import static com.bmc.gibraltar.automation.items.component.CheckboxExpressions.AT_ALL_TIMES;
import static com.bmc.gibraltar.automation.items.component.CheckboxExpressions.WHEN_CONDITION_IS_TRUE;
import static java.lang.String.format;

public class ViewDesignerInspectorTest extends AppManagerBaseTest {
    private ViewDefinitionEditorPage editorPage;

    @BeforeMethod
    private void commonPreconditions() {
        ViewDefinitionsPage viewDefinitionsPage = new ViewDefinitionsPage(wd, "task-manager").navigateToPage();
        editorPage = viewDefinitionsPage.initiateNewView();
        editorPage.dragAndDropComponentToCanvas(Component.RECORD_INSTANCE_EDITOR);
    }

    @Test
    @Features("[P528] Ajay Tailors UI of App/Library In View Designer")
    @Stories("US209637")
    public void newDisabledAndHiddenPropertiesViewAndDefaultState() {
        Stream.of(FormField.values()).forEach(formField -> {
            editorPage.dragAndDropFieldsToSelectedEditor(formField);
            verifyFalse(editorPage.getDisabledCheckboxState(), "Checkbox 'Disabled' is Enabled");
            verifyFalse(editorPage.getHiddenCheckboxState(), "Checkbox 'Hidden' is Enabled");
        });
    }

    @Test
    @Features("[P528] Ajay Tailors UI of App/Library In View Designer")
    @Stories("US209637")
    public void optionsForDisabledProperty() {
        editorPage.dragAndDropFieldsToSelectedEditor(FormField.TEXT_FIELD)
                .setDisabledCheckbox(true);
        verifyTrue(editorPage.getDisabledCheckboxState(), "Checkbox 'Disabled' is Disabled");
        verifyTrue(editorPage.getDisabledExpression() == AT_ALL_TIMES, "'Disabled' expression is not AT_ALL_TIMES");
        editorPage.setDisabledCheckbox(true)
                .setDisabledExpression(WHEN_CONDITION_IS_TRUE)
                .setDisabledCondition("true");
        verifyTrue(editorPage.getDisabledCheckboxState(), "Checkbox 'Disabled' is Disabled");
        verifyTrue(editorPage.getDisabledExpression() == WHEN_CONDITION_IS_TRUE,
                "'Disabled' expression is not WHEN_CONDITION_IS_TRUE");
        verifyEquals(editorPage.getDisabledCondition(), "true", "'Disabled' condition is not equals 'true'");
    }

    @Test
    @Features("[P528] Ajay Tailors UI of App/Library In View Designer")
    @Stories("US209637")
    public void optionsForHiddenProperty() {
        editorPage.dragAndDropFieldsToSelectedEditor(FormField.TEXT_FIELD)
                .setHiddenCheckbox(true);
        verifyTrue(editorPage.getHiddenCheckboxState(), "Checkbox 'Hidden' is Disabled");
        verifyTrue(editorPage.getHiddenExpression() == AT_ALL_TIMES, "'Hidden' expression is not AT_ALL_TIMES");
        editorPage.setHiddenCheckbox(true)
                .setHiddenExpression(WHEN_CONDITION_IS_TRUE)
                .setHiddenCondition("false");
        verifyTrue(editorPage.getHiddenCheckboxState(), "Checkbox 'Hidden' is Disabled");
        verifyTrue(editorPage.getHiddenExpression() == WHEN_CONDITION_IS_TRUE,
                "'Hidden' expression is not WHEN_CONDITION_IS_TRUE");
        verifyEquals(editorPage.getHiddenCondition(), "false", "'Hidden' condition is not equals 'false'");
    }

    @Test
    @Features("[P528] Ajay Tailors UI of App/Library In View Designer")
    @Issues({@Issue("SW00503909"), @Issue("SW00502109")})
    @Stories("US209637")
    public void runTimeFieldsAreConfiguredWithTheDisabledAndHiddenPropertiesBasedOnTheEvaluatedExpression() {
        String viewDefinitionName = "DisabledAndHiddenProperties" + RandomStringUtils.randomAlphabetic(4);
        String recordDefinitionName = "Connection";
        String instanceId = new RestDataProvider().getInstanceIds(recordDefinitionName).stream().findAny().get();
        editorPage.setViewDefinitionName(viewDefinitionName)
                .goToComponentInfoTab()
                .setRecordDefinitionName(recordDefinitionName)
                .setRecordInstanceId(instanceId);
        FormField formField = FormField.TEXT_FIELD;
        String fieldName = "Notes";
        editorPage.dragAndDropFieldsToSelectedEditor(formField).setRecordDefinitionFieldName(fieldName);
        editorPage.setHiddenCheckbox(false).setDisabledCheckbox(false);
        editorPage.dragAndDropFieldsToSelectedEditor(formField).setRecordDefinitionFieldName(fieldName);
        editorPage.setHiddenCheckbox(true).setDisabledCheckbox(true);
        editorPage.dragAndDropFieldsToSelectedEditor(formField).setRecordDefinitionFieldName(fieldName);
        editorPage.setHiddenCheckbox(true).setHiddenExpression(WHEN_CONDITION_IS_TRUE)
                .setHiddenCondition("1").setDisabledCheckbox(true).setDisabledExpression(WHEN_CONDITION_IS_TRUE)
                .setDisabledCondition("1");
        editorPage.dragAndDropFieldsToSelectedEditor(formField).setRecordDefinitionFieldName(fieldName);
        editorPage.setHiddenCheckbox(true).setHiddenExpression(WHEN_CONDITION_IS_TRUE)
                .setHiddenCondition("0").setDisabledCheckbox(true).setDisabledExpression(WHEN_CONDITION_IS_TRUE)
                .setDisabledCondition("0");
        editorPage.clickSave();
        ViewRunTimeModePage preview = editorPage.openPreviewPage();
        verifyFalse(preview.isFieldDisabled(fieldName, 1), format("Field %s (number %s) is Disabled", fieldName, 1));
        verifyFalse(preview.isFieldHidden(fieldName, 1), format("Field %s (number %s) is Hidden", fieldName, 1));

        verifyTrue(preview.isFieldDisabled(fieldName, 2), format("Field %s (number %s) is not Disabled", fieldName, 2));
        verifyTrue(preview.isFieldHidden(fieldName, 2), format("Field %s (number %s) is not Hidden", fieldName, 2));

        verifyTrue(preview.isFieldDisabled(fieldName, 3), format("Field %s (number %s) is not Disabled", fieldName, 3));
        verifyTrue(preview.isFieldHidden(fieldName, 3), format("Field %s (number %s) is not Hidden", fieldName, 3));

        verifyFalse(preview.isFieldDisabled(fieldName, 4), format("Field %s (number %s) is Disabled", fieldName, 4));
        verifyFalse(preview.isFieldHidden(fieldName, 4), format("Field %s (number %s) is Hidden", fieldName, 4));
    }
}