package com.bmc.gibraltar.automation.tests.businesslogic.ui.recorddesigner;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.record.CoreRecordFields;
import com.bmc.gibraltar.automation.pages.RecordDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.RecordDefinitionsPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteRecordFieldsTest extends AppManagerBaseTest {
    private static String recordDefinitionName = "RecordFields-" + Long.toHexString(System.currentTimeMillis());

    @DataProvider(name = "recordFields")
    public static Iterator<Object[]> recordFields() {
        RestDataProvider dataProvider = new RestDataProvider();
        new CommonSteps(dataProvider)
                .createRecordDefinitionByTemplate(recordDefinitionName, "recordDefinitionWithAllTypes.json");
        List<String> recordFields = dataProvider.getRecordFields(recordDefinitionName);
        List<String> coreFieldsNames = Arrays.asList(CoreRecordFields.values())
                .stream().map(CoreRecordFields::getName).collect(Collectors.toList());
        recordFields.removeAll(coreFieldsNames);
        return recordFields.stream().map(fieldName -> new Object[]{fieldName}).collect(Collectors.toList()).iterator();
    }

    @Test(dataProvider = "recordFields", groups = Groups.CATEGORY_SANITY, description = "deleteRecordField")
    @Features("Record Definitions")
    @Stories("US201788")
    @GUID("2e71bf61-dd78-4858-96ac-978c5ba74aeb")
    public void deleteRecordField(String fieldName) {
        testDescription = "Verify that the record field can be removed: " + fieldName;
        RecordDefinitionsPage recordDefinitionsPage = new RecordDefinitionsPage(wd).navigateToPage();
        RecordDefinitionEditorPage recordDesigner = recordDefinitionsPage.openRecordDefinition(recordDefinitionName);
        recordDesigner.selectRecField(fieldName).verifyDeleteBtnActive();
        recordDesigner
                .clickDelete()
                .verifyModalDialog("If you delete a field, any data stored in the field also will be deleted.", true)
                .confirmModalDialog(false);
        assertTrue(recordDesigner.isRecordFieldPresent(fieldName));
        recordDesigner.deSelectRecField(fieldName).deleteRecField(fieldName);
        assertFalse(recordDesigner.isRecordFieldPresent(fieldName),
                fieldName + " field is present in the record definition.");
        recordDesigner.saveRecord().closeRecordDefinition();
    }
}
