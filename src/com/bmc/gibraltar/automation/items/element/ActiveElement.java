package com.bmc.gibraltar.automation.items.element;

import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import com.bmc.gibraltar.automation.items.CommonEnumInterface;
import com.bmc.gibraltar.automation.items.DesignerElement;
import com.bmc.gibraltar.automation.items.datadictionary.ConditionsEditor;
import com.bmc.gibraltar.automation.items.datadictionary.DataDictionary;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.InspectorTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.*;
import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;
import static com.bmc.gibraltar.automation.items.tab.InspectorGroup.*;

public class ActiveElement extends DesignerElement implements CommonEnumInterface {
    private static String allVisiblePropertiesPath = "xpath=//div[@class='field']//label";
    private static String selectItems = "xpath=//div[contains(@id, 'ui-select-choices-row')]//div[@class='ng-binding ng-scope']";
    private static String parallelIcon = "//*[@class='icon-multi-instance-parallel']";
    private static String sequentialIcon = "//*[@class='icon-multi-instance-sequential']";
    private static String subProcessIcon = "//*[@class='sub-process']";
    private static String iconPath = "//*[@class='icon']";
    protected ProcessDefinitionEditorPage page;
    private InspectorTab inspector;
    // Common fields
    private ContainerProperty name = new ContainerProperty(InspectorGroup.PROPERTIES, "Name:", "1", true);
    private ContainerProperty label = new ContainerProperty(PROPERTIES, "Label:");
    private ContainerProperty x = new ContainerProperty(GEOMETRY, "X:");
    private ContainerProperty y = new ContainerProperty(GEOMETRY, "Y:");
    private ContainerProperty width = new ContainerProperty(GEOMETRY, "Width:");
    private ContainerProperty height = new ContainerProperty(GEOMETRY, "Height:");
    // Exclusive fields
    private Set<ContainerProperty> inputMapFields = new HashSet<>();
    private String xPath;
    private PaletteGroup groupOnPalette;
    private ElementOfDesigner type;
    private String id;
    private Map<Property, String> elementPropertiesByDefault;
    private Map<Property, String> elementProperties = new HashMap<>();
    private List<Property> elementTemporaryProperties;
    private String indexPath = "xpath=//div[@data-field='rxData/outputMap']//div[@class='list-item'][contains(@data-index, '%s')]";

    /**
     * This constructor is for activating element by known element from enum ElementOfDesigner.
     * If more than one such element are present on Canvas, then activated will be only the last dropped to Canvas.
     * Mostly used for first drag-n-drop of element;
     *
     * @param type Specific type of element
     * @param id   Exclusive id (model-id) on canvas. Saved all element`s life. Needs for building XPath
     * @param page Auxiliary variable. Needs for actions from ProcessDefinitionEditorPage
     */
    public ActiveElement(WebDriver driver, ElementOfDesigner type, String id, ProcessDefinitionEditorPage page) {
        this.page = page;
        groupOnPalette = type.getPaletteGroup();
        this.type = type;
        this.id = id;
        buildXPath();
        setName(type.getName());
        buildElementProperties();
        add(LABEL, type.getName());
        wd = driver;
    }

    public ActiveElement() {
        type = START;
    }

    public DataDictionary openDataDictionaryFor(Property property) {
        log.info("Opening the Data Dictionary for " + property);
        this.click();
        return new ConditionsEditor(wd, this, property).open();
    }

    public DataDictionary openDataDictionaryFor(String property) {
        return openDataDictionaryFor(new DynamicProperty(property));
    }

    public ConditionsEditor openConditionsEditorFor(ElementProperties property) {
        log.info("Opening the Conditions Editor for " + property);
        if (property.equals(ElementProperties.COMPLETION_CRITERIA) || property.equals(ElementProperties.CONDITION)
                || property.equals(ElementProperties.COMPLETION_CONDITION)) {
            return (ConditionsEditor) new ConditionsEditor(wd, this, property).open();
        } else {
            log.error("There is no Conditions Editor for " + property + " property.");
            return null;
        }
    }

    public boolean isSequential() {
        return isElementPresent(xPath + sequentialIcon);
    }

    public boolean isSubProcessElement() {
        return isElementPresent(xPath + subProcessIcon);
    }

    public boolean isParallel() {
        return isElementPresent(xPath + parallelIcon);
    }

    /**
     * Assert that the field is required.
     *
     * @property existed field from ElementProperties enum. Warning: must be belong to this element.
     */
    public ActiveElement ensurePropertyRequired(ElementProperties property) {
        verifyTrue(isPropertyRequired(property));
        return this;
    }

    /**
     * Assert that the field is NOT required.
     *
     * @property existed field from ElementProperties enum. Warning: must be belong to this element.
     */
    public ActiveElement ensurePropertyNotRequired(ElementProperties property) {
        assert !isPropertyRequired(property);
        return this;
    }

    private boolean isPropertyRequired(ElementProperties property) {
        page.clickOnElement(this);
        if (property.getGroup() == INPUT_MAP) {
            return isLabelFromInputMapGroupRequired(property.getName());
        } else {
            return isElementPresent(property.getRequiredlocator());
        }
    }

    private boolean isLabelFromInputMapGroupRequired(String field) {
        String jsScript = "(function isRequired () {\n" +
                "    var containerOfInputMapGroup = document.querySelector('div[data-name=\"inputMap\"]');\n" +
                "    var result;\n" +
                "    var list = containerOfInputMapGroup.getElementsByTagName('label');\n" +
                "    for (var i = 0; i < list.length; i++) {\n" +
                "        if (list[i].innerHTML == '" + field + "') {\n" +
                "            result = getComputedStyle(list[i], ':after').content;\n" +
                "        }\n" +
                "    }\n" +
                "   alert(result).dismiss();" +
                "return result;\n" +
                "})();";

        String result = "" + executeJS(jsScript);
        page.alertAcceptIfPresent();
        return result.contains(" *");
    }

    /**
     * Check if current Active Element has halo around exactly it
     *
     * @return
     */
    public boolean isCurrentElementActivated() {
        return !page.getHaloAroundElement(this).isEmpty();
    }

    /**
     * Assert that the field is required.
     *
     * @property existed field from ElementProperties enum. Warning: must be belong to this element.
     */
    public ActiveElement ensurePropertyRequired(String property) {
        page.clickOnElement(this);
        assert isLabelFromInputMapGroupRequired(property);
        return this;
    }

    /**
     * Assert that the field is NOT required.
     *
     * @property existed field from ElementProperties enum. Warning: must be belong to this element.
     */
    public ActiveElement ensurePropertyNotRequired(String property) {
        page.clickOnElement(this);
        assert !isLabelFromInputMapGroupRequired(property);
        return this;
    }

    @Step
    public ActiveElement verifyPropertyHasValue(ElementProperties property, String value) {
        String valueOrigin = getPropertyValue(property);
        verifyEquals(valueOrigin, value);
        return this;
    }

    public String getPropertyValues(String property) {
        page.clickOnElement(this);
        return getValue(ElementProperties.commonLocator(property));
    }

    public String getPropertyValue(Property property) {
        page.clickOnElement(this);
        String value;
        if (property.isAngular()) {
            property.setAngular(false);
            value = getText(property.getLocator() + angularSelect2Toggle);
            property.setAngular(true);
        } else
            value = getValue(property.getLocator());
        return value;
    }

    private boolean dynamicPropertyWorkAround(Property property) {
        // TODO: check if this method can be removed
        if (property instanceof DynamicProperty) {
            if (isElementPresent(property.getFieldNameLocator() + "/input")) {
                ((DynamicProperty) property).locator = property.getFieldNameLocator() + "/input";
                return true;
            }
            if (isElementPresent(property.getFieldNameLocator() + "/div")) {
                ((DynamicProperty) property).locator = property.getFieldNameLocator() + "/div";
                return true;
            }
        }
        return false;
    }

    /**
     * Add value to existed value
     *
     * @param property, which should be supplemented by value from params
     * @param value,    the text, will a
     * @return
     */
    public ActiveElement appendToProperty(String property, String value) {
        String previousValue = this.getPropertyValues(property);
        setProperty(property, previousValue + value);
        return this;
    }

    public ActiveElement appendToProperty(Property property, String value) {
        String previousValue = this.getPropertyValue(property);
        setProperty(property, previousValue + value);
        return this;
    }

    /**
     * Set element`s property by String value.
     *
     * @property existed field from ElementProperties enum. Warning: must be belong to this element.
     * @value is typed text into the property field.
     */
    public ActiveElement setProperty(Property property, String value) {
        log.info(String.format("Setting the [%s] property with [%s] value.", property.getName(), value));
        click();
        return fillProperty(property, value);
    }

    private ActiveElement fillProperty(Property property, String value) {
        if (property instanceof DynamicProperty && dynamicPropertyWorkAround(property)) {
            return this;
        }
        if (property.getButton() != null && property.getButton().startsWith("select") && !property.equals(CALLED_PROCESS)) {
            try {
                WebUtils.selectDropdownValue(getElement(property.getLocator()), value);
            } catch (UnexpectedTagNameException e) {
                page.typeKeysBySure(property.getLocator(), value);
            }
            page.confirmModalDialog(true);
        } else {
            if (property.getButton() != null && property.getButton().equals("add")) {
                click(property.getAddButtonLocator());
            }
            if (property == LABEL)
                page.typeLabel(value);
            else {
                try {
                    if (property.isAngular())
                        page.typeKeysWithEnter(property.getLocator(), value);
                    else
                        page.typeKeysBySure(property.getLocator(), value);
                } catch (ElementNotVisibleException e) {
                    log.info("Property " + property + " has input field as 'Angular select2'.");
                    property.setAngular(true);
                    page.typeKeysWithEnter(property.getLocator(), value);
                }
            }
        }
        add(property, value);
        return this;
    }

    public ActiveElement setWidthAndHeight(int width, int height) {
        clearProperty(WIDTH).fillProperty(WIDTH, "" + width);
        clearProperty(HEIGHT).fillProperty(HEIGHT, "" + height);
        return this;
    }

    public ActiveElement clearProperty(Property property) {
        page.getElement(property.getLocator()).clear();
        return this;
    }

    /**
     * Selects a value in drop-down of 'Angular select2'
     */
    private void selectFromAngularSelect2(Property property, String value) {
        click(property.getLocator() + angularSelect2Toggle);
        try {
            click(property.getLocator() + String.format(angularSelect2Optns, value));
        } catch (Exception e) {
            log.warn("Property '" + property.getName() + "' does NOT have drop down with value: " + value);
            throw e;
        }
    }

    /**
     * Types a value in field of 'Angular select2'
     */
    @Deprecated
    private void typeInAngularSelect2(Property property, String value) {
        click(property.getLocator() + angularSelect2Toggle);
        page.typeKeysWithEnter(property.getLocator() + angularSelect2Input, value);
    }

    public ActiveElement setProperties(Property... properties) {
        elementTemporaryProperties = Arrays.asList(properties);
        return this;
    }

    @Step
    public ActiveElement setElementPosition(int x, int y) {
        log.info("Changing element's position on Canvas.");
        page.getElementPropertiesTab().expandAllGroups();
        setProperty(ElementProperties.X, String.valueOf(x));
        setProperty(ElementProperties.Y, String.valueOf(y));
        return this;
    }

    /**
     * Set element`s property by default value. Warning: USER_TASK: Record Definition == "New Process" by default.
     *
     * @property existed field from ElementProperties enum. Warning: must be belong to this element.
     */
    public ActiveElement setPropertyByDefault(Property property) {
        return setProperty(property, property.getDefault());
    }

    /**
     * Makes one click on this element
     *
     * @return this element
     */
    public ActiveElement click() {
        String elementPath = this.getXPath();
        if (isElementPresent(elementPath + iconPath))
            click(elementPath + iconPath);
        else
            click(elementPath);
        return this;
    }

    public ActiveElement generateSubProcessByDeep(int nesting) {
        doubleClick();
        ActiveElement sub = null;
        setProperties(X, Y, WIDTH, HEIGHT).byValuesTextFields("200", "200", "500", "300");
        for (int i = 0; i < nesting; i++) {
            int length = i + 10;
            if (sub != null)
                sub.setProperties(X, Y, WIDTH, HEIGHT).byValuesTextFields(200 + length + "", 200 + length + "", "500", "300");
            ActiveElement start = page.dragNDropByCoordinates(START, 250 + length, 250 + length);
            ActiveElement end = page.dragNDropByCoordinates(END, 350 + length, 250 + length);
            sub = page.dragNDropByCoordinates(SUB_PROCESS, 300 + length, 300 + length);
            sub.setProperty(LABEL, "SUB_PROCESS_" + i);
            page.bindElements(start, sub, end);
            sub.doubleClick();
        }
        sub.generateSubProcessByDefault();
        doubleClick();
        return this;
    }

    public ActiveElement generateSubProcessByDefault() {
        doubleClick();
        setProperties(X, Y, WIDTH, HEIGHT).byValuesTextFields("200", "200", "500", "300");
        ActiveElement start = page.dragNDropByCoordinates(START, 250, 250);
        ActiveElement end = page.dragNDropByCoordinates(END, 350, 250);
        page.bindElementsFastWay(start, end);
        doubleClick();
        return this;
    }

    public ActiveElement doubleClick() {
        page.doubleClick(this.getXPath());
        return this;
    }

    public ActiveElement setPropertiesByMap() {
        if (type == SUB_PROCESS) {
            generateSubProcessByDefault();
        }
        if (type == CALL_ACTIVITY || type == USER_TASK) {
            page.confirmModalDialog(true);
            ElementProperties prop = type == CALL_ACTIVITY ? CALLED_PROCESS : RECORD_DEFINITION;
            page.selectDropDown(prop.getLocator(), prop.getDefault());
        }
        if (type == USER_TASK) {
            setPropertyByDefault(RECORD_INSTANCE_ID);
            setPropertyByDefault(COMPLETION_CRITERIA);
            return this;
        }
        // setProperty(PARAMETER_NAME, "1");
        elementPropertiesByDefault.keySet().stream().filter(property -> property.getGroup() != InspectorGroup.GEOMETRY
                && property.getGroup() != InspectorGroup.PROPERTIES && property.getGroup() != InspectorGroup.OUTPUT_MAP)
                .forEach(property -> {
                    String value = elementPropertiesByDefault.get(property);
                    setProperty(property, value); // setProperty(PARAMETER_NAME, "1");
                });
        return this;
    }

    /**
     * Verifies properties autocomplete.
     *
     * @param property - should be the property, with realized Autocomplete function;
     *                 NOTE: this method must be maintained as new itemsPath locator
     *                 (locator of result, which must be shown as a dropDow list, after matching).
     */
    public ActiveElement verifyAutoComplete(Property property) {
        String itemsPath = propertyPreparation(property);
        page.verifyAutoCompleteForDropDowns(property.getLocator(), itemsPath);
        return this;
    }

    /**
     * Clicks on property, collects all values from expanded list and returns it.
     *
     * @param property should be select type
     * @return Returns all values from DropDown List type property
     */
    public List<String> getDropDownList(Property property) {
        String itemsPath = propertyPreparation(property);
        click(property.getLocator());
        return getListOfWebElementsTextByLocator(itemsPath);
    }

    private String propertyPreparation(Property property) {
        page.clickOnElement(this);
        String itemsPath;
        if (!property.isAngular() && !isElementPresent(property.getLocator())) {
            property.setAngular(true);
        }
        if (property == OUTPUT_MAP_NAME) {
            itemsPath = selectItems;
        } else {
            itemsPath = property.getLocator() + "//option";
        }
        return itemsPath;
    }

    /**
     * Fills element`s all known properties by default values. Does not touch name, label, help text and GEOMETRY group.
     */
    public ActiveElement setPropertiesByDefault() {
        //TODO: add implementation for SubProcess
        if (type.equals(USER_TASK, CALL_ACTIVITY)) {
            ElementProperties prop = type == CALL_ACTIVITY ? CALLED_PROCESS : RECORD_DEFINITION;
            setProperty(prop, prop.getDefault());
            page.confirmModalDialog(true);
        }
        if (type == USER_TASK) {
            setPropertyByDefault(RECORD_INSTANCE_ID);
            setPropertyByDefault(COMPLETION_CRITERIA);
            return this;
        }
        elementPropertiesByDefault.keySet().stream().filter(property -> property.getGroup() != InspectorGroup.GEOMETRY
                && property.getGroup() != InspectorGroup.PROPERTIES
                && property.getGroup() != InspectorGroup.OUTPUT_MAP).forEach(this::setPropertyByDefault);
        return this;
    }

    private ActiveElement handleElementProperties(boolean isReadValues) {
        List<WebElement> props = getElements(allVisiblePropertiesPath);
        elementPropertiesByDefault = new LinkedHashMap<>();
        for (WebElement w : props) {
            String property = w.getText();
            if (!property.isEmpty()) {
                Property currentProp = ElementProperties.fromString(property);
                String value = currentProp.getDefault();
                if (isReadValues)
                    try {
                        value = getPropertyValue(currentProp);
                    } catch (Exception e) {
                        log.info("********Could not read " + currentProp);
                    }
                elementPropertiesByDefault.put(currentProp, value);
            }
        }
        return this;
    }

    private ActiveElement add(Property property, String value) {
        elementProperties.put(property, value);
        return this;
    }

    public ActiveElement optimizePropertiesValues() {
        String value;
        for (Property prop : elementPropertiesByDefault.keySet()) {
            if (!(prop instanceof DynamicProperty)) {
                switch ((ElementProperties) prop) {
                    case RUN_AS: {
                        value = elementPropertiesByDefault.get(prop);
                        value = value.equals("false") ? "Administrator" : value.equals("true") ? "Current User" : value;
                        elementPropertiesByDefault.put(prop, value);
                    }
                    break;
                    case LOOP_TYPE: {
                        value = elementPropertiesByDefault.get(prop);
                        value = value.equals("false") ? "Parallel" : value.equals("true") ? "Sequential" : value;
                        elementPropertiesByDefault.put(prop, value);
                    }
                    break;
                    default:
                        break;
                }
            }
        }
        return this;
    }

    public ActiveElement parseAndAdd(String property, String value) {
        Property prop = ElementProperties.fromString(property);
        elementPropertiesByDefault.put(prop, value);
        return this;
    }

    /**
     * Reads all current properties and its fields, rewrites elementPropertiesByDefault Map by this data.
     */
    private ActiveElement readAllPropertiesValues() {
        return handleElementProperties(true);
    }

    /**
     * Set(only remember, not to fill on UI) Map of properties with default values
     */
    private ActiveElement setAllPropertiesValues() {
        return handleElementProperties(false);
    }

    @Step
    public ActiveElement verifyElementContainsProperties(Stream<Property> properties) {
        properties.forEach(property -> verifyTrue(elementPropertiesByDefault.containsKey(property)
                && isElementPresent(property.getFieldNameLocator()), String.format("Property %s is not present",
                property)));
        return this;
    }

    @Step
    public ActiveElement verifyElementNotContainsProperties(Stream<Property> properties) {
        properties.forEach(property -> verifyFalse(isElementPresent(property.getFieldNameLocator()),
                String.format("Property %s is present", property)));
        return this;
    }

    /**
     * If element has already filled the property, it stores in elementPropertiesByDefault map. So the method searches this in the store, if there no such Property, method calls getPropertyValue(..)
     *
     * @param property, which value should be find
     * @return the value of property
     */
    public String getExistPropertyValue(Property property) {
        if (elementProperties.containsKey(property)) {
            return elementProperties.get(property);
        }
        return getPropertyValue(property);
    }

    /**
     * Fills element`s properties in order (like in ElementProperties enum,
     * but exactly for this element), while properties or values finish.
     *
     * @values sequence of data for properties entering.
     * Warning: is not working for CALL_ACTIVITY and USER_TASK elements.
     */
    public ActiveElement byValues(Object[] values) {
        int i = 0;
        for (Property field : elementTemporaryProperties) {
            if (values.length == i) break;
            setProperty(field, values[i++].toString());
        }
        return this;
    }

    public ActiveElement byValuesTextFields(Object... values) {
        page.clickOnElement(this);
        int i = 0;
        for (Property field : elementTemporaryProperties) {
            if (values.length == i) break;
            String locator = field.getLocator();
            page.typeKeysWithEnter(field.getLocator(), values[i++].toString());
            if (field == WIDTH || field == HEIGHT) {
                click(locator);
                page.typeKeysWithEnter(field.getLocator(), values[--i].toString());
                i++;
            }
        }
        return this;
    }

    /**
     * Appends xpath to this element locator, and verifies an existing such element. It useful if some test need to verify existing some icons or nodes into the current element.
     * example: verifyContains(ParallelIcon) - verify, is such image present on the element.
     *
     * @param xPath to inner node (inner xpath)
     */
    @Step
    public void verifyContains(String xPath) {
        verifyTrue(page.isElementPresent(this.getXPath() + xPath),
                String.format("The NODE %s for this element %s is not present", xPath, type));
    }

    @Step
    public void verifyContains(Stream<String> xPaths) {
        xPaths.forEach(xPath -> verifyTrue(page.isElementPresent(this.getXPath() + xpath),
                String.format("The NODE %s for this element %s is not present", xpath, type)));
    }

    public ActiveElement clearPropertiesMap() {
        elementPropertiesByDefault.clear();
        return this;
    }

    /**
     * Writes name and source into Output Map block. If there is no such position, method creates it.
     *
     * @nameValue text into name field.
     * @sourceValue text into source field.
     * @position Data input exactly in position block. Warning: counting starts from 0. Is prohibited position -n(<0).
     */
    public ActiveElement setProperty(String property, String value) {
        log.info("Setting " + value + " value for the " + property + " property");
        Property p = new DynamicProperty(property);
        setProperty(p, value);
        return this;
    }

    /**
     * Writes name and source into Output Map block. If there is no such position, method creates it.
     *
     * @return The position (index), which MAP block was updated this time.
     * @nameValue text into name field.
     * @sourceValue text into source field.
     * @position Data input exactly in position block. Warning: counting starts from 0. Is prohibited position -n(<0).
     */
    @Step
    public int setOutputMap(String nameValue, String sourceValue, int index) {
        page.clickOnElement(this);
        if (!page.isElementPresent(String.format(indexPath, index))) {
            setProperty(OUTPUT_MAP_NAME, nameValue);
            setProperty(OUTPUT_MAP_SOURCE, sourceValue);
            index = new Integer(page.getElement(getLast(String.format(indexPath, ""))).getAttribute("data-index"));
        } else {
            String mapName = ElementProperties.outputMapLocator(OUTPUT_MAP_NAME, index);
            String mapSource = ElementProperties.outputMapLocator(OUTPUT_MAP_SOURCE, index);
            setProperty(mapName, nameValue);
            setProperty(mapSource, sourceValue);
        }
        return index;
    }

    /**
     * Just easy way to add new OutputMAp. Just clicks on add button and adds name or both into the fields.
     *
     * @return this
     */
    @Step
    public ActiveElement addOutputMap(String[] args) {
        if (args != null) {
            setProperty(OUTPUT_MAP_NAME, args[0]);
            if (args.length >= 2)
                setProperty(OUTPUT_MAP_SOURCE, args[1]);
        }
        return this;
    }

    /**
     * Removes Output Map block in position. If the position is not exist, nothing happens.
     *
     * @position Output Map block. Warning: counting starts from 0.
     */
    public ActiveElement removeOutputMap(int position) {
        String locator = ElementProperties.getOutputMapRemover(position);
        if (isElementPresent(locator)) {
            click(locator);
            page.confirmModalDialog(true);
        }
        return this;
    }

    /**
     * Returns sorted collection of properties, is belonged exactly this element type.
     */
    public Set<Property> getElementCoreProperties() {
        return elementPropertiesByDefault.keySet();
    }

    /**
     * Returns sorted Map of current displayed properties and its Values, is belonged exactly this element type.
     */
    public Map<Property, String> getAllPropertiesCurrentMap() {
        readAllPropertiesValues();
        return elementPropertiesByDefault;
    }

    private void buildElementProperties() {
        elementPropertiesByDefault = ElementProperties.getElementProperties(type, page);
    }

    @Deprecated
    public void fillAllRequiredFields() {
        initiateInspector();
    }

    @Deprecated
    public void fillAllFields() {
        initiateInspector();
    }

    public void saveElementState() {
        initiateInspector();
    }

    public void addInputMapField(ContainerProperty field) {
        inputMapFields.add(field);
    }

    public PaletteGroup getGroupOnPalette() {
        return groupOnPalette;
    }

    public void readCoordinates(ProcessDefinitionEditorPage page) {
        page.clickOnElement(this);
        x.setTextValue(page.getX());
        y.setTextValue(page.getY());
    }

    public void readGeometry(ProcessDefinitionEditorPage page) {
        page.clickOnElement(this);
        x.setTextValue(page.getX());
        y.setTextValue(page.getY());
        width.setTextValue("" + page.getElementWidth(this));
        height.setTextValue("" + page.getElementHeight(this));
    }

    private void initiateInspector() {
        if (inspector == null)
            inspector = page.getElementPropertiesTab();
    }

    private void buildXPath() {
        this.xPath = "xpath=//*[@model-id='" + id + "']";
    }

    /**
     * Sets elements geometry in strict sequence x, y, width, height, witch be set for this element.
     * If you params out of bound, it does not call exception.
     *
     * @param geometry Strict sequence [x, y, width, height] params. Don`t worry about less or more inputted params.
     */
    public void setGeometry(String... geometry) {
        ContainerProperty[] props = {x, y, width, height};
        int i = 0;
        for (String position : geometry) {
            if (i == props.length) break;
            props[i++].setTextValue(position);
        }
    }

    /**
     * Useful in case, where needs verify existing or equals some properties.
     *
     * @param group which should be expanded.
     * @return all properties from selected group;
     */
    public List<String> getAllPropertiesNamesFromGroup(InspectorGroup group) {
        this.click();
        page.getElementPropertiesTab().expandAllGroups();
        return getListOfWebElementsTextByLocator(String.format("xpath=%s//label", group.getLocator()))
                .stream().map(a -> a.replace(":", "")).collect(Collectors.toList());
    }

    private Point getCoordinates() {
        String[] coordinates = page.getElement(getXPath()).getAttribute("transform").replace("translate(", "")
                .replace(")", "").split(",");
        return new Point(new Integer(coordinates[0]), new Integer(coordinates[1]));

    }

    /**
     * If element doesn`t have coordinates as properties, use this method.
     *
     * @return Current X coordinate on canvas. (Not from property).
     */
    public int getX() {
        return getCoordinates().getX();
    }

    public void setX(int x) {
        this.x.setTextValue("" + x);
    }

    /**
     * If element doesn`t have coordinates as properties, use this method.
     *
     * @return Current Y coordinate on canvas. (Not from property).
     */
    public int getY() {
        return getCoordinates().getY();
    }

    public void setY(int y) {
        this.x.setTextValue("" + y);
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name.getFieldName();
    }

    private void setName(String name) {
        this.name.setFieldName(name);
    }

    public String getLabel() {
        return getPropertyValue(LABEL);
    }

    public String getXPath() {
        return xPath;
    }

    public void setXPath(String XPath) {
        this.xPath = XPath;
    }

    public int getWidth() {
        return new Integer(width.getTextValue());
    }

    private void setWidth(int width) {
        this.width.setTextValue("" + width);
    }

    public int getHeight() {
        return new Integer(height.getTextValue());
    }

    private void setHeight(int height) {
        this.height.setTextValue("" + height);
    }

    public ElementOfDesigner getType() {
        return type;
    }

    public ProcessDefinitionEditorPage getPage() {
        return page;
    }
}