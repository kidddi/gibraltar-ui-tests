package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.parameter.ProcessParameter;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.pages.ManageTabPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.StartProcessPage;
import com.bmc.gibraltar.automation.tests.TaskManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CustomTypesOfInputParamsTest extends TaskManagerBaseTest {
    private String procName = getClass().getSimpleName() + RandomStringUtils.randomAlphanumeric(3);
    private Set<ProcessParameter> inputParams = new HashSet<>();
    private StartProcessPage startProcessPage;

    @BeforeClass
    protected void createDefinition() {
        ProcessDefinitionEditorPage processDesigner = new ProcessDefinitionEditorPage(wd).navigateToPage();
        processDesigner
                .setProcessName(procName)
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
            inputParams.add(customParam);
        }
        processDesigner.closeProcess();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyCustomDataTypesOnStartProcessPage")
    @Features("Start new process page")
    @Stories("US205398")
    @GUID("7a5f6cf1-b7e2-44c7-b5c1-87adf3f81d0b")
    public void verifyCustomDataTypesOnStartProcessPage() {
        ManageTabPage manageTabPage = new ManageTabPage(wd).navigateToPage();
        manageTabPage.selectProcess(procName);
        startProcessPage = manageTabPage.clickStart();

        List<String> expectedFieldsNames = new LinkedList<>();
        List<String> actualFieldNames = startProcessPage.getListOfAllProcInputs();
        List<String> expectedCustomParameterNames = new LinkedList<>();
        List<String> actualCustomParameterNames = startProcessPage.getAllCustomFieldDefinitionLabels();

        for (ProcessParameter param : inputParams) {
            if (param.getDataType().equals(DataType.CONNECTION_INSTANCE))
                expectedFieldsNames.add(param.getName());
            else expectedCustomParameterNames.add(param.getName());
            if (param.getDataType().equals(DataType.PERSON)) {
                expectedFieldsNames.add("Name");
                expectedFieldsNames.add("Birth Date");
            }
        }

        verifyTrue(actualFieldNames.containsAll(expectedFieldsNames),
                "\n Expected input Parameter fields: " + expectedFieldsNames + "\n But present: " + actualFieldNames);
        verifyTrue(actualCustomParameterNames.containsAll(expectedCustomParameterNames),
                "Expected Custom Parameters: " + expectedCustomParameterNames + "\n But present: "
                        + actualCustomParameterNames);
        startProcessPage.closePageOfStartProcess();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyStartingProcessWithCustomDataTypes")
    @Features("Start new process page")
    @Stories("US205398")
    @GUID("e91321b6-006a-453e-8882-07719b8c3aa1")
    public void verifyStartingProcessWithCustomDataTypes() {
        ManageTabPage manageTabPage = new ManageTabPage(wd).navigateToPage();
        manageTabPage.selectProcess(procName);
        startProcessPage = manageTabPage.clickStart();
        startProcessPage.fillInputField("Name", "Jim1")
                .fillInputField("Birth Date", "2015-04-08")
                .startTheProcess()
                .verifyNoErrorAppears();
    }
}