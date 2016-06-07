package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.dataprovider.CustomFieldData;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.datadictionary.DataDictionary;
import com.bmc.gibraltar.automation.items.datadictionary.DictionaryGroup;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.parameter.ProcessParameter;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import static com.bmc.gibraltar.automation.dataprovider.CustomFieldData.CustomField;
import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.USER_TASK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class CustomFieldDefinitionTest extends AppManagerBaseTest {
    private ProcessDefinitionEditorPage editView;
    private ProcessDefinitionsTabPage tabPage;
    private ActiveElement userTask;

    @BeforeClass
    public void getUserTask() {
        tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = tabPage.initiateNewProcess();
        userTask = editView.dragNDropByCoordinates(USER_TASK, "330", "370");
        userTask.setProperty(ElementProperties.RECORD_DEFINITION, "Task");
    }

    @AfterClass
    public void navigateToTab() {
        tabPage.navigateToPage();
    }

    @Test(dataProvider = "customFields", dataProviderClass = CustomFieldData.class, groups = Groups.CATEGORY_FULL, description = "expressionsForCustomField")
    @Features("Data Dictionary")
    @Stories("US205911")
    @GUID("f04290bb-1674-4182-9b92-221df103992a")
    public void expressionsForCustomField(ProcessParameter processInput) {
        testDescription = "Verifies an expression in the dictionary for the: " + processInput.getName()
                + " with the data type: " + processInput.getDataType();
        ProcessPropertiesTab processPropertiesTab = editView.getProcessPropertiesTab();
        processInput.setName(processInput.getName() + RandomStringUtils.randomAlphanumeric(5));
        processPropertiesTab.addProcessParameter(InspectorGroup.INPUT_PARAMETERS, processInput);
        ElementProperties[] properties = {ElementProperties.ASSIGNED_TO, ElementProperties.COMPLETION_CRITERIA};
        String expectedExpression = String.format("${process.%s}", processInput.getName());
        for (ElementProperties property : properties) {
            userTask.setProperty(property, "");
            DataDictionary data = userTask.openDataDictionaryFor(property);
            data.doubleClickInTreeOnVar(DictionaryGroup.PROCESS_VARIABLES, new String[]{processInput.getName()}).apply();
            checkResult(userTask, property, expectedExpression);
        }
    }

    @Test(dataProvider = "customOptions", dataProviderClass = CustomFieldData.class, groups = Groups.CATEGORY_FULL, description = "expressionsForCustomFieldPropertiesInConditionEditor")
    @Features("Data Dictionary")
    @Stories("US205911")
    @GUID("8f7ea4b6-61a0-4308-8a3c-a92533e61959")
    public void expressionsForCustomFieldOptions(ProcessParameter processInput, CustomField customsField) {
        testDescription = "Verifies an expression in the dictionary for the: " + processInput.getName()
                + " with the data type: " + processInput.getDataType() + " and his sub-options.";
        ProcessPropertiesTab processPropertiesTab = editView.getProcessPropertiesTab();
        processPropertiesTab.addProcessParameter(InspectorGroup.INPUT_PARAMETERS, processInput);
        ElementProperties[] properties = {ElementProperties.ASSIGNED_TO, ElementProperties.COMPLETION_CRITERIA};
        for (ElementProperties property : properties) {
            userTask.setProperty(property, "");
            DataDictionary data = userTask.openDataDictionaryFor(property);
            data.doubleClickInTreeOnVar(DictionaryGroup.PROCESS_VARIABLES,
                    new String[]{processInput.getName(), customsField.getPropertyNameInDictionary()}).apply();
            checkResult(processInput, userTask, property, customsField);
        }
    }

    private void checkResult(ActiveElement element, ElementProperties property, String expectedExpression) {
        String actualExpression = element.getPropertyValue(property);
        assertTrue(actualExpression.contains(expectedExpression),
                property.getName() + " does not contain expression: " + expectedExpression);
    }

    private void checkResult(ProcessParameter param, ActiveElement element, ElementProperties property, CustomField field) {
        String actualExpression = element.getPropertyValue(property);
        String expectedExpression = String.format(field.getExpectedExpression(), param.getName());
        assertThat(property.getName() + " does not contain expression: " + expectedExpression,
                actualExpression, containsString(expectedExpression));
    }
}