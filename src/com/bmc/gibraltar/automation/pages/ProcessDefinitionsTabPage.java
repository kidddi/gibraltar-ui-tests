package com.bmc.gibraltar.automation.pages;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.ActionBar.Action.*;
import static java.lang.String.format;

public class ProcessDefinitionsTabPage extends TaskManagerHomePage {
    private String organizationInfoIconLocator = "css=.organization-info";
    private String tabHeader = "xpath=//h1[text()='Process Definitions']";
    private String ownersLocator = "xpath=//div[@class='ngCellText ng-scope col2 colt2']" +
            "//span[@class='ng-binding']";

    public ProcessDefinitionsTabPage(WebDriver driver) {
        super(driver, "task-manager");
        tabName = "Process Definitions";
    }

    @Override
    public String getPageUrl() {
        return APP_MANAGER_URL + String.format("/app/application/%s/definitions?tab=process-definitions", bundle);
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(tabHeader);
    }

    public String getOrganizationInfoIconLocator() {
        return organizationInfoIconLocator;
    }


    public List<String> getOwnersList() {
        return getListOfWebElementsTextByLocator(ownersLocator);
    }

    /**
     * @return the list of saved process Names,
     * that are present in the 'Configure' tab
     */
    public List<String> getNamesOfSavedProcess() {
        try {
            if (isElementPresent(gridContainer)) {
                return getElements(entityNamesInGridView).stream().map(WebElement::getText).collect(Collectors.toList());
            }
        } catch (NoSuchElementException e) {
            log.warn("! Could not find saved process names on 'Process Definitions' tab ", e);
        }
        return new ArrayList<>();
    }

    @Step
    public ProcessDefinitionEditorPage initiateNewProcess() {
        actionBar(NEW).click();
        waitForElementNotPresent(loadingSpinner, 10);
        log.info("Initiate new process.");
        ProcessDefinitionJsonEditorPage jsonEditor = new ProcessDefinitionJsonEditorPage(wd);
        if (jsonEditor.isPageLoaded()) {
            jsonEditor.clickBtnToggle();
        }
        ProcessDefinitionEditorPage editor = new ProcessDefinitionEditorPage(wd);
        editor.waitForPageLoaded();
        return editor;
    }

    /**
     * Open process by name from table on 'Process Definitions' tab.
     * As a result returns instance of either ProcessDefinitionEditorPage OR ProcessDefinitionJsonEditorPage
     */
    @Step
    public ProcessDesignerViewPage openTheSavedProcess(String processDefName) {
        log.info("Opening the process definition with the name: " + processDefName);
        String processLink = "link=" + processDefName;
        try {
            waitForElementNotPresent(loadingSpinner, 10);
            waitForElementPresent(processLink, 10);
            click(processLink);
        } catch (Exception e) {
            log.error("\n Cannot find the process definition with the name: " + processDefName, e);
        }
        waitForElementNotPresent(loadingSpinner, 10);
        if (waitForElementPresent(ProcessDefinitionEditorPage.designerEditorContainer, 10)) {
            return new ProcessDefinitionEditorPage(wd);
        } else {
            return new ProcessDefinitionJsonEditorPage(wd);
        }
    }

    /**
     * Opens any (actually first in table) process definition
     */
    @Step
    public ProcessDesignerViewPage openAnySavedProcess() {
        return openTheSavedProcess(getNamesOfSavedProcess().get(0));
    }

    @Step
    public ProcessDefinitionsTabPage refreshProcessDefinitionsList() {
        log.info("Refreshing the list of existing process definitions.");
        actionBar(REFRESH).click();
        waitForElementNotPresent(loadingSpinner, 10);
        return this;
    }

    /* NEXT METHODS ARE RELATED TO PROCESS DEFINITION SELECTION OPERATIONS */

    @Step
    public ProcessDefinitionsTabPage selectProcessDefinitions(String[] processDefinitionNames) {
        log.info("Selecting of " + Arrays.asList(processDefinitionNames) + "].");
        selectionOfProcessDef("teal __icon-circle_o", "__icon-check_circle", processDefinitionNames);
        return this;
    }

    @Step
    public ProcessDefinitionsTabPage selectProcessDefinition(String processDefinitionName) {
        log.info("Selecting of " + processDefinitionName + "].");
        selectionOfProcessDef("teal __icon-circle_o", "__icon-check_circle", processDefinitionName);
        return this;
    }

    @Step
    public ProcessDefinitionsTabPage deselectProcessDefinitions(String[] processDefinitionNames) {
        log.info("Deselecting of " + Arrays.asList(processDefinitionNames) + "].");
        selectionOfProcessDef("__icon-check_circle", "teal __icon-circle_o", processDefinitionNames);
        return this;
    }

    @Step
    public ProcessDefinitionsTabPage deselectProcessDefinition(String processDefinitionName) {
        log.info("Deselecting of " + processDefinitionName + "].");
        selectionOfProcessDef("__icon-check_circle", "teal __icon-circle_o", processDefinitionName);
        return this;
    }

    private void selectionOfProcessDef(String classPart1, String classPart2, String[] processDefinitionNames) {
        for (String processDefinition : processDefinitionNames) {
            selectionOfProcessDef(classPart1, classPart2, processDefinition);
        }
    }

    private void selectionOfProcessDef(String classPart1, String classPart2, String processDefinition) {
        String checkboxOfProcessDefinition = String.format(checkboxForRow, processDefinition);
        if (getAttr(checkboxOfProcessDefinition, "class").contains(classPart1)) {
            click(checkboxOfProcessDefinition);
        }
        verifyTrue(getAttr(checkboxOfProcessDefinition, "class").contains(classPart2));
    }

    @Step
    public void verifyProcessDefinitionsAreSelected(String[] processDefinitionNames) {
        for (String process : processDefinitionNames) {
            log.info("Verifying if the '" + process + "' process definitions is selected.");
            String checkboxOfProcessDefinition = String.format(checkboxForRow, process);
            verifyTrue(getAttr(checkboxOfProcessDefinition, "class").contains("__icon-check_circle"));
        }
    }

    @Step
    public ProcessDefinitionsTabPage selectAllProcessDefinitions() {
        log.info("Selecting all process definitions.");
        if (!getAttr(checkboxForAllRows, "class").contains("__icon-check_circle")) {
            log.info("All process definitions are selected.");
            click(checkboxForAllRows);
        }
        return this;
    }

    /* NEXT METHODS ARE RELATED TO DELETE OPERATIONS */

    @Step
    public ProcessDefinitionsTabPage clickOnDeleteButton() {
        actionBar(DELETE).click();
        verifyTrue(isModalDialogPresent());
        return this;
    }

    private boolean isDeleteButtonEnabled() {
        return actionBar(DELETE).isActionEnabled();
    }

    @Step
    public ProcessDefinitionsTabPage verifyDeleteButtonDisabled() {
        log.info("Verifying if the 'delete' button is disabled.");
        verifyFalse(isDeleteButtonEnabled());
        return this;
    }

    @Step
    public ProcessDefinitionsTabPage verifyDeleteButtonEnabled() {
        log.info("Verifying if the 'delete' button is enabled.");
        verifyTrue(isDeleteButtonEnabled());
        return this;
    }

    @Step
    public ProcessDefinitionsTabPage deleteProcessDefinition(boolean confirmDeletion, String processDefinitionsName) {
        selectProcessDefinition(processDefinitionsName);
        clickOnDeleteButton();
        confirmModalDialog(confirmDeletion);
        log.info("Should these process definitions: [" + processDefinitionsName + "] be deleted? -" + confirmDeletion);
        waitForElementNotPresent(loadingSpinner, 5);
        return this;
    }

    /**
     * Selects all process definition at once using checkbox for all (near "Process Definition Name" column name)
     *
     * @param confirmDeletion true - to delete, false - to decline deletion
     * @return current ProcessDefinitionsTabPage
     */
    @Step
    public ProcessDefinitionsTabPage deleteAllProcessDefinitions(boolean confirmDeletion) {
        selectAllProcessDefinitions();
        clickOnDeleteButton();
        confirmModalDialog(confirmDeletion);
        log.info("Are all process definitions deleted? -" + confirmDeletion);
        sleep(3);
        return this;
    }

    /* NEXT METHODS ARE RELATED TO COPY OPERATIONS */

    @Step
    public ProcessDefinitionsTabPage verifyCopyButtonPresent(boolean isButtonShouldBePresent) {
        verifyEquals(actionBar(COPY).isActionPresent(), isButtonShouldBePresent);
        return this;
    }

    private boolean isCopyButtonEnabled() {
        return actionBar(COPY).isActionEnabled();
    }

    @Step
    public ProcessDefinitionsTabPage verifyCopyButtonDisabled() {
        log.info("Verifying if the 'copy' button is disabled.");
        verifyFalse(isCopyButtonEnabled());
        return this;
    }

    @Step
    public ProcessDefinitionsTabPage verifyCopyButtonEnabled() {
        log.info("Verifying if the 'copy' button is enabled.");
        verifyTrue(isCopyButtonEnabled());
        return this;
    }

    @Step
    public ProcessDefinitionEditorPage openEditorToCopyProcessDefinition(String processDefName) {
        selectProcessDefinitions(new String[]{processDefName});
        log.info("Copying of the [" + processDefName + "].");
        actionBar(COPY).click();
        ProcessDefinitionEditorPage editorPage = new ProcessDefinitionEditorPage(wd);
        editorPage.waitForPageLoaded();
        return editorPage;
    }

    @Step
    public void verifyProcessDoesNotExist(String processName) {
        verifyFalse(isRowExistent(processName),
                format("The Process Definition %s is present, but shouldn't", processName));
    }

    @Step
    public void verifyProcessExists(String processName) {
        verifyTrue(isRowExistent(processName),
                format("The Process Definition %s is NOT present", processName));
    }
}