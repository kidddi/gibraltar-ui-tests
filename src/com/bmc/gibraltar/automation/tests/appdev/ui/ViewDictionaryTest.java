package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.items.component.ActiveComponent;
import com.bmc.gibraltar.automation.items.component.Component;
import com.bmc.gibraltar.automation.items.datadictionary.DictionaryGroup;
import com.bmc.gibraltar.automation.items.datadictionary.ViewDictionary;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.element.Property;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ViewDesignerInspectorTabs;
import com.bmc.gibraltar.automation.pages.ViewDefinitionEditorPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.TestCaseId;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class ViewDictionaryTest extends AppManagerBaseTest {
    private RestDataProvider dataProvider = new RestDataProvider();
    private List<String> expectedAllComponents;
    private String viewDefinitionName = "Edit Task";
    private ViewDefinitionEditorPage viewDesigner;
    private Component testComponent = Component.ATTACHMENTS;
    private ActiveComponent activeComponent;
    private ViewDesignerInspectorTabs componentInfoInspector;
    private Property testProperty = ElementProperties.RECORD_DEFINITION_NAME;
    private ViewDictionary viewDictionary;
    private String[] expectedOperators = {"(", ")", "+", "-", "*", "/", "%", ">", "<", "=", "!=", ">=",
            "<=", "AND", "OR", "NOT", "NULL", "LIKE"};

    @BeforeClass
    protected void preconditionsViewDictionary() {
        log.info("BeforeClass start");
        viewDesigner = appManager.goInitiallyToView(viewDefinitionName);
        activeComponent = viewDesigner.getDroppedComponent(testComponent);
        componentInfoInspector = viewDesigner.clickOnComponent(activeComponent)
                .toInspectorTab(ViewDesignerInspectorTabs.Tab.COMPONENT_INFORMATION);
        expectedAllComponents = dataProvider.getAllComponentsForViewDefinition(viewDefinitionName);
        log.info("BeforeClass end");
    }

    @BeforeMethod
    private void initViewDictionary() {
        log.info("BeforeMethod start");
        if (viewDictionary == null || (!viewDictionary.isDisplayed()))
            viewDictionary = componentInfoInspector.openViewDictionaryForProperty(activeComponent, testProperty);
        log.info("BeforeMethod end");
    }

    @AfterMethod
    private void closeViewDictionary() {
        log.info("AfterMethod start");
        if (viewDictionary != null && viewDictionary.isDisplayed())
            viewDictionary.close();
        log.info("AfterMethod end");
    }

    @Test
    @Features("P528 - Process owner tailors view")
    @Stories("US207656: View expression dictionary")
    public void viewDictionaryGroupsPresenceTest() {
        List<String> expectedRootGroups = Arrays.asList("Components", "View Parameters");
        List<String> actualAllGroups = viewDictionary.getGroupsPresentInDataDictionary();
        verifyTrue(actualAllGroups.containsAll(expectedRootGroups), "Root Groups on View Dictionary " +
                "do NOT have all expected:" + expectedRootGroups);
        verifyTrue(actualAllGroups.containsAll(expectedAllComponents), "Sub Groups on View Dictionary " +
                "do NOT  match all expected Components:" + expectedAllComponents);
    }

    @Test
    @Features("P528 - Process owner tailors view")
    @Stories("US207656: View expression dictionary")
    public void viewDictionaryVarsPresenceTest() {
        expectedAllComponents.forEach(this::checkVarsPresentForComponent);
    }

    private void checkVarsPresentForComponent(String componentName) {
        log.info("\n Checking ViewDictionary variables as properties of Component: " + componentName);
        viewDictionary.expandGroup(componentName);
        Map<String, String> propertiesOfComponent = dataProvider
                .getMapOfComponentProperties(viewDefinitionName, componentName);
        for (String expectedProperty : propertiesOfComponent.keySet()) {
            verifyTrue(viewDictionary.isVarPresentInDictionary(expectedProperty), "View Dictionary " +
                    "does NOT have an expected variable: " + expectedProperty);
        }
    }

    @Test
    @Features("P528 - Process owner tailors view")
    @Stories("US207656: View expression dictionary")
    public void selectVarFromViewDictionaryTest() {
        viewDictionary.cleanExpressionTextBox();
        String actualText = viewDictionary.getExpressionFieldValue();
        List<String> actualTags = viewDictionary.getTagsFromExpressionTextBox();
        verifyEquals(actualText, "", "Expression Text Box has not expected text: " + actualText);
        verifyEquals(actualTags.size(), 0, "Expression Text Box has not expected vars as Tags: " + actualTags);
        viewDictionary.doubleClickInTreeOnVar(DictionaryGroup.VIEW_PARAMETERS, new String[]{"taskId"});
        viewDictionary.doubleClickInTreeOnVar(DictionaryGroup.COMPONENTS, new String[]{testComponent.getName(),
                "Record Instance Id"});
        List<String> expectedTags = Arrays.asList("taskId", "Record Instance Id");
        actualTags = viewDictionary.getTagsFromExpressionTextBox();
        verifyEquals(actualTags.size(), 2, "Expression Text Box has not expected count as Tags: " + actualTags.size());
        verifyTrue(actualTags.containsAll(expectedTags), "Not all Tags are present on Expression Text Box from expected: "
                + expectedTags);
    }

    @Test
    @Features("P528 - Process owner tailors view")
    @Stories("US207656: View expression dictionary")
    public void updateVarFromViewDictionaryTest() {
        viewDictionary.cleanExpressionTextBox();
        viewDictionary.doubleClickInTreeOnVar(DictionaryGroup.COMPONENTS, new String[]{testComponent.getName(),
                "Record Instance Id"});
        String componentId = dataProvider.getComponentDefinitionId(dataProvider.getViewDefinitionId(viewDefinitionName),
                testComponent.getType());
        viewDictionary.apply();
        String appliedValue = componentInfoInspector.getPropertyValue(InspectorGroup.PROPERTIES, testProperty.getName());
        verifyEquals(appliedValue, "${view.components." + componentId + ".recordInstanceId}");
    }

    @Test
    @Features("P522 - Developer can use expressions")
    @Stories("US208747: Design time experience - Process owner can specify complex expressions in View Dictionary")
    @TestCaseId("TC940799")
    @Issue("SW00501107: NOT, != and NULL operations are not supported in the view dictionary")
    public void viewDictionaryOperatorsSet() {
        assertThat("View Dictionary does not have all expected operator buttons.", viewDictionary.getPresentOperators(),
                hasItems(expectedOperators));
    }

    @Test
    @Features("P522 - Developer can use expressions")
    @Stories("US208747: Design time experience - Process owner can specify complex expressions in View Dictionary")
    @TestCaseId("TC940871")
    @Issue("SW00501107: NOT, != and NULL operations are not supported in the view dictionary")
    public void operatorCanBeAddedIntoExpression() {
        viewDictionary.cleanExpressionTextBox();
        Arrays.asList(expectedOperators).stream().forEach(viewDictionary::addOperatorToExpression);
        String expression = viewDictionary.getExpressionFieldValue();
        Arrays.asList(expectedOperators).stream().forEach(operator -> verifyTrue(expression.contains(operator),
                String.format("Operator %s is not added into expression.", operator)));
        viewDictionary.apply();
        String value = componentInfoInspector.getPropertyValue(InspectorGroup.PROPERTIES, testProperty.getName());
        Arrays.asList(expectedOperators).stream().forEach(operator ->
                verifyTrue(value.contains(operator), String.format("Operator %s is not added into expression.", operator)));
    }
}
