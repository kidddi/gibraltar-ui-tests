package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.ElementProperties;
import com.bmc.gibraltar.automation.items.element.Link;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.Palette;
import com.bmc.gibraltar.automation.items.tab.PaletteForProcess;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;

public class AnnotationElementTest extends AppManagerBaseTest {
    private String processName = "Annotation" + Long.toHexString(System.currentTimeMillis());
    private ProcessDefinitionEditorPage processDesignerPage;
    private ActiveElement annotationElement;
    private String annotationElementPath;
    private String linkXpath;

    @Test(groups = Groups.CATEGORY_FULL, description = "US201413: annotationElementProperties")
    @Features("Canvas")
    @Stories("US201413")
    @Issue("SW00495711")
    @GUID("b14083b5-9fa7-4cb7-a3df-fabff30d3d72")
    @Bug("SW00495711")
    public void annotationElementProperties() {
        new CommonSteps(new RestDataProvider()).createProcessDefinition(processName);
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd)
                .navigateToPage();
        processDesignerPage = (ProcessDefinitionEditorPage) processDefinitionsTabPage.openTheSavedProcess(processName);
        ActiveElement end = processDesignerPage.getDroppedElement(ElementOfDesigner.END);
        annotationElement = processDesignerPage.dragNDropByCoordinates(ElementOfDesigner.ANNOTATION, "290", "270");
        annotationElementPath = annotationElement.getXPath();
        processDesignerPage.clickOnFreeSpaceOnCanvas();
        Link linkFromAnnotationToEnd = processDesignerPage.bindElements(annotationElement, end);
        linkXpath = linkFromAnnotationToEnd.getXPath();
        processDesignerPage.click(linkXpath);
        ElementPropertiesTab elTab = processDesignerPage.getElementPropertiesTab();
        elTab.verifyElementPropertyTabIsEmpty();
        elTab.setPropertyValue(annotationElement, InspectorGroup.PROPERTIES, ElementProperties.NOTES.getName(), "some text");
        processDesignerPage.clickOnFreeSpaceOnCanvas();
        processDefinitionsTabPage = processDesignerPage.saveProcess().closeProcess();
        processDefinitionsTabPage.waitForPageLoaded();
        assertTrue(processDefinitionsTabPage.getNamesOfSavedProcess().contains(processName));
    }

    @Test(dependsOnMethods = "annotationElementProperties", groups = Groups.CATEGORY_FULL, description = "US201413: verifyAnnotationLink")
    @Features("Canvas")
    @Stories("US201413")
    @Issue("SW00495711")
    @GUID("8d14f0d1-d35d-40e6-8ca9-cf3d764d0967")
    @Bug("SW00495711")
    public void verifyAnnotationLink() {
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd)
                .navigateToPage();
        processDesignerPage = (ProcessDefinitionEditorPage) processDefinitionsTabPage.openTheSavedProcess(processName);
        PaletteForProcess paletteTab = processDesignerPage.getTab(Palette.DEFAULT_TAB);
        paletteTab.collapsePanel();
        processDesignerPage.click(linkXpath);
        ElementPropertiesTab elTab = processDesignerPage.getElementPropertiesTab();
        elTab.verifyElementPropertyTabIsEmpty();
        processDesignerPage.click(annotationElementPath);
        elTab.verifyPropertyValue(InspectorGroup.PROPERTIES, ElementProperties.NOTES.getName(), "some text\n");
        processDefinitionsTabPage.navigateToPage();
    }
}