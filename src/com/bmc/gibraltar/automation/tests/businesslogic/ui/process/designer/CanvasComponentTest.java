package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.Halo;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.TaskManagerBaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Optional;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.END;
import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.START;
import static com.bmc.gibraltar.automation.items.element.Halo.*;

public class CanvasComponentTest extends TaskManagerBaseTest {
    private ProcessDefinitionEditorPage processEditor;
    private ProcessDefinitionsTabPage configurePage;

    @DataProvider(name = "haloOfElements")
    public static Object[][] haloOfElements() {
        return new Object[][]{
                new Object[]{ElementOfDesigner.START, new Halo[]{RESIZE, LINK, REMOVE}},
                new Object[]{ElementOfDesigner.SUB_PROCESS, new Halo[]{RESIZE, LINK, REMOVE}},
        };
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "testCanvasPresence")
    @Features("Canvas")
    @Stories("US199211")
    @GUID("dd36dd8b-dc7b-4a75-87ea-7313872c9cfc")
    public void testCanvasPresence() {
        configurePage = Optional.ofNullable(configurePage).orElse(new ProcessDefinitionsTabPage(wd));
        configurePage.navigateToPage();
        processEditor = configurePage.initiateNewProcess();
        processEditor.verifyCanvasPanelPresent();
        configurePage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "testDragNDropToCanvas")
    @Features("Canvas")
    @Stories("US199211")
    @GUID("0f65cbd7-5504-48ed-9c97-83f44ed284cc")
    public void testDragNDropToCanvas() {
        configurePage = Optional.ofNullable(configurePage).orElse(new ProcessDefinitionsTabPage(wd));
        configurePage.navigateToPage();
        processEditor = configurePage.initiateNewProcess();
        ActiveElement startElement = processEditor.dragAndDropFromCentre(ElementOfDesigner.START, 100, 1);
        assertTrue(processEditor.isActiveElementPresentOnCanvas(startElement), "START is not present on canvas");
        ActiveElement endElement = processEditor.dragAndDropFromCentre(ElementOfDesigner.END, 1, 100);
        assertTrue(processEditor.isActiveElementPresentOnCanvas(endElement), "END is not present on canvas");
        ActiveElement createTaskElement = processEditor.dragAndDropFromCentre(ElementOfDesigner.CREATE_TASK, 100, 100);
        assertTrue(processEditor.isActiveElementPresentOnCanvas(createTaskElement), "CREATE_TASK is not present on canvas");
        configurePage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "multiSelection")
    @Features("Canvas")
    @Stories("US200591")
    @GUID("7be35057-9ec1-421b-999e-f5757dfc6d31")
    public void multiSelection() {
        configurePage = Optional.ofNullable(configurePage).orElse(new ProcessDefinitionsTabPage(wd));
        configurePage.navigateToPage();
        processEditor = configurePage.initiateNewProcess();
        processEditor.clearCanvas();
        ActiveElement start = processEditor.dragAndDropFromCentre(START, -300, 0);
        ActiveElement end = processEditor.dragAndDropFromCentre(END, -100, 0);
        ActiveElement end1 = processEditor.dragAndDropFromCentre(END, 200, 200);
        processEditor.bindElements(start, end);
        processEditor.multiselectAllElementsByCoordinates(750, 750, 1, 1)
                .moveMultiselectionTo(end1)
                .clickOnFreeSpaceOnCanvas()
                .moveElement(end1, -400, -400)
                .moveMultiselectionTo(end1)
                .multiselectAllElementsByCtrlClick(end, start)
                .deleteMultiselection();
        configurePage.navigateToPage();
    }

    @Test(dataProvider = "haloOfElements", groups = Groups.CATEGORY_FULL, description = "haloIconsOnCanvas")
    @Features("Canvas")
    @Stories("US199211")
    @GUID("8e455ead-2343-4693-9ad7-98a893876e3e")
    public void haloIconsOnCanvas(ElementOfDesigner element, Halo[] haloOfElement) {
        testDescription = "Verifies that halo: " + haloOfElement + " are present for: " + element.getName();
        configurePage = Optional.ofNullable(configurePage).orElse(new ProcessDefinitionsTabPage(wd));
        configurePage.navigateToPage();
        processEditor = configurePage.initiateNewProcess();
        ActiveElement el = processEditor.dragAndDropToCanvas(element);
        processEditor.verifyCountOfHaloOfElement(el, haloOfElement.length);
        processEditor.verifyHaloForElement(el, haloOfElement);
        configurePage.navigateToPage();
    }
}