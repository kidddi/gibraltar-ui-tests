package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.*;
import java.util.stream.Collectors;

public class StartProcessPage extends BasePage {
    private String btnStart = "xpath=//button[@ng-click='startProcess()']";
    private String procDefinitionDropDown = "xpath=//select[contains(@ng-model,'selectedProcessDefinition')]";
    private String allInputs = "xpath=//div[@class='rx-property-editor ng-scope']/*";
    private String concreteInput = "xpath=//label[text() ='%s']/.." + allInputs.substring(6);
    private String allInputsNames = allInputs + "/../../label";
    private String requiredInputsNames = allInputs + "[@required='required']/../../label";
    private String processInputField = "xpath=//label[text() ='%s']/../div//input";
    private String processPreview = "xpath=//div[contains(@class, 'tms-process-preview')]";
    private String processInputSection = "xpath=//div[contains(@class, 'tms-process-input-params')]";
    private String customDefinitionType = processInputSection + "//div[@class='rx-property-title ng-binding ng-scope']";
    private String canvasForPreview = "//rx-designer-canvas-preview//*[local-name() = 'svg']/*/*";
    private String closeBtn = "xpath=//button[.='Close']";
    private String headerTitle = "xpath=//div[contains(@class,'rx-core-editor-header-title-center')]";

    public StartProcessPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Starts the process and assure that app. is redirected to Manage tab
     */
    @Step("Fill InputParam field '{0}' with value '{1}'")
    public StartProcessPage fillInputField(String fieldName, String value) {
        String locator = String.format(processInputField, fieldName);
        typeKeysWithNoEnter(locator, value);
        return this;
    }

    /**
     * Starts the current selected/previewed Process. Waits until next page is loaded
     *
     * @return instance of ManageTabPage
     */
    @Step
    public ManageTabPage startTheProcess() {
        try {
            String processName = getChosenProcessName();
            sleep(2);
            click(btnStart);
            assertTrue(isSucessMessageDisplayed("Process has been started successfully."));
            ManageTabPage manageTabPage = new ManageTabPage(wd);
            manageTabPage.waitForPageLoaded();
            log.info("The process instance for the PD [" + processName + "] was started.");
            return manageTabPage;
        } catch (Throwable e) {
            fail("\n Cannot start a process instance, see screenshot");
            return null;
        }
    }

    @Step
    public void clickStartButton() {
        click(btnStart);
    }

    @Step
    public ManageTabPage closePageOfStartProcess() {
        click(closeBtn);
        return new ManageTabPage(wd);
    }

    @Step
    public String getChosenProcessName() {
        return getElement(procDefinitionDropDown + "/option[@selected]").getText();
    }

    /**
     * Selects the desired Process Definition from the drop down menu
     *
     * @param value
     */
    @Step
    public StartProcessPage selectProcessFromDropDown(String value) {
        try {
            selectDropDown(procDefinitionDropDown, value);
        } catch (ElementNotFoundException e) {
            log.error("Could not select process '" + value + "' among possible options: "
                    + Arrays.toString(getNamesOfProcessesInDropDown().toArray()));
        }
        return this;
    }

    /**
     * @return - list of present Process Definitions to select from the drop down menu
     */
    public List<String> getNamesOfProcessesInDropDown() {
        List<String> allProcNames = new ArrayList<>();
        List<WebElement> allWe = getElements(procDefinitionDropDown + "/option");
        allProcNames.addAll(allWe.stream().map(WebElement::getText).collect(Collectors.toList()));
        return allProcNames;
    }

    /**
     * @return List<String> of required only Process Inputs (fields)
     */
    @Step
    public List<String> getListOfRequiredProcInputs() {
        return getListOfLabels(requiredInputsNames);
    }

    /**
     * @return List<String> of all Process Inputs (fields)
     */
    @Step
    public List<String> getListOfAllProcInputs() {
        return getListOfLabels(allInputsNames);
    }

    /**
     * @return List<String> of all Custom Field Definition (i.e.Person) Parameter names
     */
    @Step
    public List<String> getAllCustomFieldDefinitionLabels() {
        return getListOfLabels(customDefinitionType);
    }

    private List<String> getListOfLabels(String inputsLocator) {
        List<String> labels = new ArrayList<>();
        List<WebElement> allElements = getElements(inputsLocator);
        labels.addAll(allElements.stream().map(WebElement::getText).collect(Collectors.toList()));
        return labels;
    }

    @Deprecated
    public Map<String, DataType> getMapOfRequiredInputs() {
        Map<String, DataType> mapOfInputs = new HashMap<>();
        List<String> reqInputs = getListOfRequiredProcInputs();
        DataType dataType;
        for (String reqInput : reqInputs) {
            dataType = getDataTypeForInput(reqInput);
            mapOfInputs.put(reqInput, dataType);
        }
        return mapOfInputs;
    }

    @Deprecated
    @Step
    public DataType getDataTypeForInput(String input) {
        //TODO: implement, once US200197 finished by dev.(maybe via REST API)
        DataType dataType = null;
        return dataType;
    }

    /**
     * This method will set values for all required Process Inputs (order is mandatory !)
     *
     * @param values array ov values.
     */
    @Deprecated
    @Step
    public void populateRequiredInputs(String[] values) {
        Map<String, DataType> mapOfRequired = getMapOfRequiredInputs();
        int index = 0;
        for (String requiredInput : mapOfRequired.keySet()) {
            setProcessInput(requiredInput, values[index++]);
        }
    }

    /**
     * This method sets the value for any Process Input
     * (now only values in String format, but will investigate others if needed)
     *
     * @param inputName the name of Process Input
     * @param value     in format of string
     */
    @Deprecated(/*//TODO: add also cases for other(non-simple typeKeysWithEnter) data types"*/)
    @Step
    public void setProcessInput(String inputName, String value) {
        String inputLocator = String.format(concreteInput, inputName);
        DataType dt = getDataTypeForInput(inputName);
        switch (dt) {
            default:
                typeKeysWithEnter(inputLocator, value);
                break;
        }
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(headerTitle);
    }

    public boolean isPreviewDisplayed() {
        return isElementPresent(processPreview + canvasForPreview);
    }

    /**
     * @param state true - if the preview should be displayed, false - if not
     * @return
     */
    @Step
    public StartProcessPage verifyProcessPreview(boolean state) {
        boolean isPreviewDisplayed = isPreviewDisplayed();
        log.info("Current preview state is: " + isPreviewDisplayed + " and expected is: " + state);
        verifyEquals(isPreviewDisplayed, state);
        return this;
    }
}
