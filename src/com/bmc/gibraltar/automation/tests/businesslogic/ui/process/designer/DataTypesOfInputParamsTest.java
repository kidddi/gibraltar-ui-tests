package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.parameter.ProcessParameter;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataTypesOfInputParamsTest extends AppManagerBaseTest {
    private ProcessDefinitionEditorPage procEditor;
    private ProcessDefinitionsTabPage confPage;
    private ProcessPropertiesTab inspector;
    private InspectorGroup inputParameters = InspectorGroup.INPUT_PARAMETERS;
    private ProcessParameter inputParam;
    private String processDefinitionName = "AllTypesInputs" + RandomStringUtils.randomAlphanumeric(5);
    private List<ProcessParameter> generatedProcessParameters = new ArrayList<>();

    /**
     * @return needed for InputParams array of data: DataType + default value (related to particular DataType)
     */
    @DataProvider(name = "existingDataTypes")
    public static Object[][] existingDataTypes() {
        return new Object[][]{
                new Object[]{DataType.BOOLEAN, "True"},
                new Object[]{DataType.DATE, "2015-04-08"},
                new Object[]{DataType.DATE_TIME, "2014-01-01 01:23 PM"},
                new Object[]{DataType.DECIMAL, "-1.25"},
                new Object[]{DataType.FLOATING, "2.5e-4"},
                new Object[]{DataType.INTEGER, "99"},
                new Object[]{DataType.TEXT, "Some text"},
                new Object[]{DataType.TIME, "12:55 PM"},
                // TODO: need debug these 2 data types
                // new Object[]{ DataType.SELECTION, "opt" },
                // new Object[]{ DataType.RECORD_INSTANCE, "set record definition" },
        };
    }

    @BeforeGroups("processInputsDataTypes")
    public void goToProcessDesigner() {
        confPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procEditor = confPage.initiateNewProcess();
        inspector = procEditor.getProcessPropertiesTab();
        inspector.clickAddParameter(inputParameters);
    }

    @AfterGroups("processInputsDataTypes")
    public void closeProcessDesigner() {
        confPage.navigateToPage();
    }

    @Test(dataProvider = "existingDataTypes", groups = "processInputsDataTypes", description = "verifyPresenceDataTypeForInputParam")
    @Features("Inspector")
    @Stories("US200197/US201417+US201453+US201454")
    @GUID("b51009ed-c6a3-4f0e-8f5e-11eb944a3ad4")
    public void verifyPresenceDataTypeForInputParam(DataType dataType, String defValue) {
        testDescription = "Verifies tha data type: " + dataType.getName()
                + " is available in the process input data type dropdown.";
        assertTrue(inspector.getListDataTypesForProcParameters(inputParameters).contains(dataType),
                "DataType:'" + dataType + "' is not available in " + inputParameters);
    }

    @Test(dataProvider = "existingDataTypes", groups = "processInputsDataTypes", description = "verifyPropertiesForInputParam")
    @Features("Inspector")
    @Stories("US200197/US201417+US201453+US201454 + US201386")
    @GUID("9e21dc87-a6f3-459c-9806-278afba23de4")
    public void verifyPropertiesForInputParam(DataType dataType, String defValue) {
        testDescription = "Verify PropertiesForInputParam: data type: " + dataType.getName() + ". def Value: "
                + defValue;
        inspector.selectDataTypeForProcParameter(inputParameters, "last()", dataType);
        List<String> propActual = inspector.getListOfPropertiesForParameter(inputParameters, "last()");
        List<String> propExpected = Arrays.asList(dataType.propertiesForInputParameter());
        log.info("\n Actual: " + propActual.toString() + "\n Expected: " + propExpected);
        assertTrue(propActual.containsAll(propExpected),
                "DataType [" + dataType + "]  Actual properties:'" + propActual + "'\n but Expected: " + propExpected);
    }

    @Test(dataProvider = "existingDataTypes", groups = "processInputsDataTypes", description = "verifyDefValueDataType")
    @Features("Inspector")
    @Stories("US200197/US201417+US201453+US201454 + US201386")
    @GUID("ab16c226-1dde-496f-adfe-2e1fd8e8c547")
    public void verifyDefValueDataType(DataType dataType, String defValue) {
        testDescription = "Verify DefValueDataType: data type: " + dataType.getName() + ". def Value: " + defValue;
        String paramName = "defValue_" + dataType;
        inputParam = new ProcessParameter(paramName, dataType);
        inspector.fillProcParamName(inputParameters, "last()", paramName);
        inspector.selectDataTypeForProcParameter(inputParameters, "last()", dataType);
        String erasedDefValue = inspector.getDefaultValueForParamByOrder(inputParameters, "last()");
        inspector.verifyTrue(erasedDefValue.isEmpty()
                        || erasedDefValue.endsWith("PM")
                        || erasedDefValue.endsWith("AM"),
                "Default value for chosen DataType '" + dataType + "' was not auto erased");
        inspector.fillDefaultValueForProcParam(inputParameters, inputParam, defValue);
        String actual = inspector.getDefaultValueForParamByOrder(inputParameters, "last()");
        assertEquals(actual, defValue, "Actual default Value:'" + actual + "' is different from Expected:'" + defValue + "'");
    }
    // TODO: test pre-population

    // TODO: test check the same DataType on StartProcessPage

    @Test(groups = Groups.CATEGORY_FULL, description = "verifySaveCloseReopenDataTypeForInputParam")
    @Features("Inspector")
    @Stories("US200197/US201417+US201453+US201454")
    @GUID("d91a80ba-3174-48b5-8f17-362cdd3766fa")
    public void verifySaveCloseReopenDataTypeForInputParam() {
        ProcessDefinitionsTabPage tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procEditor = tabPage.initiateNewProcess();
        procEditor.setProcessName(processDefinitionName);
        procEditor.bindElements
                (procEditor.getDroppedElement(ElementOfDesigner.START),
                        procEditor.getDroppedElement(ElementOfDesigner.END));
        inspector = procEditor.getProcessPropertiesTab();
        generatedProcessParameters.clear();
        addProcessParametersOfAllTypes();
        confPage = procEditor.saveProcess().closeProcess();
        procEditor = (ProcessDefinitionEditorPage) confPage.openTheSavedProcess(processDefinitionName);
        inspector = procEditor.getProcessPropertiesTab();
        List<ProcessParameter> allAddedParams = inspector.getListOfProcessParams(inputParameters);
        allAddedParams.stream().forEach(param ->
                verifyEquals(inspector.getDataTypeForParameterByParamName(inputParameters, param.getName()),
                        param.getDataType(), "Wrong DataType after save/reopen"));
        tabPage.navigateToPage();
    }

    private void addProcessParametersOfAllTypes() {
        List<Object[]> params = Arrays.asList(existingDataTypes());
        params.stream().forEach(procParam -> inspector.addProcessParameter(inputParameters,
                generateNewParameter((DataType) procParam[0], procParam[1].toString())));
    }

    private ProcessParameter generateNewParameter(DataType dataType, String defValue) {
        String paramName = dataType.getName() + "_test";
        inputParam = new ProcessParameter(
                paramName,
                dataType,
                true,
                "Helper " + paramName,
                defValue);
        if (dataType == DataType.SELECTION) {
            inputParam.setOptions(new String[]{defValue, defValue + 1, defValue + 2});
        }
        if (dataType == DataType.RECORD_INSTANCE) {
            inputParam.setRecordDefinition("Task");
        }
        generatedProcessParameters.add(inputParam);
        return inputParam;
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyWarningMessageIfDeleteParameter")
    @Features("Inspector")
    @Stories("US201254")
    @GUID("c179e639-dbfe-45e8-9a84-dc1aa0f12d5b")
    public void verifyWarningMessageIfDeleteParameter() {
        RestDataProvider dataProvider = new RestDataProvider();
        String processName = "DataTypes" + RandomStringUtils.randomAlphanumeric(8);
        String processDefinitionJson = new CommonSteps(dataProvider).createCustomProcessDefinition(processName,
                "processDefinitionWithInputsOutputs.json");
        List<String> inputs = JsonPath.from(processDefinitionJson).get("inputParams.name");
        ProcessDefinitionsTabPage definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        definitionsTabPage.refresh();
        procEditor = (ProcessDefinitionEditorPage) definitionsTabPage.openTheSavedProcess(processName);
        inspector = procEditor.getProcessPropertiesTab();
        inspector.verifyDataTypeReadOnly(inputParameters, "last()");
        inspector.clickDeleteButton(InspectorGroup.INPUT_PARAMETERS, inputs.get(0));
        procEditor.verifyModalDialog("Are you sure you want to delete this item?", true);
        procEditor.confirmModalDialog(false);
        definitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyReAddDeletedDataTypeParameter")
    @Features("Inspector")
    @Stories("US201254")
    @GUID("3b9647ca-4b3e-4542-936f-7d11eae145e2")
    public void verifyReAddDeletedDataTypeParameter() {
        deleteAllParameters();
        addProcessParametersOfAllTypes();
        procEditor.saveProcess();
    }

    private void deleteAllParameters() {
        RestDataProvider dataProvider = new RestDataProvider();
        String processDefinitionName = "AllInputs" + RandomStringUtils.randomAlphanumeric(8);
        new CommonSteps(dataProvider).createCustomProcessDefinition(processDefinitionName,
                "inputsWithAllDataTypes.json");
        ProcessDefinitionsTabPage definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procEditor = (ProcessDefinitionEditorPage) definitionsTabPage.openTheSavedProcess(processDefinitionName);
        inspector = procEditor.getProcessPropertiesTab();
        List<ProcessParameter> allAddedParams = inspector.getListOfProcessParams(inputParameters);
        allAddedParams.stream().forEach(procParam ->
                inspector.deleteProcessParam(inputParameters, procParam.getName(), true));
    }
}