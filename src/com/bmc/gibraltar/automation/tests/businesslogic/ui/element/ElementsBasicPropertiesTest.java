package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.dataprovider.ElementsProperties;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.processdesigner.ToolbarItem;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.*;

public class ElementsBasicPropertiesTest extends AppManagerBaseTest {
    private ProcessDefinitionsTabPage processDefinitionsTabPage;
    private ProcessDefinitionEditorPage procDefEditPage;
    private ElementPropertiesTab elementTab;

    @DataProvider
    public static Iterator<Object[]> labelsOfElements() {
        List<ElementOfDesigner> withoutLabel = Arrays.asList(START, END, PARALLEL_GATEWAY, EXCLUSIVE_GATEWAY);
        List<Object[]> labels =
                Arrays.asList(ElementOfDesigner.values()).stream()
                        .filter(el -> !withoutLabel.contains(el))
                        .filter(el -> el != TIMER)
                        .filter(el -> el != ANNOTATION)
                        .map(el -> new Object[]{el, true, ElementProperties.LABEL.getName(), el.getName()}).collect(Collectors.toList());
        labels.addAll(withoutLabel.stream()
                .map(el -> new Object[]{el, false, ElementProperties.LABEL.getName(), ""}).collect(Collectors.toList()));
        labels.add(new Object[]{ANNOTATION, true, ElementProperties.NOTES.getName(), ""});
        return labels.iterator();
    }

    @Test(dataProvider = "labelsOfElements", groups = Groups.CATEGORY_FULL, description = "US200589, US201385: elementLabel")
    @Features("Inspector")
    @Stories("US200589, US201385")
    @GUID("b0ed1adf-e393-4d4b-b3ca-37772767b562")
    @Bug("")
    public void elementLabel(ElementOfDesigner el, boolean isLabelMarkedAsRequired,
                             String property, String defaultPropertyValue) {
        this.testDescription = "Verifies that element: " + el.getName() + " has default label.";
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = processDefinitionsTabPage.initiateNewProcess();
        elementTab = procDefEditPage.getElementPropertiesTab();
        ActiveElement elOnCanvas = procDefEditPage.dragAndDropToCanvas(el);
        procDefEditPage.clickOnElement(elOnCanvas);
        elementTab.verifyPropertyMarkedAsRequired(elOnCanvas, InspectorGroup.PROPERTIES, isLabelMarkedAsRequired, property);
        elementTab.verifyPropertyValue(InspectorGroup.PROPERTIES, property, defaultPropertyValue);
        processDefinitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US201255: tabsStateAndIconsPresenceOnCanvasToolbar")
    @Features("Canvas")
    @Stories("US201255")
    @GUID("62dca945-cbb9-4e36-a662-7b4ff9bf594c")
    @Bug("")
    public void tabsStateAndIconsPresenceOnCanvasToolbar() {
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = processDefinitionsTabPage.initiateNewProcess();
        ProcessPropertiesTab processTab = procDefEditPage.getProcessPropertiesTab();
        processTab.verifyTabActive();
        elementTab = procDefEditPage.getElementPropertiesTab();
        elementTab.switchToTab();
        elementTab.verifyElementPropertyTabIsEmpty();
        elementTab.collapsePanel();
        Arrays.asList(ToolbarItem.values()).stream()
                .forEach(icon -> assertTrue(isElementPresent(icon.getIconPath()),
                        icon + " icon not present on Canvas toolbar."));
        elementTab.expandPanel();
        processDefinitionsTabPage.navigateToPage();
    }

    @Test(dataProvider = "elementsPropertiesMap", dataProviderClass = ElementsProperties.class, groups = Groups.CATEGORY_FULL, description = "US199210, US199760: propertyGroupsPresenceForElement")
    @Features("Inspector")
    @Stories("US199210, US199760")
    @GUID("a9d89acb-42ed-404f-b496-c1a4014eef6b")
    @Bug("")
    public void propertyGroupsPresenceForElement(ElementOfDesigner element, int groupsCount,
                                                 List<ElementsProperties.Group> propertiesGroups) {
        testDescription = "Verifies groups and fields presence for the element: " + element.getName();
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        procDefEditPage = processDefinitionsTabPage.initiateNewProcess();
        log.info("Checking Properties Presence For Element: " + element);
        procDefEditPage.clearCanvas();
        ActiveElement elementOnCanvas = procDefEditPage.dragAndDropToCanvas(element);
        procDefEditPage.clickOnElement(elementOnCanvas);
        elementTab = procDefEditPage.getElementPropertiesTab();
        elementTab.verifyTabContainsProperties();
        verifyEquals(elementTab.getGroupsCount(), groupsCount, "Number of groups is wrong.");
        elementTab.expandAllGroups();
        for (ElementsProperties.Group singleGroup : propertiesGroups) {
            String groupName = singleGroup.getGroupName();
            verifyTrue(elementTab.getGroupsList().contains(groupName), groupName + " group is not present in Inspector.");
            elementTab.verifyPropertiesCount(singleGroup.getGroup(), singleGroup.getLabelsCount());
            elementTab.verifyPropertiesPresence(singleGroup.getGroup(), singleGroup.getLabels());
        }
        processDefinitionsTabPage.navigateToPage();
    }
}