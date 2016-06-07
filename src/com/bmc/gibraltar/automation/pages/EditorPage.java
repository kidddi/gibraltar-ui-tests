package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.items.element.ElementAction;
import com.bmc.gibraltar.automation.items.tab.Palette;
import com.bmc.gibraltar.automation.items.tab.PaletteForProcess;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;

public abstract class EditorPage extends BaseEditorPage {
    protected String clearCanvas = "xpath=//button[@ng-click='onClear()']";
    protected String canvas = "xpath=//div[@class='paper']/*[contains(@id,'v-')]";
    protected String name = "No Name";
    protected String definitionEditorName = "EditorPage";
    protected String canvasFreeSpace = "xpath=//div[@class='paper']";

    protected EditorPage(WebDriver driver) {
        super(driver);
    }

    public EditorPage(WebDriver driver, String bundle) {
        super(driver, bundle);
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(headerTitle);
    }

    //TODO: need to refactor
    public PaletteForProcess getProcessTab(Palette tab) {
        return new ProcessDefinitionEditorPage(wd).getTab(tab);
    }

    @Step
    public PaletteForProcess switchToTab(Palette tab) {
        PaletteForProcess palette = getProcessTab(tab);
        palette.switchToTab();
        return palette;
    }

    @Step
    public ElementAction[] dragAndDropElements(ElementAction... elements) {
        //TODO: use List instead of varargs
        Arrays.asList(elements).forEach(e -> dragAndDrop(e.getXpathOnPalette(), canvas));
        return elements;
    }

    @Step
    public EditorPage verifyElementsArePresentOnCanvas(ElementAction... elements) {
        //TODO: use List instead of varargs
        Arrays.asList(elements).stream().forEach(e -> verifyTrue(isElementPresent(e.locatorOnCanvas())));
        return this;
    }

    @Step
    public EditorPage clearCanvas() {
        click(clearCanvas);
        confirmModalDialog(true);
        return this;
    }

    public String getHeaderName() {
        return getText(headerTitle);
    }
}