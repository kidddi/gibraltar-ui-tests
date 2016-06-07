package com.bmc.gibraltar.automation.tests.businesslogic.ui.recorddesigner;

import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.record.RecordField;
import com.bmc.gibraltar.automation.pages.RecordDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.RecordDefinitionsPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class RecordFieldsTest extends AppManagerBaseTest {
    private final String recordDefinitionName = "Record-" + RandomStringUtils.randomAlphanumeric(3);
    private RecordDefinitionsPage recordDefinitionsPage;
    private RecordDefinitionEditorPage recordDesigner;

    @DataProvider(name = "dataTypesForRecordFields")
    public static Object[][] dataTypesForRecordFields() {
        return new Object[][]{
                new Object[]{DataType.BOOLEAN, "True"},
                new Object[]{DataType.DATE, "2015-04-08"},
                new Object[]{DataType.DATE_TIME, "2014-01-01 01:23 PM"},
                new Object[]{DataType.INTEGER, "99"},
                new Object[]{DataType.TEXT, "Some text"},
                new Object[]{DataType.TIME, "12:55 PM"},
                new Object[]{DataType.SELECTION, "opt"},
                new Object[]{DataType.FLOATING, "2.5e-4"},
                new Object[]{DataType.DECIMAL, "30"}
        };
    }

    @Test(dataProvider = "dataTypesForRecordFields", groups = Groups.CATEGORY_SANITY, description = "addRecordFields")
    @Features("Record Definitions")
    @Stories("US202419")
    @GUID("93ae4fc2-05b8-418a-b7b2-424f68c026ea")
    public void addRecordFields(DataType dataType, String defValue) {
        testDescription = "Verifies that a record field with the "
                + dataType.getName() + " data type can be added into the record definition.";
        RecordField recField = generateRecordField(dataType, defValue);
        toRecord(recordDefinitionName);
        recordDesigner.addRecordField(recField);
        recordDesigner.saveRecord().closeRecordDefinition();
    }

    private RecordField generateRecordField(DataType dataType, String defValue) {
        if (dataType.equals(DataType.FLOATING) || dataType.equals(DataType.DECIMAL)) {
            return new RecordField("Test_" + dataType.getName(), dataType, true, "2", "0", "1000", defValue);
        } else {
            return new RecordField("Test_" + dataType.getName(), dataType, true, defValue,
                    "Description: " + dataType.getName(), new String[]{defValue, defValue + 1, defValue + 2}
            );
        }
    }

    private void toRecord(String recordName) {
        recordDefinitionsPage = new RecordDefinitionsPage(wd).navigateToPage();
        if (!recordDefinitionsPage.isRecordExistent(recordName)) {
            recordDesigner = recordDefinitionsPage.initiateNewRecordDef().setRecordName(recordName);
        } else {
            recordDesigner = recordDefinitionsPage.openRecordDefinition(recordName);
        }
    }
}