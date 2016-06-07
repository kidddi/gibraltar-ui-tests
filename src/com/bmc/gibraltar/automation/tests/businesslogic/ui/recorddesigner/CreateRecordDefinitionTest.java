package com.bmc.gibraltar.automation.tests.businesslogic.ui.recorddesigner;

import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.framework.entities.User;
import com.bmc.gibraltar.automation.items.Application;
import com.bmc.gibraltar.automation.items.CommonHandlers;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.record.RecordDefinition;
import com.bmc.gibraltar.automation.items.record.RecordField;
import com.bmc.gibraltar.automation.items.record.RecordProperties;
import com.bmc.gibraltar.automation.items.tab.ElementPropertiesTab;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ValidationIssuesTab;
import com.bmc.gibraltar.automation.pages.*;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.parameter.DataType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;

public class CreateRecordDefinitionTest extends AppManagerBaseTest {
    private RecordDefinitionsPage recordDefinitionsPage;
    private ProcessDefinitionEditorPage processEditorPage;

    @DataProvider(name = "dataTypesOfRecordFields")
    public static Iterator<Object[]> dataTypesOfRecordFields() {
        return Arrays.asList(DataType.getAllRecordsTypes())
                .stream().map(type -> new Object[]{type}).collect(Collectors.toList()).iterator();
    }

    @DataProvider(name = "recordDefinitionNames")
    public static Object[][] recordDefinitionNames() {
        return new Object[][]{
                new Object[]{"UserMessage"},
                new Object[]{"Task"},
        };
    }

    @DataProvider(name = "elementsWithRecordDefinitionProperty")
    public static Object[][] elementsWithRecordDefinitionProperty() {
        return new Object[][]{
                new Object[]{ElementOfDesigner.CREATE_RECORD_INSTANCE, InspectorGroup.INPUT_MAP, "Record Definition Name:"},
                new Object[]{ElementOfDesigner.UPDATE_RECORD_INSTANCE, InspectorGroup.INPUT_MAP, "Record Definition Name:"},
                new Object[]{ElementOfDesigner.USER_TASK, InspectorGroup.PROPERTIES, "Record Definition:"},
        };
    }

    @Test(dataProvider = "dataTypesOfRecordFields", groups = Groups.CATEGORY_FULL, description = "defaultValuesForRecordField")
    @Features("Record Definitions")
    @Stories("US202419")
    @GUID("99eb79ad-a312-48ab-8bba-cd7b8c40df7c")
    public void defaultValuesForRecordField(DataType type) {
        testDescription = "Verify default values for a record field with data type: " + type.getName();
        recordDefinitionsPage = Optional.ofNullable(recordDefinitionsPage).orElse(new RecordDefinitionsPage(wd));
        recordDefinitionsPage.navigateToPage();
        RecordDefinition record = new RecordDefinition(wd, type.getName() + RandomStringUtils.randomAlphanumeric(5), recordDefinitionsPage);
        RecordField field = record.addNewField(type);
        CommonHandlers.FuncTwoVoid<RecordProperties, String> verify = (prop, value) -> recordDefinitionsPage
                .assertTrue(field.readProperty(prop).equals(value));
        verify.that(RecordProperties.NAME, "New Field");
        verify.that(RecordProperties.TYPE, field.getDataType().getName());
        verify.that(RecordProperties.DEFAULT_VALUE, "");
        verify.that(RecordProperties.MAXIMUM, "");
        verify.that(RecordProperties.MINIMUM, "");
        //TODO: use data provider instead of switch
        switch (type) {
            case TEXT:
                verify.that(RecordProperties.LENGTH, "254");
                break;
            case DECIMAL:
                verify.that(RecordProperties.PRECISION, "2");
                break;
            case FLOATING:
                verify.that(RecordProperties.PRECISION, "6");
                break;
            case IMAGE:
                verify.that(RecordProperties.SIZE, "500");
                break;
            case SELECTION:
                verify.that(RecordProperties.OPTIONS, "");
                field.fillOptions("some expression");
                break;
        }
        field.fillName(field.getDataType().getName());
        recordDefinitionsPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "checkPresenceOfRecordsFromLibrary")
    @Features("Record Definitions")
    @Stories("US201782")
    @GUID("1be8379b-1b19-419d-b45c-8c119e9385f5")
    public void checkPresenceOfRecordsFromLibrary() {
        recordDefinitionsPage = Optional.ofNullable(recordDefinitionsPage)
                .orElse(new RecordDefinitionsPage(wd));
        recordDefinitionsPage.navigateToPage();
        List<String> recordDefinitions = recordDefinitionsPage.getNamesOfExistingRecords();
        assertThat("Record Definitions from the library are not present.", recordDefinitions,
                hasItems("Note", "Task", "UserMessage", "ApprovalSignature", "ApprovalSummary", "Connection"));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "recordDefinitionsFromLibraryAreReadonly")
    @Features("Record Definitions")
    @Stories("US201782")
    @GUID("3bdd5fde-000a-40df-b83b-d1ac737cc51f")
    public void recordDefinitionsFromLibraryAreReadonly() {
        // Demo is SaaS admin, and he is able to edit all these records
        if (!user.getUsername().equals("Demo")) {
            recordDefinitionsPage = Optional.ofNullable(recordDefinitionsPage)
                    .orElse(new RecordDefinitionsPage(wd));
            recordDefinitionsPage.navigateToPage();
            String[] recordFromLibrary = {"UserMessage", "approval:ApprovalSignature", "approval:ApprovalSummary"};
            RecordDefinitionEditorPage editorPage = new RecordDefinitionEditorPage(wd);
            for (String recordName : recordFromLibrary) {
                openURL(editorPage.getPageUrl() + recordName);
                editorPage.waitForPageLoaded();
                editorPage.verifyInfoMessagesAppear("You do not have permissions to edit this record definition");
                recordDefinitionsPage.navigateToPage();
            }
        }
    }

    @Test(dataProvider = "elementsWithRecordDefinitionProperty", groups = Groups.CATEGORY_FULL, description = "recordsNamesForRecordDefinitionDropdown")
    @Features("Record Definitions")
    @Stories("US204287, US201782, US202009")
    @GUID("e7c50fca-43da-47ad-b718-26b680164d0a")
    public void recordsNamesForRecordDefinitionDropdown(ElementOfDesigner element, InspectorGroup group, String propertyName) {
        testDescription = "Verify that existing record definitions are present for an element: "
                + element.getName() + " in group: " + group.getGroupName() + " for a property: " + propertyName;
        recordDefinitionsPage = Optional.ofNullable(recordDefinitionsPage).orElse(new RecordDefinitionsPage(wd));
        recordDefinitionsPage.navigateToPage();
        List<String> existingRecordDefinitions = recordDefinitionsPage.getNamesOfExistingRecords();
        ProcessDefinitionsTabPage processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        processEditorPage = processDefinitionsTabPage.initiateNewProcess();
        ActiveElement elementOnCanvas = processEditorPage.dragAndDropToCanvas(element);
        processEditorPage.clickOnElement(elementOnCanvas);
        ElementPropertiesTab elPropertiesTab = processEditorPage.getElementPropertiesTab();
        List<String> recordDefinitionNames = elPropertiesTab.getAvailableOptionsFromDropdown(group, propertyName);
        assertThat(
                String.format("'Record Definition' drop-down of the %s element does not contain all existing record definitions.", element.getName()),
                recordDefinitionNames, containsInAnyOrder(existingRecordDefinitions.toArray()));
        processDefinitionsTabPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "checkColumnNamesOnRecordDefinitionsPage")
    @Features("Record Definitions")
    @Stories("US202009")
    @GUID("ca61ce15-a995-4d90-b033-282de1547007")
    public void checkColumnNamesOnRecordDefinitionsPage() {
        recordDefinitionsPage = Optional.ofNullable(recordDefinitionsPage)
                .orElse(new RecordDefinitionsPage(wd));
        recordDefinitionsPage.navigateToPage();
        recordDefinitionsPage.waitForPageLoaded();
        assertThat("Not all expected columns are present on the Record Definitions page.",
                recordDefinitionsPage.getColumnNames(),
                hasItems("Record Definition Name", "Bundle Path"));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyGridViewOfFieldsInRecordDesigner")
    @Features("Record Definitions")
    @Stories("US202419, US203853")
    @GUID("1f804353-b3ad-44be-aca9-d3b3db112532")
    public void verifyGridViewOfFieldsInRecordDesigner() {
        recordDefinitionsPage = Optional.ofNullable(recordDefinitionsPage)
                .orElse(new RecordDefinitionsPage(wd));
        recordDefinitionsPage.navigateToPage();
        RecordDefinitionEditorPage recordDesigner = recordDefinitionsPage.initiateNewRecordDef();
        List<String> actualGridColumns = recordDesigner.getColumnNames();
        assertThat("Actual grid columns doesn't contain expected ones.", actualGridColumns,
                hasItems("Field Name", "Data Type", "Required", "Default Value"));
        recordDefinitionsPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyLastModifiedAndOwnerProps")
    @Features("Record Definitions")
    @Stories("US203853")
    @Issue("SW00498077: Record Definition. Nobody can edit record definitions besides its creators(owner)")
    @GUID("e578d389-13bb-471e-b592-038f73a68858")
    @Bug("SW00498077")
    public void verifyLastModifiedAndOwnerProps() {
        log.info("Data preparation");
        User ajayUser = new User("ajay@coke.com", "password");
        User jonnieUser = new User("jonnie@coke.com", "password");
        String ajay = ajayUser.getUsername();
        String jonnie = jonnieUser.getUsername();
        RecordDefinitionsPage recordDefinitionsPage = new RecordDefinitionsPage(wd);
        recordDefinitionsPage.logout();
        LoginPage loginPage = new LoginPage(wd, Application.TASK_MANAGER).navigateToPage();
        loginPage.loginTaskManager(ajay, ajayUser.getPassword());
        recordDefinitionsPage.navigateToPage();
        RecordDefinition recordDefinition = new RecordDefinition(
                wd, "LastModifiedAndOwnerAjay" + recordDefinitionsPage.randomString(), recordDefinitionsPage);
        Function<String, Date> date = name -> {
            recordDefinition.addNewField(TEXT).fillName("Text" + name);
            recordDefinition.save();
            return new Date();
        };
        CommonHandlers.FuncTwoVoid<String, Date> verify = (user, registerDate) -> {
            // TODO remove usage of deprecated method
            Date lastModifiedDate = new Date(recordDefinition.getProperty(RecordProperties.LAST_MODIFIED_DATE));
            long expected = (registerDate.getTime() - lastModifiedDate.getTime()) / 1000;
            verifyTrue((expected < 10 && -expected > -10), "expected date for 10 seconds more. Real: " + expected);
            verifyEquals(recordDefinition.getProperty(RecordProperties.OWNER), "ajay");
            verifyEquals(recordDefinition.getProperty(RecordProperties.LAST_MODIFIED_BY), user.replaceAll("@coke.com", ""));
        };
        log.info("Test Execution");
        Date registerDate = date.apply(ajay);
        verify.that(ajay, registerDate);
        recordDefinition.close();
        recordDefinitionsPage.logout();
        loginPage.loginTaskManager(jonnie, jonnieUser.getPassword());
        recordDefinition.openThisRecord();
        verify.that(ajay, registerDate);
        verify.that(jonnie, date.apply(jonnie));
        recordDefinition.close();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyDuplicateNamesAreShownInValidationTab")
    @Features("Record Definitions")
    @Stories("US202419")
    @GUID("54cb661c-7be1-4459-98d5-1a0b88395b65s")
    public void verifyDuplicateNamesAreShownInValidationTab() {
        recordDefinitionsPage = Optional.ofNullable(recordDefinitionsPage).orElse(new RecordDefinitionsPage(wd));
        recordDefinitionsPage.navigateToPage();
        RecordDefinition record = new RecordDefinition(
                wd, "verifyDuplicateNamesAreShownInValidationTab" + recordDefinitionsPage.randomString(),
                recordDefinitionsPage);
        ValidationIssuesTab validTab = record.toValidationTab();
        validTab.verifyValidationTabHasNoErrors();
        RecordField decimal = record.addNewField(DECIMAL);
        RecordField floating = record.addNewField(FLOATING);
        validTab.verifyIssueExists(decimal, "Field name must be unique");
        validTab.verifyIssueExists(floating, "Field name must be unique");
        decimal.fillName("decimal");
        validTab.verifyValidationTabHasNoErrors();
        record.save();
        recordDefinitionsPage.navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyMaximumAndMinimumPropertiesFunctionalitys")
    @Features("Record Definitions")
    @Stories("US202419")
    @GUID("23c2241f-9c99-4ebe-b844-90ef573c98dd")
    public void verifyMaximumAndMinimumPropertiesFunctionality() {
        recordDefinitionsPage = Optional.ofNullable(recordDefinitionsPage).orElse(new RecordDefinitionsPage(wd));
        recordDefinitionsPage.navigateToPage();
        RecordDefinition record = new RecordDefinition(
                wd, "MaximumAndMinimumPropertiesFunctionality" + recordDefinitionsPage.randomString(),
                recordDefinitionsPage);
        RecordProperties[] correctTestData = {RecordProperties.MAXIMUM.set("10"),
                RecordProperties.MINIMUM.set("5"),
                RecordProperties.DEFAULT_VALUE.set("7")};
        RecordField decimal = record.addNewFieldAndFillProperties(DECIMAL, correctTestData);
        RecordField floating = record.addNewFieldAndFillProperties(FLOATING, correctTestData);
        RecordField integer = record.addNewFieldAndFillProperties(INTEGER, correctTestData);
        RecordField[] fields = {decimal, floating, integer};
        record.toValidationTab().verifyValidationTabHasNoErrors();
        record.save();
        for (RecordField field : fields) {
            field.fillDefaultValue("100");
            record.toValidationTab().verifyIssueExists(field, "Default Value cannot be greater than Maximum Value.");
            field.fillDefaultValue("4");
            record.toValidationTab().verifyIssueExists(field, "Default Value cannot be less than Minimum Value.");
        }
        RecordProperties[] wrongTestData = {RecordProperties.MAXIMUM.set("1"),
                RecordProperties.MINIMUM.set("5"),
                RecordProperties.DEFAULT_VALUE.set("")};
        for (RecordField field : fields) {
            field.fillProperty(wrongTestData);
            record.toValidationTab().verifyIssueExists(field, "Minimum Value cannot be greater than Maximum Value.");
        }
        floating.fillPrecision("");
        record.toValidationTab()
                .verifyIssueExists(floating, "Precision must be a valid number greater than 0, or equal to -1.");
        floating.fillPrecision("0");
        record.toValidationTab()
                .verifyIssueExists(floating, "Precision must be a valid number greater than 0, or equal to -1.");
        floating.fillPrecision("99");
        record.toValidationTab()
                .verifyIssueNotExists(floating, "Precision must be a valid number greater than 0, or equal to -1.");
        decimal.fillPrecision("0");
        record.toValidationTab().verifyIssueExists(decimal, "Precision must be a valid number between 1 and 9.");
        decimal.fillPrecision("99");
        record.toValidationTab().verifyIssueExists(decimal, "Precision must be a valid number between 1 and 9.");
        decimal.fillPrecision("2");
        record.toValidationTab().verifyIssueNotExists(decimal, "Precision must be a valid number between 1 and 9.");
        correctTestData = new RecordProperties[]{RecordProperties.MAXIMUM.set("10"),
                RecordProperties.MINIMUM.set("5"),
                RecordProperties.DEFAULT_VALUE.set("7")};
        for (RecordField field : fields) {
            field.fillProperty(correctTestData);
        }
        record.save().close();
    }
}