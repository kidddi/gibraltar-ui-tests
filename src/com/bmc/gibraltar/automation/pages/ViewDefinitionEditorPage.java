package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.framework.utils.PropertiesUtils;
import com.bmc.gibraltar.automation.framework.utils.web.AngularJs;
import com.bmc.gibraltar.automation.framework.utils.web.Browser;
import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import com.bmc.gibraltar.automation.items.component.ActiveComponent;
import com.bmc.gibraltar.automation.items.component.CheckboxExpressions;
import com.bmc.gibraltar.automation.items.component.Component;
import com.bmc.gibraltar.automation.items.record.FormField;
import com.bmc.gibraltar.automation.items.tab.Palette;
import com.bmc.gibraltar.automation.items.tab.PaletteForView;
import com.bmc.gibraltar.automation.items.tab.ValidationIssuesTab;
import com.bmc.gibraltar.automation.items.tab.ViewDesignerInspectorTabs;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public class ViewDefinitionEditorPage extends EditorPage implements HasValidator {
    private String btnToRunTimeMode = "xpath=//button[@ng-click='showRuntimeMode()']";
    private String modalContent = "xpath=//div[@class='modal-content']";
    private String btnOk = "xpath=//button[.='OK']";
    private String parameterForRuntime = "//label[span[.='%s']]//input";
    private String canvasFreeSpace = "xpath=//ul[@dnd-list='layoutDescriptor.children']";
    private String recordEditorFreeSpace = "//div[@layout-descriptor='recordInstanceEditorLayout']//ul[@dnd" +
            "-list='layoutDescriptor.children']";
    private String viewInfoTab = "xpath=//a[@tab-name='rightBladeConfiguration.tabs.viewInformation.name']";
    private String componentInfoTab = "xpath=//a[@tab-name='rightBladeConfiguration.tabs.componentInformation.name']";
    private String fieldsOnCanvas = "//li//li";
    private String fieldLabelOnCanvas = "//div[@class='rx-view-designer-item-content']//label//span[@class='d" +
            "-textfield__item ng-binding']";
    private String fieldOnCanvasWithSpecificLabel = fieldLabelOnCanvas + "[text()='%s:']";
    private String componentHeaderOnCanvas = "%s//h2";
    private String fldProcessName = "xpath=//div[@data-field='rxData/name']//input";
    private String undo = "xpath=//div[@class='icon __icon-undo']";
    private String redo = "xpath=//div[@class='icon __icon-share']";
    private String recordInstanceId = "xpath=//input[@data-attribute = 'rxData/recordInstanceId']";
    private String deleteFieldIcon = "//div[@class='rx-view-designer-item-halo']//span[@ng-click='onDeleteItemClick" +
            "($event)']";
    private String haloDelete = "//div[contains(concat(' ', @class, ' '), ' rx-view-designer-item ') " +
            "and .//div[contains(@class, 'content')]//span[text()='%s:']]//span[contains(@ng-click," +
            "'onDeleteItemClick')]";
    private String componentsOnCanvas = "(//div[@class='rx-view-designer-item ng-isolate-scope']//ul)";

    @FindBy(css = "select[data-attribute='rxData/recordDefinitionName']")
    private WebElement recordDefinitionNameSelect;

    @FindBy(css = "h2")
    private List<WebElement> componentHeader;

    @FindBy(css = "[tab-name='rightBladeConfiguration.tabs.validationIssues.name']")
    private WebElement validationIssuesIcon;

    @FindBy(className = "rx-core-editor-header__title")
    private WebElement headerTitle;

    @FindBy(css = "select[data-attribute='rxData/fieldId']")
    private WebElement recordDefinitionFieldNameSelect;

    @FindBy(css = "[data-field='rxData/disabled'] input[type='checkbox']")
    private WebElement disabledCheckbox;

    @FindBy(css = "[data-field='rxData/hidden'] input[type='checkbox']")
    private WebElement hiddenCheckbox;

    @FindBy(css = "[data-field='rxData/disabled'] select")
    private WebElement disabledCheckboxDropDown;

    @FindBy(css = "[data-field='rxData/hidden'] select")
    private WebElement hiddenCheckboxDropDown;

    @FindBy(css = "input[data-attribute='rxData/disabled']")
    private WebElement disabledCondition;

    @FindBy(css = "input[data-attribute='rxData/hidden']")
    private WebElement hiddenCondition;

    protected ViewDefinitionEditorPage(WebDriver driver) {
        super(driver, "task-manager");
    }

    public ViewDefinitionEditorPage(WebDriver driver, String bundle) {
        super(driver, bundle);
    }

    @Override
    protected void checkSuccessMessageWhileSavingDisplayed() {
        assertTrue(isSucessMessageDisplayed("View Definition saved successfully."));
    }

    @Override
    public String getPageUrl() {
        return APP_MANAGER_URL + format("/app/application/%s/view/new", bundle);
    }

    @Override
    public boolean isPageLoaded() {
        return WebUtils.isElementDisplayed(headerTitle);
    }

    @Step
    public PaletteForView toPalette(Palette tab) {
        return new PaletteForView(wd, this, tab.getTabLink(), tab.getTabPath());
    }

    @Step
    public ViewDesignerInspectorTabs toInspectorTab(ViewDesignerInspectorTabs.Tab tab) {
        ViewDesignerInspectorTabs inspectorTab = new ViewDesignerInspectorTabs(wd, this, tab);
        inspectorTab.switchToTab();
        return inspectorTab;
    }

    public String getViewDefinitionName() {
        return headerTitle.getText();
    }

    @Step
    public ViewDefinitionEditorPage setViewDefinitionName(String name) {
        log.info("Setting View Definition name to '" + name + "'");
        goToViewInfoTab();
        typeKeysWithEnter(fldProcessName, name);
        return this;
    }

    @Step
    public ViewDefinitionsPage close() {
        log.info("Closing the View Designer.");
        click(closeButton);
        return new ViewDefinitionsPage(wd);
    }

    /**
     * Finds desired {@code component}, that is present (last dropped, if more than one with the same type of component
     * were added )
     * on Canvas of View Designer, and based on that creates an instance of ActiveComponent
     *
     * @param component
     * @return ActiveComponent
     */
    public ActiveComponent getDroppedComponent(Component component) {
        try {
            WebElement lastComponentAmongSimilarOnCanvas = getElement(component.getLocatorOnCanvas());
            String viewComponentId = lastComponentAmongSimilarOnCanvas.getAttribute("rx-view-component-id");
            return new ActiveComponent(component, viewComponentId, this);
        } catch (Exception e) {
            fail("\n Could NOT get generated rx-view-component-id... on Canvas for Active Component: " + component
                    .getName() + "\n" + e);
            return null;
        }
    }

    /**
     * Clicks on Component present on Canvas
     *
     * @param component Active Component
     * @return the same instance
     */
    @Step
    public ViewDefinitionEditorPage clickOnComponent(ActiveComponent component) {
        click(component.getXPath());
        return this;
    }

    @Step
    public List<String> getFieldsOnCanvas(Component component) {
        List<WebElement> fields = getElements(component.getLocatorOnCanvas() + fieldLabelOnCanvas);
        return fields.stream().map(field -> getText(field).replaceAll(":", "")).collect(Collectors.toList());
    }

    @Step
    public ViewDefinitionEditorPage saveViewDefinition() {
        //Click to scroll to the element cause it is hidden
        click(saveButton);
        //Actual click
        click(saveButton);
        return this;
    }

    @Step
    public ViewRunTimeModePage goToRunTimeModeWithParams(String paramName, String paramValue) {
        log.info("Opening a run time mode.");
        click(btnToRunTimeMode);
        waitForElementPresent(modalContent, 5);
        specifyRuntimeParams(paramName, paramValue);
        click(btnOk);
        String winHandleBefore = wd.getWindowHandle();
        for (String winHandle : wd.getWindowHandles()) {
            wd.switchTo().window(winHandle);
        }
        ViewRunTimeModePage runTimePage = new ViewRunTimeModePage(wd, winHandleBefore);
        runTimePage.waitForPageLoaded();
        return runTimePage;
    }

    @Step
    public ViewRunTimeModePage openPreviewPage() {
        click(btnToRunTimeMode);
        return new ViewRunTimeModePage(wd);
    }

    private void specifyRuntimeParams(String paramName, String paramValue) {
        String paramPath = modalContent + format(parameterForRuntime, paramName);
        typeKeys(paramPath, paramValue);
    }

    public List<Component> getComponentsOnCanvas() {
        return Arrays.asList(Component.values()).stream().filter(c ->
                isElementPresent(c.getLocatorOnCanvas())).collect(Collectors.toList());
    }

    public String getComponentHeaderOnCanvas(Component component) {
        return getText(format(componentHeaderOnCanvas, component.getLocatorOnCanvas()));
    }

    @Step
    public ViewDefinitionEditorPage dragAndDropComponentToCanvas(Component component) {
        log.info("Dragging '" + component.getName() + "' component to canvas");
        WebElement from = getElement(component.getXpathOnPalette());
        WebElement to = getElement(canvasFreeSpace);
        WebUtils.dragAndDrop(wd, from, to);
        return this;
    }

    /**
     * @param editorIndex Record Instance Editor position. 0 is first
     * @param fields      fields first passed field will be at the bottom of Record Instance Editor
     */
    @Step
    public ViewDefinitionEditorPage dragAndDropFieldsToSelectedEditor(int editorIndex, FormField... fields) {
        Stream.of(fields).forEach(field -> {
            int index = editorIndex + 1;
            log.info(format("Dragging '%s' field to editor with index '%s'", field.getName(), editorIndex));
            WebElement from = wd.findElement(By.xpath(field.getXpathOnPalette()));
            WebElement to = wd.findElement(By.xpath(componentsOnCanvas + "[" + index + "]"))
                    .findElement(By.xpath(recordEditorFreeSpace));
            WebUtils.dragAndDrop(wd, from, to);
        });
        return this;
    }

    /**
     * To use if there is only one Record Instance Editor
     *
     * @param fields first passed field will be at the bottom of Record Instance Editor
     */
    @Step
    public ViewDefinitionEditorPage dragAndDropFieldsToSelectedEditor(FormField... fields) {
        dragAndDropFieldsToSelectedEditor(0, fields);
        return this;
    }

    @Step
    public ViewDefinitionEditorPage moveField(int editorIndex, int indexFrom, int indexTo) {
        WebElement fromE = getFieldInRecordEditor(editorIndex, indexFrom);
        WebElement toE = getFieldInRecordEditor(editorIndex, indexTo);
        fromE.click();
        WebUtils.dragAndDrop(wd, fromE, toE);
        return this;
    }

    /**
     * To use if only one Record Editor is present
     */
    @Step
    public ViewDefinitionEditorPage moveField(int indexFrom, int indexTo) {
        moveField(0, indexFrom, indexTo);
        return this;
    }

    @Step
    public String getFieldName(int editorIndex, int fieldIndex) {
        WebElement selectedField = getFieldInRecordEditor(editorIndex, fieldIndex);
        selectedField.click();
        return wd.findElements(By.xpath(fieldsOnCanvas + FormField.getNameXpathOnCanvas())).get(fieldIndex)
                .getText();
    }

    /**
     * To use if only one Record Editor is present
     */
    @Step
    public String getFieldName(int fieldIndex) {
        return getFieldName(0, fieldIndex);
    }

    @Step
    public ViewDefinitionEditorPage setRecordInstanceId(String id) {
        log.info("Setting Record Instance Id to '" + id + "'");
        goToComponentInfoTab();
        typeKeysWithEnter(recordInstanceId, id);
        return this;
    }

    @Step
    public ViewDefinitionEditorPage goToViewInfoTab() {
        click(viewInfoTab);
        return this;
    }

    @Step
    public ViewDefinitionEditorPage goToComponentInfoTab() {
        click(componentInfoTab);
        return this;
    }

    @Step
    public ValidationIssuesTab goToValidationIssuesTab() {
        validationIssuesIcon.click();
        return new ValidationIssuesTab(wd, this);
    }

    /**
     * Sets Record Definition for selected component
     */
    @Step
    public ViewDefinitionEditorPage setRecordDefinitionName(String name) {
        log.info("Setting '" + name + "' record definition");
        goToComponentInfoTab();
        return selectValueInDropDown(recordDefinitionNameSelect, name);
    }

    @Step
    public ViewDefinitionEditorPage setRecordDefinitionFieldName(String name) {
        log.info("Setting '" + name + "' record definition field");
        return selectValueInDropDown(recordDefinitionFieldNameSelect, name);
    }

    private ViewDefinitionEditorPage selectValueInDropDown(WebElement selectElement, String name) {
        AngularJs.waitForAngularRequestsToFinish(wd);
        if (PropertiesUtils.getBrowser() == Browser.GC) {
            new Select(selectElement).selectByVisibleText(name);
        } else {
            WebUtils.selectDropdownValue(selectElement, name);
        }
        return this;
    }

    @Step
    public List<String> getAvailableRecordDefinitionNames() {
        return getAvailableValuesFromDropDown(recordDefinitionNameSelect);
    }

    @Step
    public List<String> getAvailableRecordDefinitionFieldNames() {
        return getAvailableValuesFromDropDown(recordDefinitionFieldNameSelect);
    }

    private List<String> getAvailableValuesFromDropDown(WebElement dropDownField) {
        goToComponentInfoTab();
        AngularJs.waitForAngularRequestsToFinish(wd);
        Select dropdown = new Select(dropDownField);
        return dropdown.getOptions().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    @Step
    public ViewDefinitionEditorPage clickUndo() {
        click(undo);
        return this;
    }

    @Step
    public ViewDefinitionEditorPage clickRedo() {
        click(redo);
        return this;
    }

    //TODO: investigate this method, how it'll work when 2 or more RIE components are on Canvas
    @Step
    public ViewDefinitionEditorPage removeFieldFromCanvas(String fieldName) {
        WarningDialog warningDialog = clickOnRemoveFieldSign(fieldName);
        warningDialog.clickOkButton();
        return this;
    }

    @Step
    public WarningDialog clickOnRemoveFieldSign(String fieldName) {
        click(Component.RECORD_INSTANCE_EDITOR.getLocatorOnCanvas() + format(fieldOnCanvasWithSpecificLabel,
                fieldName));
        click(Component.RECORD_INSTANCE_EDITOR.getLocatorOnCanvas() + format(haloDelete, fieldName));
        return new WarningDialog<>(wd, this);
    }

    @Step
    public ViewDefinitionEditorPage removeFieldFromRecordEditor(int editorIndex, int fieldIndex) {
        selectFieldInRecordEditor(editorIndex, fieldIndex);
        remove();
        return this;
    }

    /**
     * To use if only one Record Editor is present
     */
    @Step
    public ViewDefinitionEditorPage removeFieldFromRecordEditor(int fieldIndex) {
        removeFieldFromRecordEditor(0, fieldIndex);
        return this;
    }

    /**
     * @param recordEditorIndex Record Instance Editor index on canvas. 0 is first
     */
    @Step
    public ViewDefinitionEditorPage removeRecordEditor(int recordEditorIndex) {
        selectRecordEditorOnCanvas(recordEditorIndex);
        remove();
        return this;
    }

    @Step
    public ViewDefinitionEditorPage removeFirstRecordEditor() {
        return removeRecordEditor(0);
    }

    private void remove() {
        new WebDriverWait(wd, LONG_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(deleteFieldIcon)))
                .click();
        new WarningDialog<>(wd, this).clickOkButton();
    }

    private WebElement getFieldInRecordEditor(int editorIndex, int fieldIndex) {
        return getFields(editorIndex).get(fieldIndex);
    }

    private List<WebElement> getFields(int editorIndex) {
        int index = editorIndex + 1;
        String fieldsLocator = componentsOnCanvas + "[" + index + "]" + fieldLabelOnCanvas;
        return wd.findElements(By.xpath(fieldsLocator));
    }

    @Step
    public int getNumberOfFields(int editorIndex) {
        return getFields(editorIndex).size();
    }

    /**
     * To use if there is only one Record Editor on the canvas
     */
    @Step
    public int getNumberOfFields() {
        return getNumberOfFields(0);
    }

    @Step
    public ViewDefinitionEditorPage selectFieldInRecordEditor(int editorIndex, int fieldIndex) {
        getFieldInRecordEditor(editorIndex, fieldIndex).click();
        return this;
    }

    @Step
    public ViewDefinitionEditorPage selectComponentOnCanvas(Component component) {
        click(component.getHeaderLocatorOnCanvas());
        return this;
    }

    @Step
    public List<String> getComponentNames() {
        return componentHeader.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public int getNumberOfComponentsOnCanvas() {
        return wd.findElements(By.xpath(componentsOnCanvas)).size();
    }

    /**
     * @param recordEditorIndex Record Instance Editor index on canvas. 0 is first
     */
    @Step
    public ViewDefinitionEditorPage selectRecordEditorOnCanvas(int recordEditorIndex) {
        AngularJs.waitForAngularRequestsToFinish(wd);
        componentHeader.get(recordEditorIndex).click();
        return this;
    }

    /**
     * To use if there is only one Record Editor on canvas
     */
    @Step
    public ViewDefinitionEditorPage selectFirstRecordEditorOnCanvas() {
        return selectRecordEditorOnCanvas(0);
    }
    // TODO: add method to select component on Canvas when view contains two or more similar components

    @Step
    public boolean getDisabledCheckboxState() {
        boolean disabledCheckboxState = disabledCheckbox.isSelected();
        if (disabledCheckboxState == false)
            assertTrue(!disabledCondition.isDisplayed() && !disabledCheckboxDropDown.isDisplayed());
        return disabledCheckboxState;
    }

    @Step
    public CheckboxExpressions getDisabledExpression() {
        return CheckboxExpressions.valueOf(getSelectedOption(disabledCheckboxDropDown));
    }

    @Step
    public ViewDefinitionEditorPage setDisabledExpression(CheckboxExpressions expression) {
        return selectValueInDropDown(disabledCheckboxDropDown, expression.getExpression());
    }

    @Step
    public String getDisabledCondition() {
        return getValue(disabledCondition);
    }

    @Step
    public ViewDefinitionEditorPage setDisabledCondition(String condition) {
        typeKeys(disabledCondition, condition);
        return this;
    }

    @Step
    public boolean getHiddenCheckboxState() {
        boolean hiddenCheckboxState = hiddenCheckbox.isSelected();
        if (hiddenCheckboxState == false)
            assertTrue(!hiddenCondition.isDisplayed() && !hiddenCheckboxDropDown.isDisplayed());
        return hiddenCheckboxState;
    }

    @Step
    public CheckboxExpressions getHiddenExpression() {
        return CheckboxExpressions.valueOf(getSelectedOption(hiddenCheckboxDropDown));
    }

    @Step
    public ViewDefinitionEditorPage setHiddenExpression(CheckboxExpressions expression) {
        return selectValueInDropDown(hiddenCheckboxDropDown, expression.getExpression());
    }

    @Step
    public String getHiddenCondition() {
        return getValue(hiddenCondition);
    }

    @Step
    public ViewDefinitionEditorPage setHiddenCondition(String condition) {
        typeKeys(hiddenCondition, condition);
        return this;
    }

    @Step
    public ViewDefinitionEditorPage setDisabledCheckbox(boolean isDisabled) {
        if (disabledCheckbox.isSelected() != isDisabled)
            disabledCheckbox.click();
        return this;
    }

    @Step
    public ViewDefinitionEditorPage setHiddenCheckbox(boolean isHidden) {
        if (hiddenCheckbox.isSelected() != isHidden)
            hiddenCheckbox.click();
        return this;
    }

    private String getSelectedOption(WebElement dropDownField) {
        return new Select(dropDownField).getFirstSelectedOption().getText();
    }
}
