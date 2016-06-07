package com.bmc.gibraltar.automation.items.record;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.processdesigner.ToolbarItem;
import com.bmc.gibraltar.automation.items.tab.RecordFieldPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.RecordPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.ValidationIssuesTab;
import com.bmc.gibraltar.automation.pages.Locators;
import com.bmc.gibraltar.automation.pages.RecordDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.RecordDefinitionsPage;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.List;

import static com.bmc.gibraltar.automation.items.parameter.DataType.NAN;

public class RecordDefinition implements Locators {
    protected static final Logger LOG = Logger.getLogger(RecordDefinition.class);

    //link for actions
    RecordDefinitionsPage page;
    RecordDefinitionEditorPage editorPage;
    // Record properties
    private String name;
    private String description;

    // fields List
    private List<RecordField> allFields;

    private WebDriver driver;

    /**
     * Opens Record Definition tab, initiates new Record and type fill name field.
     *
     * @param driver
     * @param name   this text is typed into name field.
     * @param page   assigned to inner var and uses for different actions.
     */
    public RecordDefinition(WebDriver driver, String name, RecordDefinitionsPage page) {
        this.page = page;
        this.driver = driver;
        editorPage = page.initiateNewRecordDef();
        fillName(name);
        addDefaultFields();
    }

    @Step
    public static List<String> geFieldsViaREST(String record) {
        RestDataProvider rest = new RestDataProvider();
        List<String> fieldsFromREST = new ArrayList<>();
        fieldsFromREST.addAll(rest
                .getRecordFieldsNamesByFieldOption(RecordField.Option.OPTIONAL, record));
        fieldsFromREST.addAll(rest
                .getRecordFieldsNamesByFieldOption(RecordField.Option.REQUIRED, record));
        return fieldsFromREST;
    }

    @Step
    public String getProperty(RecordProperties property) {
        if (property.getTypes()[0] == NAN)
            toRecordPropertiesTab();
        return page.getValue(property.getLocator());
    }

    @Step
    public RecordDefinition openThisRecord() {
        page.navigateToPage();
        page.openRecordDefinition(name);
        return this;
    }

    /**
     * Need fo next operations with existing field
     *
     * @param fieldName correct field name.
     * @return existing field from ArrayList fields;
     */
    @Step
    public RecordField getField(String fieldName) {
        return allFields.stream().filter(s -> s.getName().equals(fieldName)).findFirst().get();
    }

    /**
     * Type record's name into field 'Name'
     */
    @Step
    public RecordDefinition fillName(String name) {
        this.name = name;
        editorPage.setRecordName(name);
        return this;
    }

    /**
     * Type text into field 'Help Text'
     */
    @Step
    public RecordDefinition fillDescription(String text) {
        this.description = text;
        toRecordPropertiesTab().setRecordDescription(text);
        return this;
    }

    @Step
    public ValidationIssuesTab toValidationTab() {
        return editorPage.toValidationTab(driver);
    }

    @Step
    public RecordFieldPropertiesTab toFieldPropertiesTab() {
        return editorPage.toFieldPropertiesTab();
    }

    /**
     * Selects a record field by clicking on its checkbox
     */
    @Step
    public RecordPropertiesTab toRecordPropertiesTab() {
        return editorPage.toRecordPropertiesTab();
    }

    /**
     * Adds a Record Fields, fills into Inspector tab all values of Field Properties,
     * like Name/Is Required/Default Value (instance of RecordField should have these values)
     *
     * @params fields 1 or more types will be created
     */
    @Step
    public RecordDefinition addNewFields(RecordField... fields) {
        for (RecordField fld : fields) {
            editorPage.addRecordField(fld.setAction(editorPage));

        }

        return this;
    }

    /**
     * Adds Record Fields, fills into Inspector tab all values of Field Properties by default.
     *
     * @params types 1 or more types will be created
     */
    @Step
    public RecordField[] addNewFields(DataType... types) {
        RecordField[] arrFields = new RecordField[types.length];
        int i = 0;
        for (DataType type : types) {
            RecordField newField = new RecordField(type).setAction(editorPage).setAllByDefault();
            allFields.add(newField);
            arrFields[i++] = newField;
            editorPage.createRecordField(newField);
        }
        return arrFields;
    }

    /**
     * Just adds a new Record Field by Type.
     *
     * @params only 1 type
     */
    @Step
    public RecordField addNewField(DataType type) {
        RecordField newField = new RecordField(type).setAction(editorPage);
        allFields.add(newField);
        editorPage.addRecordFieldDraft(newField);
        return newField;
    }

    /**
     * Create new field. Sets and types properties with its values. Example  new RecordField(TEXT, NAME.set("Cat"), LENGTH.set("'"777"))
     * Warning! Should be correct properties! But shouldn`t send name and Options obligatory.
     *
     * @param props should be specified style. Like  OPTIONS.set("tabby, black, monochrome"), REQUIRED.set("true"), MINIMUM.set("123") ..
     */
    @Step
    public RecordField addNewFieldAndFillProperties(DataType type, RecordProperties... props) {
        RecordField newField = new RecordField(type).setAction(editorPage).setAllByDefault();
        editorPage.createRecordField(newField);
        newField.fillProperty(props);
        allFields.add(newField);
        return newField;
    }

    @Step
    public RecordDefinition isFildPresent(String recField) {
        assert editorPage.isRecordFieldPresent(recField);
        return this;
    }

    @Step
    public RecordDefinition clickOnFieldRow(String recField) {
        editorPage.clickOnRecFieldRow(recField);
        return this;
    }

    /**
     * Deselects a record field by clicking on its checkbox
     */
    @Step
    public RecordDefinition deSelectFields(String... recFields) {
        for (String recField : recFields)
            editorPage.deSelectRecField(recField);
        return this;
    }

    /**
     * Selects a record fields by clicking on its checkbox
     */
    @Step
    public RecordDefinition selectFields(String... fields) {
        for (String field : fields)
            editorPage.selectRecField(field);
        return this;
    }

    /**
     * Selects a record fields by clicking on its checkbox
     */
    @Step
    public RecordDefinition selectFields(RecordField... fields) {
        for (RecordField field : fields)
            selectFields(field.getName());
        return this;
    }

    @Step
    public RecordDefinition isFieldSelected(String recField) {
        assert editorPage.isRecFieldSelected(recField);
        return this;
    }

    @Step
    public RecordDefinition deleteField(String recField) {
        editorPage.deleteRecField(recField);
        return this;
    }

    @Step
    public RecordDefinition deleteFields(String... recFields) {
        selectFields(recFields);
        clickDelete();
        return this;
    }

    @Step
    public RecordDefinition deleteFields(RecordField... fields) {
        selectFields(fields);
        clickDelete();
        return this;
    }

    @Step
    public RecordDefinition clickDelete() {
        editorPage.clickToolBarItem(RecordDefinitionEditorPage.ToolBarItem.DELETE_FIELD);
        return this;
    }

    @Step
    public RecordDefinition verifyDeleteBtnInactive() {
        editorPage.verifyDeleteBtnInactive();
        return this;
    }

    @Step
    public RecordDefinition verifyDeleteBtnActive() {
        editorPage.verifyDeleteBtnActive();
        return this;
    }

    @Step
    public RecordDefinition save() {
        editorPage.saveRecord();
        return this;
    }

    @Step
    public RecordDefinitionsPage close() {
        return editorPage.closeRecordDefinition();
    }

    @Step
    @Deprecated
    public RecordDefinition undo() {
        page.click(ToolbarItem.UNDO.getIconPath());
        return this;
    }

    @Step
    @Deprecated
    public RecordDefinition redo() {
        page.click(ToolbarItem.REDO.getIconPath());
        return this;
    }

    /**
     * Verify is UNDO or REDO (depends on params) disabled
     *
     * @param item UNDO or REDO only!
     * @return true if disabled end false if enebled
     */
    @Step
    @Deprecated
    public boolean verifyUndoRedoIsDisabled(ToolbarItem item) {
        LOG.info("Verifying if the " + item + " is disabled.");
        return page.isElementPresent(item.getIconPath() + "[@disabled='disabled']");
    }

    /**
     * Verify is UNDO or REDO (depends on params) enebled
     *
     * @param item UNDO or REDO only!
     * @return true if enabled end false if disabled
     */
    @Step
    public boolean verifyUndoRedoIsEnabled(ToolbarItem item) {
        return !verifyUndoRedoIsDisabled(item);
    }

    /**
     * Adds default (core) fields to the List due constructor creation.
     */

    private void addDefaultFields() {
        allFields = new ArrayList<>();
        for (CoreRecordFields coreField : CoreRecordFields.values()) {
            allFields.add(coreField.getRecordField());
        }
    }

    public String getName() {
        return name;
    }
}
