package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.datadictionary.ConditionsEditor;
import com.bmc.gibraltar.automation.items.datadictionary.DataDictionary;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.element.LoopType;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.CALL_ACTIVITY;
import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.USER_TASK;
import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class ActivitiesElementsTest extends AppManagerBaseTest {
    private ProcessDefinitionsTabPage configurePage;
    private ProcessDefinitionEditorPage editView;
    private List<String> loopTypes = Arrays.asList("Parallel", "Sequential");

    @DataProvider
    public static Object[][] actionElementsAndLoopTypes() {
        List<LoopType> loopsProperties = Arrays.asList(LoopType.SEQUENTIAL, LoopType.PARALLEL, LoopType.EMPTY);
        return new Object[][]{
                new Object[]{ElementOfDesigner.USER_TASK, loopsProperties},
                new Object[]{ElementOfDesigner.SUB_PROCESS, loopsProperties},
                new Object[]{ElementOfDesigner.CALL_ACTIVITY, loopsProperties},
                new Object[]{ElementOfDesigner.RECEIVE_TASK, loopsProperties},
        };
    }

    @BeforeClass
    public void preconditions() {
        configurePage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        editView = configurePage.initiateNewProcess();
    }

    @AfterMethod
    public void cleanUp() {
        editView.clearCanvas(true);
    }

    @Test(dataProvider = "actionElementsAndLoopTypes", groups = Groups.CATEGORY_FULL, description = "US204003: multiInstancePropertiesPresenceForAction")
    @Features("Inspector")
    @Stories("US204003")
    @GUID("98d8d2f1-4d44-420d-ad7e-ee5244ba2945")
    @Bug("")
    public void multiInstancePropertiesPresenceForAction(ElementOfDesigner elementOfDesigner, List<LoopType> loopsProperties) {
        this.testDescription = "Verifies that appropriate icon present when the multi instance loop is chosen for the action: " +
                elementOfDesigner.getName() + " And multi instance fields are displayed.";
        ActiveElement element = editView.dragAndDropToCanvas(elementOfDesigner);
        if (elementOfDesigner.equals(USER_TASK, CALL_ACTIVITY))
            element.setPropertiesByDefault();
        ElementPropertiesTab elementPropertiesTab = editView.getElementPropertiesTab();
        elementPropertiesTab.expandAllGroups();
        for (LoopType loopType : loopsProperties) {
            element.setProperty(LOOP_TYPE, loopType.getLoopType());
            assertTrue(loopType.isMultiInstanceIconPresent(element));
            List<String> presentFields = elementPropertiesTab.getPropertiesList(InspectorGroup.MULTI_INSTANCE_LOOP);
            List<ElementProperties> props = presentFields.stream().map(ElementProperties::get).collect(Collectors.toList());
            assertThat(props, hasItems(loopType.getMultiInstanceFields()));
        }
    }

    @Test(dataProvider = "actionElementsAndLoopTypes", groups = Groups.CATEGORY_FULL, description = "US204003: dictionaryEditorsForMultiInstanceProperties")
    @Features("Inspector")
    @Stories("US204003")
    @GUID("5bb5fcc2-cb65-40a9-852e-d75828cbd6c4")
    @Bug("")
    public void dictionaryEditorsForMultiInstanceProperties(ElementOfDesigner elementOfDesigner, List<LoopType> loopsProperties) {
        this.testDescription = "Verifies that the Data Dictionary is displayed when to click on 'Edit' link of properties in 'Multi Instance Loop' group " +
                " for the action:" + elementOfDesigner.getName();
        ActiveElement element = editView.dragAndDropToCanvas(elementOfDesigner);
        element.setPropertiesByDefault();
        for (String loop : loopTypes) {
            element.setProperty(LOOP_TYPE, loop);
            DataDictionary dataDictionary = element.openDataDictionaryFor(INPUT_DATA);
            verifyTrue(dataDictionary.isDisplayed(), "Data Dictionary is not present for INPUT_DATA.");
            dataDictionary.close();
            ConditionsEditor conditionsEditor = element.openConditionsEditorFor(COMPLETION_CONDITION);
            assertTrue(conditionsEditor.isDisplayed(), "Conditions Editor is not present for COMPLETION_CONDITION.");
            conditionsEditor.clickOk();
        }
    }

    @Test(dataProvider = "actionElementsAndLoopTypes", groups = Groups.CATEGORY_FULL, description = "US204003: verifyInputDataItemDropDown")
    @Features("Inspector")
    @Stories("US204003")
    @Issue("SW00498719: Input Data Item of Sub Process element is empty despite of Process Input Parameters")
    @GUID("eaeeb829-3552-406e-9d17-a5058f50035d")
    @Bug("SW00498719")
    public void verifyInputDataItemDropDown(ElementOfDesigner elementOfDesigner, List<LoopType> loopsProperties) {
        this.testDescription = "Verifies that 'Input Data' dropdown in the 'Multi Instance Loop' group contains expected options for the action:"
                + elementOfDesigner.getName();
        ActiveElement element = editView.dragAndDropToCanvas(elementOfDesigner);
        if (elementOfDesigner.equals(USER_TASK, CALL_ACTIVITY)) {
            element.setPropertiesByDefault();
        }
        String[] properties;
        switch (elementOfDesigner) {
            case RECEIVE_TASK:
                String signalParameter1 = "param name 1";
                String signalParameter2 = "param name 2";
                element.setProperty(PARAMETER_NAME, signalParameter1).setProperty(PARAMETER_NAME, signalParameter2);
                properties = new String[]{signalParameter1, signalParameter2};
                break;
            case SUB_PROCESS:
                String inputParameter1 = "input text param 1";
                String inputParameter2 = "input text param 2";
                editView.addInputParam(inputParameter1);
                editView.addInputParam(inputParameter2);
                properties = new String[]{inputParameter1, inputParameter2};
                break;
            default:
                List<String> props = element.getAllPropertiesNamesFromGroup(InspectorGroup.INPUT_MAP);
                properties = props.toArray(new String[props.size()]);
        }
        editView.getElementPropertiesTab().expandAllGroups();
        for (String loop : loopTypes) {
            element.setProperty(LOOP_TYPE, loop);
            assertThat(elementOfDesigner.getName() + " SW00498719", element.getDropDownList(INPUT_DATA_ITEM),
                    hasItems(properties));
        }
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US204003: cancelTaskInputMapOfMultiInstanceUserTask")
    @Features("Inspector")
    @Stories("US204003")
    @GUID("c38ed105-1289-4227-b713-b9407838cd3b")
    public void cancelTaskInputMapOfMultiInstanceUserTask() {
        ActiveElement element = editView.dragAndDropToCanvas(USER_TASK);
        element.setPropertiesByDefault();
        ElementPropertiesTab inspector = editView.getElementPropertiesTab();
        List<String> properties = inspector.getPropertiesList(InspectorGroup.INPUT_MAP);
        List<String> inspectorGroups = inspector.getGroupsList();
        assertTrue(inspectorGroups.contains(InspectorGroup.CANCEL_TASK_INPUT_MAP.getGroupName()));
        inspector.expandAllGroups();
        List<String> propertiesFromCancelTask = inspector.getPropertiesList(InspectorGroup.CANCEL_TASK_INPUT_MAP);
        verifyTrue(isAlphabetical(propertiesFromCancelTask),
                "USER_TASK. CANCEL_TASK_INPUT_MAP. The fields  are not in alphabetical order.");
        //TODO: remove from comparison the required fields
        properties.remove("Priority:");
        propertiesFromCancelTask.remove("Priority:");
        assertThat("Properties for User Task are not equal", properties, equalTo(propertiesFromCancelTask));
    }
}