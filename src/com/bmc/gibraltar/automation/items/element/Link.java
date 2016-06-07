package com.bmc.gibraltar.automation.items.element;

import com.bmc.gibraltar.automation.items.tab.InspectorTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import ru.yandex.qatools.allure.annotations.Step;

public class Link extends ActiveElement {
    // TODO: locator should be private
    public static String locator = "xpath=//*[@class='rx SequenceFlow link' or @class='rx TextAnnotationAssociation link']";
    private String conditionField = "xpath=//span[.='Element Information']/..//*[@data-field='rxData/condition']//input";
    private String descriptionField = "xpath=//span[.='Element Information']/..//*[@data-field='rxData/description']//textarea";
    private String labelField = "xpath=//span[.='Element Information']/..//*[@data-field='labels/0/attrs/text/text']//input";
    private String labelPositionSlider = "xpath=//label[contains(text(),'Label Position')]/following-sibling::*";
    private ActiveElement source;
    private ActiveElement target;
    // TODO: XPath should start from lowercase letter
    private String XPath;
    private String id;
    private ProcessDefinitionEditorPage editorPage;
    private InspectorTab inspector;
    private String description;

    public Link(WebDriver driver, ActiveElement source, ActiveElement target, String id, ProcessDefinitionEditorPage page) {
        this.editorPage = page;
        this.id = id;
        this.source = source;
        this.target = target;
        this.XPath = String.format("xpath=//*[@model-id='%s']//*[@class='tool-options']//*[contains(local-name(), 'path')]", id);
        wd = driver;
    }

    public String getCondition() {
        editorPage.click(XPath);
        return editorPage.getElement(conditionField).getAttribute("value");
    }

    public String getModelId() {
        return String.format("xpath=//*[@model-id='%s']", id);
    }

    /**
     * This will return the path to the icon that helps to select element on Canvas.
     *
     * @return
     */
    public String getXPath() {
        return XPath;
    }

    public void setXPath(String XPath) {
        this.XPath = XPath;
    }

    @Step
    public void typeCondition(String condition) {
        editorPage.click(XPath);
        editorPage.typeKeysWithEnter(conditionField, condition);
    }

    public void typeConditionByDefault() {
        typeCondition("${userTask.Status} = \"Assigned\"");
    }

    public void remove() {
        editorPage.click(XPath);
        String removeLocator = XPath + "//*[@class='tool-remove']//*[contains(@transform,'scale')]";
        editorPage.click(removeLocator);
    }

    public String getDescription() {
        editorPage.click(XPath);
        return editorPage.getElement(descriptionField).getAttribute("value");
    }

    public void setDescription(String description) {
        editorPage.click(XPath);
        editorPage.typeKeysWithEnter(descriptionField, description + "\n");
    }

    public ActiveElement getTarget() {
        return target;
    }

    public void setTarget(ActiveElement target) {
        this.target = target;
    }

    public void setSource(ActiveElement source) {
        this.source = source;
    }

    public String getLabel() {
        editorPage.click(XPath);
        return editorPage.getElement(labelField).getAttribute("value");
    }

    public void setLabel(String label) {
        editorPage.click(XPath);
        editorPage.typeKeysWithEnter(labelField, label + "\n");
    }

    public String getLabelPosition() {
        editorPage.click(XPath);
        return editorPage.getElement(labelPositionSlider).getAttribute("value");
    }

    /**
     * Sets the label position via slider
     *
     * @param driver
     * @param value  should be from 0.1 to 0.9
     */
    public void setLabelPosition(WebDriver driver, double value) {
        editorPage.click(XPath);
        Actions builder = new Actions(driver);
        Action dragAndDrop;
        int width = editorPage.getElement(labelPositionSlider).getSize().getWidth();
        if (value > 0.1 && value < 0.5) {
            dragAndDrop = builder.clickAndHold(editorPage.getElement(labelPositionSlider))
                    .moveByOffset(-(width / 2), 0)
                    .moveByOffset((int) (width * value), 0)
                    .release().build();
        } else if (value > 0.4) {
            dragAndDrop = builder.clickAndHold(editorPage.getElement(labelPositionSlider))
                    .moveByOffset(-(width / 2), 0)
                    .moveByOffset((int) ((width / 0.9) * value), 0).
                            release().build();
        } else {
            dragAndDrop = builder.clickAndHold(editorPage.getElement(labelPositionSlider))
                    .moveByOffset(-(width / 2), 0).
                            release().build();
        }
        dragAndDrop.perform();
    }

    @Override
    public ProcessDefinitionEditorPage getPage() {
        return editorPage;
    }
}