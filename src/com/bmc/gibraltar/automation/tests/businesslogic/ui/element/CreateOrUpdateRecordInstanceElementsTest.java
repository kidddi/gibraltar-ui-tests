package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.record.RecordDefinition;
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
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.element.ElementProperties.RECORD_DEFINITION_NAME;

public class CreateOrUpdateRecordInstanceElementsTest extends AppManagerBaseTest {

    @DataProvider
    public static Object[][] recordDefinitionsNames() {
        RestDataProvider restDataProvider = new RestDataProvider();
        String customRecordDefinition = "Custom-" + Long.toHexString(System.currentTimeMillis());
        new CommonSteps(restDataProvider)
                .createRecordDefinitionByTemplate(customRecordDefinition, "recordDefinitionWithAllTypes.json");
        List<String> allRecordDefinitions = restDataProvider.getRecordDefinitionNames();
        List<String> listOfRecordDefinitions =
                Arrays.asList(customRecordDefinition, "UserMessage", "Task", "Note",
                        "ApprovalSignature", "ApprovalSummary", "Person")
                        .stream().filter(allRecordDefinitions::contains).collect(Collectors.toList());
        return new Object[][]{
                new Object[]{ElementOfDesigner.CREATE_RECORD_INSTANCE, listOfRecordDefinitions, 1},
                new Object[]{ElementOfDesigner.UPDATE_RECORD_INSTANCE, listOfRecordDefinitions, 2},
        };
    }

    @Test(dataProvider = "recordDefinitionsNames", groups = Groups.CATEGORY_FULL, description = "US204287: inputMapPropertiesForRecordInstanceElement")
    @Stories("US204287")
    @Features("Inspector")
    @GUID("b274dbec-c7e7-4218-ad32-89608130b910")
    @Bug("")
    public void inputMapPropertiesForRecordInstanceElement(ElementOfDesigner element,
                                                           List<String> listOfRecordDefinitions,
                                                           int defaultPropertiesCount) {
        this.testDescription = "Verifies fields present in the 'Input Map' group for the :" + element.getName();
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        ProcessDefinitionEditorPage procDesigner = processDefinitionsTabPage.initiateNewProcess();
        ElementPropertiesTab inspector = procDesigner.getElementPropertiesTab();
        ActiveElement createRecInst = procDesigner.dragAndDrop(element);
        for (String recordDefinition : listOfRecordDefinitions) {
            List<String> recordDefinitionFields = RecordDefinition.geFieldsViaREST(recordDefinition);
            inspector.setPropertyValue(createRecInst, InspectorGroup.INPUT_MAP,
                    RECORD_DEFINITION_NAME.getName(), recordDefinition);
            procDesigner.confirmModalDialog(true);
            inspector.verifyPropertiesCount(InspectorGroup.INPUT_MAP,
                    recordDefinitionFields.size() + defaultPropertiesCount);
            inspector.verifyPropertiesPresence(InspectorGroup.INPUT_MAP,
                    recordDefinitionFields.stream().toArray(String[]::new));
        }
        processDefinitionsTabPage.navigateToPage();
    }
}
