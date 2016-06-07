package com.bmc.gibraltar.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.ActionBar.Action.START;

public class ManageTabPage extends TaskManagerHomePage {
    private String pageHeader = "xpath=//h1[text()='Processes']";
    private String slctProcessDefinition = "xpath=//select[@ng-model='selectedProcessDefinitionName']";
    private String selectedProcessDefinition = slctProcessDefinition + "/option[.='%s']";
    private String processInstanceRow = "xpath=//div[@ng-click='row.toggleSelected($event)']";
    private String processInstanceID = "xpath=//a[contains(@ui-sref, 'processInstanceId')]";
    private String loadingSpinner = "xpath=//div[contains(@class, 'cg-busy-animation')]";
    private int beforeStart;
    private int afterStart;
    private String instanceIdLink = "//a[.='%s']";

    public ManageTabPage(WebDriver driver) {
        super(driver);
        tabName = "Manage Processes";
    }

    @Override
    public String getPageUrl() {
        return TASK_MANAGER_URL + "/tms/processes";
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(pageHeader);
    }

    /**
     * Initiates opening 'StartProcess' (with selected process at the moment) page by clicking on Start button.
     *
     * @return StartProcessPage
     */
    @Step
    public StartProcessPage clickStart() {
        StartProcessPage startProcessPage = new StartProcessPage(wd);
        actionBar(START).click();
        startProcessPage.waitForPageLoaded();
        return startProcessPage;
    }

    @Step
    public StartProcessPage openProcessStart(String name) {
        log.info("Open the Start New Process Page for: " + name);
        click(lnkManage);
        selectProcess(name);
        beforeStart = getTasksAmount();
        actionBar(START).click();
        sleepMsec(5000);
        return new StartProcessPage(wd);
    }

    @Step
    // TODO: review usage of this method, remove it
    public ManageTabPage startProcess(String name) {
        new StartProcessPage(wd).clickStartButton();
        waitForPageLoaded();
        sleepMsec(5000);
        selectProcess(name);
        afterStart = getTasksAmount();
        return this;
    }

    @Step
    public int getTasksAmount() {
        log.info("Get count of started processes.");
        return getElements(processInstanceRow, false).size();
    }

    @Step
    public ManageTabPage verifyProcessStart(String name) {
        assertTrue(beforeStart + 1 == afterStart, " The task " + name + " has not started.");
        return this;
    }

    /**
     * If the specified process definition is not selected from a dropdown, this method will select it
     *
     * @param processDefinitionName
     */
    @Step
    public void selectProcess(String processDefinitionName) {
        selectDropDown(getElement(slctProcessDefinition), processDefinitionName);
        refresh();
        waitForElementNotPresent(loadingSpinner, 10);
        waitForElementPresent(String.format(selectedProcessDefinition, processDefinitionName), 10);
    }

    @Step
    public List<String> getListOfProcessDefinitions() {
        Select select = new Select(getElement(slctProcessDefinition));
        List<WebElement> options = select.getOptions();
        return options.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    @Step
    public List<String> getListOfProcesses(String processDefinitionName) {
        selectProcess(processDefinitionName);
        waitForElementPresent(String.format(selectedProcessDefinition, processDefinitionName), 10);
        List<WebElement> processes = getElements(processInstanceID);
        return processes.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    @Step
    public ProcessViewerPage openProcessInstanceById(String processDefinitionName, String instanceId) {
        selectProcess(processDefinitionName);
        return getProcessViewerPage(instanceId);
    }

    @Step
    public ProcessViewerPage openProcessInstanceLatest(String processDefinitionName) {
        selectProcess(processDefinitionName);
        List<String> idsStr = this.getListOfProcesses(processDefinitionName);
        List<Integer> idsInt = idsStr.stream().map(Integer::valueOf).collect(Collectors.toList());
        String latestId = Collections.max(idsInt).toString();
        return getProcessViewerPage(latestId);
    }

    private ProcessViewerPage getProcessViewerPage(String instanceId) {
        String processInstanceId = processInstanceRow + String.format(instanceIdLink, instanceId);
        waitForElementPresent(processInstanceId, 10);
        click(processInstanceId);
        ProcessViewerPage processViewerPage = new ProcessViewerPage(wd);
        processViewerPage.waitForPageLoaded();
        return processViewerPage;
    }

    /**
     * Selects the specified process definition from the dropdown on the 'Manage Processes' page
     * and verifies count of processes
     *
     * @param processDefinitionName that will be selected from the dropdown
     * @param expectedCount         of processes for the selected {@processDefinitionName}
     */
    @Step
    public void verifyCountOfProcesses(String processDefinitionName, int expectedCount) {
        selectProcess(processDefinitionName);
        waitForElementPresent(String.format(selectedProcessDefinition, processDefinitionName), 10);
        int processesCount = getTasksAmount();
        verifyEquals(processesCount, expectedCount, "Expected count of processes doesn't equal to actual.");
    }

    @Step
    public ManageTabPage verifyStartButtonDisabled(String processDefName) {
        verifyFalse(isStartButtonEnabled(processDefName),
                String.format("The 'Start' button is enabled for the process definition %s.", processDefName));
        return this;
    }

    @Step
    public ManageTabPage verifyStartButtonEnabled(String processDefName) {
        verifyTrue(isStartButtonEnabled(processDefName),
                String.format("The 'Start' button is disabled for the process definition %s.", processDefName));
        return this;
    }

    public boolean isStartButtonEnabled(String processDefName) {
        selectProcess(processDefName);
        waitForElementPresent(String.format(selectedProcessDefinition, processDefName), 10);
        return actionBar(START).isActionEnabled();
    }
}