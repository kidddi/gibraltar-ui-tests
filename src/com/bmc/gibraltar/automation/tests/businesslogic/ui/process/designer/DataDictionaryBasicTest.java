package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.datadictionary.ConditionsEditor;
import com.bmc.gibraltar.automation.items.datadictionary.DataDictionary;
import com.bmc.gibraltar.automation.items.datadictionary.DictionaryGroup;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Arrays;
import java.util.List;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.USER_TASK;
import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;

public class DataDictionaryBasicTest extends AppManagerBaseTest {
    private ProcessDefinitionEditorPage procDesigner;
    private ActiveElement userTask;
    private DataDictionary dataDictionary;
    private ProcessDefinitionsTabPage tabPage;
    private ConditionsEditor conditionExpressionEditor;

    @BeforeMethod
    protected void prepareUserTaskForDataDictionary() {
        tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDesigner = tabPage.initiateNewProcess();
        userTask = procDesigner.dragAndDrop(USER_TASK);
        userTask.setPropertiesByDefault();
    }

    @AfterMethod(alwaysRun = true)
    protected void goToProcessDefinitionsTab() {
        if (dataDictionary != null && dataDictionary.isDisplayed()) {
            dataDictionary.close();
        }
        if (conditionExpressionEditor != null && conditionExpressionEditor.isDisplayed()) {
            conditionExpressionEditor.close();
        }
        tabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyAvailabilityDDForElementsProperties")
    @Features("Data Dictionary")
    @Stories("US201116")
    @GUID("434aa317-597f-40fc-9839-eb633d92ea34")
    public void verifyAvailabilityDDForElementsProperties() {
        ElementProperties[] userTaskProperties = {PRIORITY, STATUS, SUBMITTER, SUMMARY};
        for (ElementProperties property : userTaskProperties) {
            userTask.openDataDictionaryFor(property).close();
            DataDictionary dictionary = userTask.openDataDictionaryFor(property).doubleClickVar(userTask, property);
            dictionary.verifyExpressionCorrectness();
            dictionary.clickOk();
            dictionary.verifyResultCorrectness();
        }
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyEditPropertyValueDialog")
    @Features("Data Dictionary")
    @Stories("US201116, US206060")
    @GUID("d5461710-d428-426e-871e-74903a6e519b")
    public void verifyEditPropertyValueDialog() {
        for (DictionaryGroup.GENERAL generalVar : DictionaryGroup.GENERAL.values()) {
            dataDictionary = userTask.openDataDictionaryFor(PRIORITY);
            assertTrue(dataDictionary.isDisplayed(), "Data Dictionary Header is absent");
            dataDictionary.cleanExpressionTextBox();
            assertTrue(dataDictionary.isVarPresentInDictionary(generalVar.getName()));
            dataDictionary.doubleClickVar(generalVar);
            assertEquals(dataDictionary.getTagsFromExpressionTextBox().get(0), generalVar.getName());
            dataDictionary.clickOk();
            assertEquals(userTask.getPropertyValue(PRIORITY), generalVar.getValue());
        }
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyDDHintInfoMessage")
    @Features("Data Dictionary")
    @Stories("US203089")
    @GUID("8e90b30d-82a1-4cd6-b8cb-61f33110c3e9")
    public void verifyDDHintInfoMessage() {
        dataDictionary = userTask.openDataDictionaryFor(PRIORITY);
        String msgText = "Data dictionary items can be inserted into expressions. Drag an drop from data dictionary and" +
                " drop it at the desired location in the expression. Alternately, double-click on a dictionary item, " +
                "or click on a '+' icon next to a dictionary item to add it to the end of the expression.";
        dataDictionary.verifyHintInfoMessage(msgText);
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyDDVarsAsTags")
    @Features("Data Dictionary")
    @Stories("US203089")
    @GUID("d99f258b-1c3a-452e-80e3-ebc6f178f9ed")
    public void verifyDDVarsAsTags() {
        userTask.setProperty(COMPLETION_CRITERIA, "");
        conditionExpressionEditor = userTask.openConditionsEditorFor(COMPLETION_CRITERIA);
        List<String> actualAddedTags = Arrays.asList("Current User", "Current Date");
        conditionExpressionEditor
                .doubleClickVar(DictionaryGroup.GENERAL.CURRENT_USER);
        conditionExpressionEditor
                .addOperatorToExpression("!=")
                .doubleClickVar(DictionaryGroup.GENERAL.CURRENT_DATE);
        verifyEquals(conditionExpressionEditor.getTagsFromExpressionTextBox(), actualAddedTags);
    }
}