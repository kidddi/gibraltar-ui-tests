package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.Halo;
import com.bmc.gibraltar.automation.items.element.Link;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.parameter.ProcessParameter;
import com.bmc.gibraltar.automation.items.processdesigner.ToolbarItem;
import com.bmc.gibraltar.automation.items.tab.*;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import ru.yandex.qatools.allure.annotations.Step;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class ProcessDefinitionEditorPage extends ProcessDesignerViewPage implements HasValidator {
    public static final String organizationInfoIconLocator = "css=.organization-info";
    public static String designerEditorContainer = "xpath=//div[contains(concat('', @class, ''), 'rx-joint-process-designer')]";
    public static String canvasFreeSpace = "xpath=//div[@class='paper']";
    /* HALO VARIABLES: */
    public static String haloOfActiveElement = canvasFreeSpace + "//div[@class='handles']/div";
    /* MULTI SELECTION VARIABLES: */
    public static String multiselectionBox = "xpath=//div[@class='selection-box'][last()]";
    public static String multiselectionRemover = "xpath= //div[@class='selection-wrapper']//div[@class='handle nw remove']";
    /* PROPERTIES VARIABLES: */
    static public String CURRENT_USER = "Current User";
    static public String ADMINISTRATOR = "Administrator";
    static public String xInput = "xpath=//div[@data-field='position/x']//input[@class='number']";
    static public String yInput = "xpath=//div[@data-field='position/y']//input[@class='number']";
    static public String descriptionField = "xpath=//div[@data-field='rxData/description']//textarea";
    static public String runAs = "xpath=//select[@data-attribute='rxData/runAsUser']";
    static public String fldProcessName = "xpath=//div[@data-field='rxData/name']//input";
    private static String inputOutputAdd = "xpath=//div[contains(h3, '%s')]//button[@class='btn-list-add']";
    private static String inputOutputNameField = "xpath=//div[contains(@data-field, 'rxData/%s/')]//input[contains(@data-attribute, '/name')]";
    private String palettePanelContainer = "xpath=//div[contains(concat(' ', @class, ' '), 'rx-blade-dock-left')]";

    public ProcessDefinitionEditorPage(WebDriver driver) {
        super(driver, "task-manager");
        definitionEditorName = "Process Definition Editor";
    }

    public ProcessDefinitionEditorPage(WebDriver driver, String bundle) {
        super(driver, bundle);
    }

    @Step
    public void verifyDesignerEditorViewEnabled() {
        verifyElementPresent(designerEditorContainer);
    }

    /**
     * Clicks on ActiveElement and waits for 1 sec. to assure all data is loaded to UI
     *
     * @param element ActiveElement
     * @return ProcessDefinitionEditorPage instance
     */
    public ProcessDefinitionEditorPage clickOnElement(ActiveElement element) {
        click(element.getXPath());
        sleep(1);
        return this;
    }

    @Override
    protected void checkSuccessMessageWhileSavingDisplayed() {
        if (getProcessPropertiesTab().isProcessDefinitionEnabled()) {
            assertTrue(isSucessMessageDisplayed("Process Definition saved successfully."));
        }
    }

    /**
     * Easy way to drag and drop elements
     */
    @Step
    public List<ElementOfDesigner> dragAndDrop(ElementOfDesigner... elements) {
        List<ElementOfDesigner> list = new ArrayList<ElementOfDesigner>();
        for (ElementOfDesigner el : elements) {
            dragAndDrop(el.getXpathOnPalette(), canvas);
            list.add(el);
        }
        return list;
    }

    /**
     * Drops the element to canvas and set x, y exactly equal input param = value + canvas center value
     */
    @Step
    public ActiveElement dragAndDropFromCentre(ElementOfDesigner element, int xFromCentre, int yFromCentre) {
        ActiveElement el = dragAndDropToCanvas(element);
        moveElement(el, xFromCentre, yFromCentre);
        return el;
    }

    /**
     * Drops the element to canvas and set position for X, Y (int) accordingly
     */
    @Step
    public ActiveElement dragNDropByCoordinates(ElementOfDesigner element, int x, int y) {
        ActiveElement el = dragAndDropToCanvas(element);
        el.setElementPosition(x, y);
        return el;
    }

    /**
     * Gets element from palette, drags to element on canvas, an drops.
     * Example: Drug and drop Timer event on User Task element. Drug User Task from palette on Sub Process.
     */
    @Step
    public ActiveElement dragAndDropOnElement(ElementOfDesigner elementOnPalette, ActiveElement elementOnCanvas) {
        dragAndDrop(elementOnPalette.getXpathOnPalette(), elementOnCanvas.getXPath());
        return getDroppedElement(elementOnPalette);
    }

    /**
     * Drops the element to canvas and set position for X, Y (String) accordingly
     */
    @Step
    public ActiveElement dragNDropByCoordinates(ElementOfDesigner element, String x, String y) {
        ActiveElement el = dragAndDropToCanvas(element);
        setXY(el, x, y);
        return el;
    }

    /**
     * Drops the element to canvas and set x, y by random in the interval: [100 : 800]
     */
    @Step
    public ActiveElement dragAndDrop(ElementOfDesigner element) {
        int xCoordinate = RandomUtils.nextInt(100, 800 + 1);
        int yCoordinate = RandomUtils.nextInt(100, 800 + 1);
        return dragNDropByCoordinates(element, xCoordinate, yCoordinate);
    }

    /**
     * Drag-n-drop element from the default tab
     *
     * @param element
     * @return
     */
    @Step
    public ActiveElement dragAndDropToCanvas(@NotNull ElementOfDesigner element) {
        return dragAndDropToCanvas(Palette.DEFAULT_TAB, element);
    }

    @Step("Drag-n-drop the {1} element from the {0}.")
    public ActiveElement dragAndDropToCanvas(@NotNull Palette tabName, @NotNull ElementOfDesigner element) {
        String elementXPath = element.getXpathOnPalette(tabName);
        WebElement target = waitForElement(elementXPath);
        WebElement objective = waitForElement(canvasFreeSpace);
        dragAndDrop(target, objective);
        return getDroppedElement(element);
    }

    /**
     * Moves element FROM current position TO a new (by adding coordinates on x, y)
     */
    @Step
    public ProcessDefinitionEditorPage moveElement(ActiveElement element, int x, int y) {
        element.click();
        int getX = new Integer(getAttr(xInput, "value"));
        int getY = new Integer(getAttr(yInput, "value"));
        x += getX;
        y += getY;
        typeKeysWithEnter(xInput, "" + x);
        typeKeysWithEnter(yInput, "" + y);
        return this;
    }

    private void fillProcessFields(String locator, String value) {

        String s = "var newName = \"" + value + "\";\n" +
                "var inputName = '" + locator + "';\n" +
                "$('rx-process-designer > form').scope().selected.process.get('rxData')[inputName] = newName;\n" +
                "$('rx-process-designer > form').scope().selected.process.trigger('change:rxData');";
        executeJS(s);
    }

    public ProcessDefinitionEditorPage typeLabel(String value) {

        String s = "var newName = \"" + value + "\";\n" +
                "$('[data-attribute = \"rxData/label\"]').val(newName);";
        executeJS(s);
        return this;
    }

    /**
     * Selects elements by Ctrl+click in multiselection box. Use moveMultiselectionTo() or deleteMultiselection() methods after.
     *
     * @param elements ActiveElement elements, on canvas, which should be selected
     * @return ProcessDefinitionEditorPage instance
     */

    @Step
    public ProcessDefinitionEditorPage multiselectAllElementsByCtrlClick(ActiveElement... elements) {
        Actions action = new Actions(wd);
        action.keyDown(Keys.CONTROL);
        for (ActiveElement ae : elements)
            action.click(getElement(ae.getXPath()));
        action.perform();
        return this;
    }

    /**
     * Selects elements by Shift+click_move in multiselection box. Warning: While method doing, the cursor is took away.
     *
     * @param xStart, yStart,xEnd, yEnd. Real screen coordinates.
     * @return ProcessDefinitionEditorPage instance
     */
    @Step("User can select elements on canvas by ctrl+mouse_click or shift+mouse_select. Move and delete selected block.")
    public ProcessDefinitionEditorPage multiselectAllElementsByCoordinates(int xStart, int yStart, int xEnd, int yEnd) {
        try {
            Robot robot = new Robot();
            robot.mouseMove(xStart, yStart);
            robot.delay(300);
            robot.keyPress(16);
            robot.delay(300);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.delay(300);
            robot.keyRelease(16);
            robot.delay(300);
            robot.mouseMove(xEnd, yEnd);
            robot.delay(300);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Moved multiselection block to position. Warning: The first - multiselection block must be on canvas.
     *
     * @param position is a target, where should move the multiselection block.
     * @return ProcessDefinitionEditorPage instance
     */
    @Step
    public ProcessDefinitionEditorPage moveMultiselectionTo(ActiveElement position) {
        try {
            dragAndDrop(getElement(multiselectionBox), getElement(position.getXPath()));
        } catch (Exception e) {
        }
        return this;
    }

    /**
     * Deletes multiselection block. Warning: The first - multiselection block must be on canvas.
     *
     * @return ProcessDefinitionEditorPage instance
     */
    @Step
    public ProcessDefinitionEditorPage deleteMultiselection() {
        click(multiselectionRemover);
        return this;
    }

    @Step
    public ProcessDefinitionEditorPage setRunAs(String user) {
        clickOnProcessInformationTab();
        selectDropDown(runAs, user);
        return this;
    }

    @Step
    public ProcessDefinitionEditorPage isPropertyCorrect(String locator, String value) {
        clickOnProcessInformationTab();
        String realValue = getValue(locator);
        assert realValue.equals(value);
        return this;
    }

    @Step
    public String getX() {
        return getAttr(xInput, "value");
    }

    @Step
    public String getY() {
        return getAttr(yInput, "value");
    }

    @Step
    public void setXY(ActiveElement element, String x, String y) {
        clickOnElement(element);
        getElementPropertiesTab().expandAllGroups();
        typeKeysWithEnter(xInput, x);
        typeKeysWithEnter(yInput, y);
    }

    @Step
    public int getElementWidth(ActiveElement element) {
        String path = element.getXPath().substring(element.getXPath().indexOf("=") + 1, element.getXPath().length());
        return getElement("xpath=//div[@class='paper']" + path + "//div").getSize().getWidth();
    }

    @Step
    public int getElementHeight(ActiveElement element) {
        String path = element.getXPath().substring(element.getXPath().indexOf("=") + 1, element.getXPath().length());
        return getElement("xpath=//div[@class='paper']" + path + "//div").getSize().getHeight();
    }

    /**
     * This method gets the element's position on the Canvas (through its style, not the Inspector values)
     *
     * @param element element on the Canvas
     * @return array where first value - element's X coordinate, and the second - Y coordinate
     */
    @Step
    public String[] getElementPosition(ActiveElement element) {
        String path = element.getXPath().substring(element.getXPath().indexOf("=") + 1, element.getXPath().length());
        String coordinates = getElement("xpath=//div[@class='paper']" + path).getAttribute("transform");
        String[] xAndYCoordinates = coordinates.substring(coordinates.indexOf("(") + 1, coordinates.length() - 1).split(",");
        return xAndYCoordinates;
    }

    /**
     * This method verifies if the specified element has expected coordinated on the Canvas
     *
     * @param element             on the Canvas
     * @param expectedXCoordinate
     * @param expectedYCoordinate
     */
    @Step
    public void verifyElementPosition(ActiveElement element, int expectedXCoordinate, int expectedYCoordinate) {
        verifyEquals(getElementPosition(element)[0], String.valueOf(expectedXCoordinate));
        verifyEquals(getElementPosition(element)[1], String.valueOf(expectedYCoordinate));
    }

    @Step
    public void verifyElementSize(ActiveElement elementOnCanvas, int expectedWidth, int expectedHeight) {
        clickOnElement(elementOnCanvas);
        verifyEquals(getElementWidth(elementOnCanvas), expectedWidth);
        verifyEquals(getElementHeight(elementOnCanvas), expectedHeight);
    }

    @Step
    public ProcessDefinitionEditorPage bindElementsFastWay(ActiveElement source, ActiveElement target) {
        click(source.getXPath());
        dragAndDrop(
                getElement(Halo.LINK.getHaloPath()),
                getElement(target.getXPath()));
        return this;
    }

    public List<WebElement> getElementList(String locator) {
        return super.getElements(locator);
    }

    @Step
    public ProcessDefinitionEditorPage bindElements(ActiveElement... elements) {
        log.info("Binding elements.");
        for (int i = 0; i < elements.length - 1; i++) {
            bindElementsFastWay(elements[i], elements[i + 1]);
        }
        return this;
    }

    @Step
    public Link bindElements(ActiveElement source, ActiveElement target) {
        assertTrue(isBoundSuccessfully(source, target), format("Link from %s to %s was not created", source, target));
        String id = getElement(getLast(Link.locator)).getAttribute("model-id");
        return new Link(wd, source, target, id, this);
    }

    @Step
    public boolean isBoundSuccessfully(ActiveElement source, ActiveElement target) {
        List<WebElement> oldLinks = getElementList(Link.locator);
        bindElementsFastWay(source, target);
        List<WebElement> newLinks = getElementList(Link.locator);
        return oldLinks.size() + 1 == newLinks.size();
    }

    public ActiveElement getDroppedElement(ElementOfDesigner element) {
        log.info("Selecting of " + element.getName() + " on Canvas.");
        try {
            WebElement lastElementAmongSimilarOnCanvas = getElement(element.locatorOnCanvas());
            String modelId = lastElementAmongSimilarOnCanvas.getAttribute("model-id");
            return new ActiveElement(wd, element, modelId, this);
        } catch (Exception e) {
            log.warn(
                    "\n Could NOT get generated model-id... on Canvas for ActiveElement: " + element.name(), e);
            return null;
        }
    }

    @Step
    public ProcessDefinitionEditorPage deleteElement(ActiveElement element) {
        log.info("Removing of the " + element + " from the Canvas.");
        element.click();
        click(Halo.REMOVE.getHaloPath());
        return this;
    }

    @Step
    public ActiveElement copyElement(ActiveElement element) {
        clickOnElement(element);
        click(Halo.CLONE.getHaloPath());
        return getDroppedElement(element.getType());
    }

    @Step
    public ProcessDefinitionEditorPage setProcessName(String name) {
        log.info("Process Definition name is [" + name + "].");
        clickOnFreeSpaceOnCanvas();
        waitForElementPresent(fldProcessName, 5);
        typeKeysWithEnter(fldProcessName, name);
        this.name = name;
        return this;
    }

    @Step
    public String getProcessDefName() {
        return getAttr(fldProcessName, "value");
    }

    @Step
    public ProcessDefinitionEditorPage setProcessDescription(String text) {
        typeKeysWithEnter(descriptionField, text);
        return this;
    }

    @Step
    public List<Halo> getHaloAroundElement(ActiveElement elm) {
        log.info("Get halo of the '" + elm.getName() + "' element.");
        List<WebElement> allHalos = getElements(haloOfActiveElement);
        List<Halo> listHaloNames = new ArrayList<>();
        for (WebElement haloElement : allHalos)
            if (haloElement.isDisplayed()) {
                String dataAction = getAttr(haloElement, "data-action");
                listHaloNames.add(Halo.get(dataAction));
            }
        return listHaloNames;
    }

    @Step
    public void verifyHaloForElement(ActiveElement elm, Halo... arrayHaloToVerify) {
        log.info("Verify halo presence for '" + elm.getName() + "' element.");
        List<Halo> listHaloNames = getHaloAroundElement(elm);
        for (Halo haloToVerify : arrayHaloToVerify) {
            verifyTrue(listHaloNames.contains(haloToVerify), "No halo: " + haloToVerify + " is present for element: ");
        }
    }

    @Step
    public void verifyCountOfHaloOfElement(ActiveElement elm, int expectedCountOfHalo) {
        log.info("Verify halo count for '" + elm.getName() + "' element.");
        int actualCountOfHalo = getHaloAroundElement(elm).size();
        verifyEquals(actualCountOfHalo, expectedCountOfHalo, "Expected " + expectedCountOfHalo + ", but found " + actualCountOfHalo + " halo for " + elm.getName());
    }

    @Step
    public ProcessDefinitionEditorPage clickOnFreeSpaceOnCanvas() {
        click(canvasFreeSpace);
        return this;
    }

    @Step
    public ProcessDefinitionEditorPage clickOnProcessInformationTab() {
        getProcessPropertiesTab().switchToTab();
        return this;
    }

    @Step
    public void verifyCanvasPanelPresent() {
        verifyElementPresent(canvasFreeSpace,
                "Page " + getPageName() + " does NOT have Canvas: " + canvasFreeSpace);
    }

    @Step
    public ProcessDefinitionEditorPage waitForProcessSaving() {
        checkIfProcessValid();
        clickOnFreeSpaceOnCanvas();
        waitForElement(fldProcessName + "[@readonly='readonly']", 15, true);
        return this;
    }

    @Step
    public void verifyElementsCountOnCanvas(int countOfElementsOnCanvas) {
        verifyEquals(getListOfElements("xpath=//div[@class='paper']//*[contains(concat('', @class, ''), 'element')]").size(), countOfElementsOnCanvas);
    }

    @Step
    public void verifyCanvasIsEmpty() {
        verifyElementsCountOnCanvas(0);
    }

    @Step
    public boolean isElementPresentOnCanvas(ElementOfDesigner element) {
        return isElementPresent(element.locatorOnCanvas());
    }

    @Step
    public boolean isActiveElementPresentOnCanvas(ActiveElement elm) {
        return isElementPresent(elm.getXPath());
    }

    /* NEXT METHODS RELATE TO CANVAS TOOLBAR */

    @Step
    public void executeToolbarAction(ToolbarItem toolbarItem) {
        log.info(toolbarItem + " option was chosen in the canvas toolbar.");
        click(toolbarItem.getIconPath());
    }

    /**
     * This method allows to execute a specified toolbar action a few times
     *
     * @param toolbarItem     a button on the canvas toolbar
     * @param numberOfActions how much times the specified toolbar item will be executed
     */
    @Step
    public void executeToolbarAction(ToolbarItem toolbarItem, int numberOfActions) {
        for (int i = 0; i < numberOfActions; i++) {
            click(toolbarItem.getIconPath());
        }
    }

    @Step
    public void clearCanvas(boolean confirmClearing) {
        executeToolbarAction(ToolbarItem.CLEAR_CANVAS);
        confirmModalDialog(confirmClearing);
    }

    /**
     * This method verifies that a button on the canvas toolbar is enabled
     *
     * @param toolbarItem Should be: UNDO, REDO,  GRID_SIZE_MINUS,  GRID_SIZE_PLUS
     */
    @Step
    public void verifyButtonOnCanvasToolbarIsEnabled(ToolbarItem toolbarItem) {
        log.info("Verifying if the " + toolbarItem + " is enabled.");
        verifyTrue(null == getElement(toolbarItem.getIconPath()).getAttribute("disabled"));
    }

    /**
     * This method verifies that a button on the canvas toolbar is disabled
     *
     * @param toolbarItem Should be: UNDO, REDO, GRID_SIZE_MINUS, GRID_SIZE_PLUS
     */
    @Step
    public void verifyButtonOnCanvasToolbarIsDisabled(ToolbarItem toolbarItem) {
        log.info("Verifying if the " + toolbarItem + " is disabled.");
        verifyTrue(isElementPresent(toolbarItem.getIconPath() + "[@disabled='disabled']"));
    }

    /* NEXT METHODS RELATE TO INSPECTOR */

    public ElementPropertiesTab getElementPropertiesTab() {
        return new ElementPropertiesTab(wd, this);
    }

    public ProcessPropertiesTab getProcessPropertiesTab() {
        return new ProcessPropertiesTab(wd, this);
    }

    /* NEXT METHODS RELATE TO PALETTE */

    @Step
    public void verifyPalletePanelPresent() {
        verifyElementPresent(palettePanelContainer,
                "Page " + getPageName() + " does NOT have Pallete: " + palettePanelContainer);
    }

    /**
     * This method gets one of the tabs in Palette
     *
     * @param tab can be: Palette.DEFAULT, Palette.FAVORITE, Palette.RECENT or Palette.SETTINGS;
     * @return
     */
    public PaletteForProcess getTab(Palette tab) {
        PaletteForProcess newTab = new PaletteForProcess(wd, this, tab.getTabLink(), tab.getTabPath());
        newTab.setTab(tab);
        return newTab;
    }

    @Step
    public ProcessDefinitionEditorPage isEnabled(String element) {
        String disabled = getAttr(element, "disabled");
        String name = getAttr(element, "text");
        assertEquals(disabled, "", "The element  \"" + name + "\" is Enabled");
        return this;
    }

    @Step
    public ProcessDefinitionEditorPage isDisabled(String element) {
        String disabled = getAttr(element, "disabled");
        String name = getAttr(element, "text");
        assertNotEquals(disabled, "disabled", "The element  \"" + name + "\" is Disabled");
        return this;
    }

    public void selectDropDown(String locator, String option) {
        getSelect(getElement(locator)).selectByVisibleText(option);
    }

    public String getAttr(String locator, String attribute) {
        return super.getAttr(locator, attribute);
    }

    public String getAttr(WebElement locator, String attribute) {
        return super.getAttr(locator, attribute);
    }

    /**
     * Easy adds new Input param with all default (text type). And type name there
     *
     * @param name text into Name: field (for just created Input block)
     * @return name (text)
     */

    @Step
    public String addInputParam(String name) {
        return addInOutPutParam("Input", name);
    }

    /**
     * Easy adds new Output param with all default (text type). And type name there
     *
     * @param name text into Name: field (for just created Output block)
     * @return name (text)
     */
    @Step
    public String addOutputParam(String name) {
        return addInOutPutParam("Output", name);
    }

    private String addInOutPutParam(String type, String name) {
        clickOnProcessInformationTab();
        click(format(inputOutputAdd, type + " Parameters"));
        String nameLocator = getLast(format(inputOutputNameField, type.toLowerCase() + "Params"));
        typeKeysWithEnter(nameLocator, name);
        return name;
    }

    @Step
    public ProcessParameter createParam(String name, DataType dataType) {
        return new ProcessParameter(name, dataType);
    }

    public ProcessPropertiesTab addProcessParam(InspectorGroup type, ProcessParameter param) {
        return getProcessPropertiesTab().addProcessParameter(type, param);
    }

    /**
     * Method drags and holds web element (dragAndHoldOnElement) over the target(holdOnTargetLocator), gets an attribute of target,
     * returns the element on its place and releases.
     * It is useful in case, if some element is changed its properties if to hover on it some other element.
     *
     * @param sourceLocator   a locator of web element, which should to take, move and hold over target web element.
     * @param targetLocator   a locator of web element, which is a target, to hover the first element, over this one.
     * @param targetAttribute if target is changed its properties, type there an attribute of target web element,
     *                        which exactly is changing.
     * @return value of target attribute, in the moment, when first element was holding over the target.
     */
    private String getAttributeOnHover(String sourceLocator, String targetLocator, String targetAttribute) {
        Actions action = new Actions(wd);
        action.clickAndHold(getElement(sourceLocator)).moveToElement(getElement(targetLocator)).perform();
        String targetAttributeValue = getElement(targetLocator).getAttribute(targetAttribute);
        action.moveToElement(getElement(sourceLocator)).release().perform();
        return targetAttributeValue;
    }

    /**
     * @return true, if elementOnCanvas is highlighted in the moment of holding elementFromPalette over elementOnCanvas
     * Example:
     * If Timer hover over User Task or Receive Task elements, they became highlighted.
     * And theirs class contains "highlighted".
     */
    @Step
    public boolean isActiveElementHighlightedOnHover(ElementOfDesigner elementFromPalette,
                                                     ActiveElement elementOnCanvas) {
        return getAttributeOnHover(elementFromPalette.getXpathOnPalette(),
                elementOnCanvas.getXPath(), "class").contains("highlighted");
    }

}