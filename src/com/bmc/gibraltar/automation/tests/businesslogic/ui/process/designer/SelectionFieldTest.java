package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.datadictionary.DataDictionary;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectionFieldTest extends AppManagerBaseTest {
    private static RestDataProvider restDataProvider = new RestDataProvider();
    private ProcessDefinitionEditorPage processDefinitionEditorPage;

    @DataProvider
    public static Object[][] selectionFields() {
        String customRecordDefinition = "Custom-" + Long.toHexString(System.currentTimeMillis());
        new CommonSteps(restDataProvider)
                .createRecordDefinitionByTemplate(customRecordDefinition, "recordDefinitionWithAllTypes.json");
        List<String> allRecordDefinitions = restDataProvider.getRecordDefinitionNames();
        List<String> recordDefinitions =
                Arrays.asList(customRecordDefinition, "UserMessage", "Task", "ApprovalSignature", "Person")
                        .stream().filter(allRecordDefinitions::contains).collect(Collectors.toList());
        return new Object[][]{
                new Object[]{ElementOfDesigner.USER_TASK, InspectorGroup.PROPERTIES,
                        ElementProperties.RECORD_DEFINITION, recordDefinitions},
                new Object[]{ElementOfDesigner.CREATE_RECORD_INSTANCE, InspectorGroup.INPUT_MAP,
                        ElementProperties.RECORD_DEFINITION_NAME, recordDefinitions},
                new Object[]{ElementOfDesigner.UPDATE_RECORD_INSTANCE, InspectorGroup.INPUT_MAP,
                        ElementProperties.RECORD_DEFINITION_NAME, recordDefinitions}
        };
    }

    @DataProvider
    public static Object[][] elementsWithoutSelectionFields() {
        return new Object[][]{
                new Object[]{ElementOfDesigner.CREATE_TASK,
                        new ElementProperties[]{ElementProperties.STATUS, ElementProperties.PRIORITY}},
                new Object[]{ElementOfDesigner.CREATE_TASK_CUSTOM,
                        new ElementProperties[]{ElementProperties.STATUS, ElementProperties.PRIORITY}},
                new Object[]{ElementOfDesigner.UPDATE_TASK_STATUS,
                        new ElementProperties[]{ElementProperties.STATUS}},
        };
    }

    @Test(dataProvider = "selectionFields", groups = Groups.CATEGORY_FULL, description = "selectionFieldsArePresentInProcessDesigners")
    @Features("Inspector")
    @Stories("US205672")
    @GUID("6feeea2c-9802-4f33-b02b-28827500c82e")
    public void selectionFieldsArePresentInProcessDesigner(ElementOfDesigner element, InspectorGroup group,
                                                           ElementProperties property,
                                                           List<String> recordDefinitions) {
        testDescription = "Verify selectionFieldsArePresentInProcessDesigner: " + "ElementOfDesigner: "
                + element.getName() + ". InspectorGroup: " + group.getGroupName() + ". ElementProperties: "
                + property.getName();
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd)
                .navigateToPage();
        processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        ActiveElement activeElement = processDefinitionEditorPage.dragAndDropToCanvas(element);

        ElementPropertiesTab tab = processDefinitionEditorPage.getElementPropertiesTab();
        for (String recordDefName : recordDefinitions) {
            tab.setPropertyValue(activeElement, group, property, recordDefName);
            processDefinitionEditorPage.confirmModalDialog(true);
            Map<String, List<String>> selectionFieldsWithOptions = restDataProvider
                    .getSelectionFieldsAndOptions(recordDefName);
            for (Map.Entry<String, List<String>> field : selectionFieldsWithOptions.entrySet()) {
                String fieldName = field.getKey();
                List<String> fieldOptions = selectionFieldsWithOptions.get(fieldName);
                ElementPropertiesTab elTab = processDefinitionEditorPage.getElementPropertiesTab();
                List<String> optionsInInspector = elTab
                        .getAvailableOptionsFromDropdown(InspectorGroup.INPUT_MAP, fieldName + ":");
                verifyTrue(fieldOptions.containsAll(optionsInInspector) && optionsInInspector.containsAll(fieldOptions));
                activeElement.openDataDictionaryFor(fieldName + ":")
                        .verifyOptionsPresentInOptionsGroup(fieldOptions)
                        .close();
            }
        }
        processDefinitionsTabPage.navigateToPage();
    }

    @Test(dataProvider = "elementsWithoutSelectionFields", groups = Groups.CATEGORY_FULL, description = "selectionFieldsAreNotPresentForTasksGroup")
    @Features("Inspector")
    @Stories("US205672")
    @GUID("733d1d34-4280-4242-a2c2-1f69872c8642")
    public void selectionFieldsAreNotPresentForTasksGroup(ElementOfDesigner elementOfDesigner,
                                                          ElementProperties[] properties) {
        testDescription = "Verify selectionFieldsAreNotPresentForTasksGroup: " + "ElementOfDesigner: "
                + elementOfDesigner.getName();
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd)
                .navigateToPage();
        processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        ActiveElement elementToBeVerified = processDefinitionEditorPage.dragAndDropToCanvas(elementOfDesigner);
        ElementPropertiesTab elTab = processDefinitionEditorPage.getElementPropertiesTab();
        for (ElementProperties property : properties) {
            elTab.verifyDropDownIsNotPresentForProperty(InspectorGroup.INPUT_MAP, property.getName());
            DataDictionary dataDictionary = elementToBeVerified.openDataDictionaryFor(property);
            List<String> groupsInDataDictionary = dataDictionary.getGroupsPresentInDataDictionary();
            verifyTrue(!groupsInDataDictionary.contains("Options"));
            dataDictionary.close();
        }
        processDefinitionsTabPage.navigateToPage();
    }
}