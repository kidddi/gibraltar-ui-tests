package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.Link;
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

import java.util.Optional;

public class SequenceFlowTest extends AppManagerBaseTest {
    private String processName = "LinkProcess" + Long.toHexString(System.currentTimeMillis());
    private ProcessDefinitionsTabPage processDefinitionsTabPage;
    private ProcessDefinitionEditorPage processDefinitionEditorPage;
    private Link linkFromStartToEndElement;
    private String linkFromStartToEndElementPath;

    @DataProvider
    public static Object[][] linksProperties() {
        return new Object[][]{
                new Object[]{ElementOfDesigner.START, new String[]{"Type:", "Label:", "Label Position:", "Description:"}},
                new Object[]{ElementOfDesigner.EXCLUSIVE_GATEWAY, new String[]{"Type:", "Label:", "Label Position:",
                        "Description:", "Condition:"}},
        };
    }

    @Test(dataProvider = "linksProperties", groups = Groups.CATEGORY_FULL, description = "US201731: checkPropertiesPresenceForSequenceFlow")
    @Features("Inspector")
    @Stories("US201731")
    @GUID("1adef3dd-17c1-4839-abbc-60920f80733d")
    @Bug("")
    public void checkPropertiesPresenceForSequenceFlow(ElementOfDesigner element, String[] props) {
        this.testDescription = "Verifies properties presence for a sequence flow outgoing from the element: " + element.getName();
        processDefinitionsTabPage = Optional.ofNullable(processDefinitionsTabPage)
                .orElse(new ProcessDefinitionsTabPage(wd));
        processDefinitionsTabPage.navigateToPage();
        processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        processDefinitionEditorPage.clearCanvas();
        ActiveElement el = processDefinitionEditorPage.dragNDropByCoordinates(element, "450", "240");
        ActiveElement end = processDefinitionEditorPage.dragNDropByCoordinates(ElementOfDesigner.END, "450", "430");
        Link linkBetweenElements = processDefinitionEditorPage.bindElements(el, end);
        ElementPropertiesTab elementTab = processDefinitionEditorPage.getElementPropertiesTab();
        processDefinitionEditorPage.click(linkBetweenElements.getXPath());
        elementTab.verifyPropertiesPresence(InspectorGroup.PROPERTIES, props);
        processDefinitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "US201731: specifyLabelAndLabelPositionForSequenceFlow")
    @Features("Inspector")
    @Stories("US201731")
    @GUID("d38c4779-0ecf-4ec9-babf-b3ac01cc07ba")
    @Bug("")
    public void specifyLabelAndLabelPositionForSequenceFlow() {
        processDefinitionsTabPage = Optional.ofNullable(processDefinitionsTabPage)
                .orElse(new ProcessDefinitionsTabPage(wd));
        processDefinitionsTabPage.navigateToPage();
        processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        ActiveElement startElement = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.START);
        ActiveElement endElement = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.END);
        endElement.setElementPosition(250, 380);
        linkFromStartToEndElement = processDefinitionEditorPage.bindElements(startElement, endElement);
        linkFromStartToEndElementPath = linkFromStartToEndElement.getXPath();
        linkFromStartToEndElement.setLabel("label");
        linkFromStartToEndElement.setLabelPosition(wd, 0.8);
        ProcessPropertiesTab processPropertiesTab = processDefinitionEditorPage.getProcessPropertiesTab();
        processPropertiesTab.switchToTab();
        processPropertiesTab.setProcessName(processName);
        processDefinitionEditorPage.saveProcess().closeProcess();
    }

    @Test(dependsOnMethods = "specifyLabelAndLabelPositionForSequenceFlow", groups = Groups.CATEGORY_FULL, description = "US201731: modifyLabelAndLabelPositionForSequenceFlow")
    @Features("Inspector")
    @Stories("US201731")
    @GUID("56f12871-a27c-4bd7-9198-c1d1febf0d64")
    @Bug("")
    public void modifyLabelAndLabelPositionForSequenceFlow() {
        processDefinitionsTabPage = Optional.ofNullable(processDefinitionsTabPage)
                .orElse(new ProcessDefinitionsTabPage(wd));
        processDefinitionsTabPage.navigateToPage();
        processDefinitionEditorPage = (ProcessDefinitionEditorPage) processDefinitionsTabPage
                .openTheSavedProcess(processName);
        processDefinitionEditorPage.click(linkFromStartToEndElementPath);
        verifyTrue(linkFromStartToEndElement.getLabel().equals("label"));
        processDefinitionEditorPage.verifyTrue(linkFromStartToEndElement.getLabelPosition().equals(String.valueOf(0.8)));
        linkFromStartToEndElement.setLabel("new label");
        linkFromStartToEndElement.setLabelPosition(wd, 0.2);
        linkFromStartToEndElement.setDescription("\n");
        processDefinitionEditorPage.saveProcess();
        processDefinitionsTabPage = processDefinitionEditorPage.closeProcess();
        processDefinitionsTabPage.openTheSavedProcess(processName);
        verifyTrue(linkFromStartToEndElement.getLabel().equals("new label"));
        verifyTrue(linkFromStartToEndElement.getLabelPosition().equals(String.valueOf(0.2)));
        processDefinitionsTabPage.navigateToPage();
    }
}