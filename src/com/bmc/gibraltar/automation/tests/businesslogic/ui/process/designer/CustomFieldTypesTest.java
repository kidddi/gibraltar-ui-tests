package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.parameter.ProcessParameter;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;

public class CustomFieldTypesTest extends AppManagerBaseTest {
    private String processDefinitionNames = getClass().getSimpleName() + RandomStringUtils.randomAlphanumeric(3);

    @Test(groups = Groups.CATEGORY_FULL, description = "createDefinitionWithCustomTypes")
    @Features("Data Dictionary")
    @Stories("US205911")
    @GUID("5ef3c160-92fb-408d-9f95-7d6f6386b46c")
    public void createDefinitionWithCustomTypes() {
        ProcessDefinitionEditorPage processDesigner = new ProcessDefinitionEditorPage(wd).navigateToPage();
        processDesigner
                .setProcessName(processDefinitionNames)
                .bindElements(processDesigner.getDroppedElement(ElementOfDesigner.START),
                        processDesigner.getDroppedElement(ElementOfDesigner.END));

        RestDataProvider restApi = new RestDataProvider();
        List<String> allCustomTypes = restApi.getCustomFieldDefinitionTypes();
        for (String type : allCustomTypes) {
            DataType dataType = DataType.getDataTypeByResourceType(type);
            ProcessParameter customParam = new ProcessParameter(dataType.getName() + "_test", dataType);
            processDesigner.getProcessPropertiesTab()
                    .addProcessParameter(InspectorGroup.INPUT_PARAMETERS, customParam);
            processDesigner.saveProcess();
        }
        ProcessDefinitionsTabPage tabPage = processDesigner.closeProcess();
        tabPage.refresh();
        assertTrue(tabPage.getNamesOfSavedProcess().contains(processDefinitionNames));
    }
}