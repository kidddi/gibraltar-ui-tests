package com.bmc.gibraltar.automation.tests.businesslogic.ui.element;

import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.*;
import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;
import static com.bmc.gibraltar.automation.items.element.Halo.*;
import static com.bmc.gibraltar.automation.items.tab.InspectorGroup.PROPERTIES;
import static java.util.Arrays.asList;


public class TimerTest extends AppManagerBaseTest {
    private ProcessDefinitionEditorPage process;
    private ProcessDefinitionsTabPage processTabPage;

    @BeforeMethod
    public void preconditions() {
        processTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        process = processTabPage.initiateNewProcess();
    }

    @Test
    @Features("P534 - Process Designer")
    @Stories("US208808, US207699")
    public void verifyTimerCanBeAttachedToReceiveTask() {
        ActiveElement receiveTask = process.dragAndDropToCanvas(RECEIVE_TASK);
        ActiveElement timer = process.dragAndDropOnElement(TIMER, receiveTask);
        assertTrue(process.isActiveElementPresentOnCanvas(timer), "TIMER is not present on canvas");
    }

    @Test
    @Features("P534 - Process Designer")
    @Stories("US208808, US207699")
    public void verifyTimerCanBeAttachedToUserTask() {
        ActiveElement userTask = process.dragAndDropToCanvas(USER_TASK);
        ActiveElement timer = process.dragAndDropOnElement(TIMER, userTask);
        assertTrue(process.isActiveElementPresentOnCanvas(timer), "TIMER is not present on canvas");
    }

    @Test
    @Features("P534 - Process Designer")
    @Stories("US208808, US207699")
    public void verifyTimerCanNotBeAttachedToNotValidElementForTimerOrCanvas() {
        ActiveElement timer = process.dragAndDropToCanvas(TIMER);
        assertFalse(process.isActiveElementPresentOnCanvas(timer), "TIMER is present on canvas");
        ActiveElement notValidElementForTimer = process.dragAndDropToCanvas(CREATE_BASECAMP_PROJECT);
        timer = process.dragAndDropOnElement(TIMER, notValidElementForTimer);
        assertFalse(process.isActiveElementPresentOnCanvas(timer), "TIMER is present on canvas");
    }

    @Test
    @Features("P534 - Process Designer")
    @Stories("US208808, US207699")
    public void verifyThatUserTaskIsHighlightedOnHoverTheTimer() {
        ActiveElement userTask = process.dragAndDropToCanvas(USER_TASK);
        assertTrue(process.isActiveElementHighlightedOnHover(TIMER, userTask),
                "User Task is not highlighted after hovering Timer event");
    }

    @Test
    @Features("P534 - Process Designer")
    @Stories("US208808, US207699")
    public void verifyThatReceiveTaskIsHighlightedOnHoverTheTimer() {
        ActiveElement receiveTask = process.dragAndDropToCanvas(RECEIVE_TASK);
        assertTrue(process.isActiveElementHighlightedOnHover(TIMER, receiveTask),
                "Receive Task is not highlighted after hovering Timer event");
    }

    @Test
    @Features("P534 - Process Designer")
    @Stories("US208808, US207699")
    public void verifyThatNotValidElementForTimerIsNotHighlightedOnHoverTheTimer() {
        ActiveElement notValidElementForTimer = process.dragAndDropToCanvas(CREATE_BASECAMP_PROJECT);
        assertFalse(process.isActiveElementHighlightedOnHover(TIMER, notValidElementForTimer),
                "Not Valid Element should not highlighted after hovering Timer event");
    }

    @Test
    @Features("P534 - Process Designer")
    @Stories("US208808, US207699")
    public void verifyTimerHaloActions() {
        ActiveElement userTask = process.dragNDropByCoordinates(USER_TASK, 260, 340);
        ActiveElement receiveTask = process.dragNDropByCoordinates(RECEIVE_TASK, 440, 340);
        ActiveElement timer = process.dragAndDropOnElement(TIMER, userTask);
        verifyFalse(process.isBoundSuccessfully(receiveTask, timer), "The Link from receiveTask to timer was created");
        verifyTrue(process.getAllErrorMessages().contains("Element \"Timer\" cannot have inbound sequence flows."));
        verifyTrue(process.isBoundSuccessfully(timer, receiveTask), "The Link from timer to receiveTask was not created");
        timer.click();
        process.verifyEquals(process.getHaloAroundElement(timer), asList(LINK, REMOVE, UNLINK),
                "Timer event`s options doesn`t match with declared");
    }

    @Test
    @Features("P534 - Process Designer")
    @Stories("US208808, US207699")
    public void verifyInteractionBetweenTimerAndUserAndReceiveTaskElements() {
        ActiveElement userTask = process.dragNDropByCoordinates(USER_TASK, 260, 340);
        ActiveElement receiveTask = process.dragNDropByCoordinates(RECEIVE_TASK, 440, 340);
        ActiveElement timerForUserTask = process.dragAndDropOnElement(TIMER, userTask);
        ActiveElement timerForReceiveTask = process.dragAndDropOnElement(TIMER, receiveTask);
        process.deleteElement(userTask);
        assertFalse(process.isActiveElementPresentOnCanvas(timerForUserTask), "timerForUserTask is present on canvas");
        receiveTask.setProperty(X, "100");
        receiveTask.setProperty(Y, "100");
        verifyTrue(80 < timerForReceiveTask.getX() && timerForReceiveTask.getX() < 120,
                "Timer was not moved with RECEIVE_TASK");
        verifyTrue(80 < timerForReceiveTask.getY() && timerForReceiveTask.getY() < 120,
                "Timer was not moved with RECEIVE_TASK");
    }

    @Test
    @Features("P534 - Process Designer")
    @Stories("US208808, US207699")
    public void verifyTimerElementProperties() {
        ActiveElement userTask = process.dragAndDropToCanvas(USER_TASK);
        ActiveElement timer = process.dragAndDropOnElement(TIMER, userTask);
        timer.click();
        List<String> timerProperties = process.getElementPropertiesTab().getPropertiesList(PROPERTIES);
        process.verifyEquals(timerProperties, asList("Help Text:", "Timer:"));
        timer.ensurePropertyRequired(TIMER_PROPERTY);
    }

    @Test
    @Features("P534 - Process Designer")
    @Stories("US208808, US207699")
    public void verifyThatTimerIsAbleToBeSeededWithinCallActivityAndSubProcessElements() {
        String userTaskProcessName = "userTaskProcess" + RandomStringUtils.randomAlphabetic(4);
        String timerProcess = "TimerIsAbleToBeSeededWithinCallActivityAndSubProcess"
                + RandomStringUtils.randomAlphabetic(4);
        process.setProcessName(userTaskProcessName);
        ActiveElement userTask = process.dragAndDrop(USER_TASK);
        userTask.setPropertiesByDefault();
        process.bindElements(process.getDroppedElement(START), userTask, process.getDroppedElement(END));
        process.saveProcess().closeProcess();
        process = processTabPage.initiateNewProcess();
        process.setProcessName(timerProcess);

        ActiveElement callActivity = process.dragNDropByCoordinates(CALL_ACTIVITY, 530, 130);
        callActivity.setProperty(CALLED_PROCESS, userTaskProcessName);
        ActiveElement subProcess = process.dragNDropByCoordinates(SUB_PROCESS, 480, 470);
        ActiveElement start = process.getDroppedElement(START);
        ActiveElement end = process.getDroppedElement(END);
        process.bindElements(start, callActivity, subProcess, end);

        callActivity.doubleClick();
        userTask = process.getDroppedElement(USER_TASK);
        ActiveElement timerInCallActivity = process.dragAndDropOnElement(TIMER, userTask);
        timerInCallActivity.setPropertyByDefault(TIMER_PROPERTY);
        process.bindElements(timerInCallActivity, process.getDroppedElement(END));
        callActivity.doubleClick();

        subProcess.doubleClick();
        ActiveElement startInSubProcess = process.getDroppedElement(START);
        ActiveElement endInSubProcess = process.getDroppedElement(END);
        subProcess = process.getDroppedElement(SUB_PROCESS);
        subProcess.setWidthAndHeight(300, 300);
        ActiveElement userTaskInSubProcess = process.dragAndDropOnElement(USER_TASK, subProcess);
        userTaskInSubProcess.setPropertiesByDefault();
        process.bindElements(startInSubProcess, userTaskInSubProcess, endInSubProcess);
        ActiveElement timerInCallActivityInSubProcess = process.dragAndDropOnElement(TIMER, userTaskInSubProcess);
        timerInCallActivityInSubProcess.setPropertyByDefault(TIMER_PROPERTY);
        process.bindElements(timerInCallActivityInSubProcess, endInSubProcess);

        process.saveProcess().closeProcess();
        processTabPage.verifyProcessExists(timerProcess);
    }
}