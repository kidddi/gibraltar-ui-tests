package com.bmc.gibraltar.automation.tests.businesslogic.ui.recorddesigner;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.record.CoreRecordFields;
import com.bmc.gibraltar.automation.pages.RecordDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.RecordDefinitionsPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

public class DeleteRecordDefinitionTest extends AppManagerBaseTest {
    private RecordDefinitionsPage recordDefinitionsPage;

    @Test(groups = Groups.CATEGORY_SANITY, description = "abilityToDeleteRecordDefinition")
    @Features("Record Definitions")
    @Stories("US202537")
    @GUID("886b8220-5a07-4cc6-8079-c19c983537bd")
    public void abilityToDeleteRecordDefinition() {
        String recordDefinitionName = createRecordAndGoToPage("RecordForDelete-" + RandomStringUtils.randomAlphanumeric(5));
        recordDefinitionsPage.verifyRecordDefinitionPresence(recordDefinitionName, true)
                .selectRecord(recordDefinitionName)
                .verifyDeleteBtnActive();
        recordDefinitionsPage.clickDelete()
                .verifyModalDialog("Are you sure you want to delete selected record definitions?", true)
                .confirmModalDialog(true);
        assertFalse(recordDefinitionsPage.isRecordExistent(recordDefinitionName),
                String.format("The record definition with the %s is present.", recordDefinitionName));
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "addRecordWithSameNameAfterOnceWasDeleted")
    @Features("Record Definitions")
    @Stories("US202537")
    @GUID("fb45f836-9941-4cac-b355-e97b60bce615")
    public void addRecordWithSameNameAfterOnceWasDeleted() {
        String recordDefinitionName = createRecordAndGoToPage("RecordForDelete" + RandomStringUtils.randomAlphanumeric(5));
        recordDefinitionsPage.deleteRecord(recordDefinitionName);
        RecordDefinitionEditorPage recordDesigner = recordDefinitionsPage.initiateNewRecordDef().setRecordName(recordDefinitionName);
        recordDesigner.saveRecord().closeRecordDefinition();
        recordDefinitionsPage.waitForPageLoaded();
        assertTrue(recordDefinitionsPage.isRecordExistent(recordDefinitionName),
                String.format("The record definition with the %s is not present.", recordDefinitionName));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "inabilityToDeleteCoreField")
    @Features("Record Definitions")
    @Stories("US201788")
    @GUID("2368ee12-9a6c-47bd-a7a4-4ad94e40045d")
    public void inabilityToDeleteCoreField() {
        String recordDefinitionName = createRecordAndGoToPage("RecordDefinition-" + RandomStringUtils.randomAlphanumeric(5));
        RecordDefinitionEditorPage recordDesigner = recordDefinitionsPage.openRecordDefinition(recordDefinitionName);
        for (CoreRecordFields field : CoreRecordFields.values()) {
            recordDesigner.selectRecField(field.getName()).verifyDeleteBtnInactive();
            recordDesigner.deSelectRecField(field.getName());
        }
        recordDesigner.closeRecordDefinition();
    }

    private String createRecordAndGoToPage(String recordDefinitionName) {
        CommonSteps commonSteps = new CommonSteps(new RestDataProvider());
        commonSteps.createRecordDefinitionWithCoreFieldsOnly(recordDefinitionName);
        recordDefinitionsPage = new RecordDefinitionsPage(wd).navigateToPage();
        recordDefinitionsPage.refresh();
        return recordDefinitionName;
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "inabilityToDeleteProtectedRecords")
    @Features("Record Definitions")
    @Stories("US202537")
    @GUID("4d94587c-0cfc-47bb-bbbb-5c71650dc1cd")
    public void inabilityToDeleteProtectedRecords() {
        recordDefinitionsPage = new RecordDefinitionsPage(wd).navigateToPage();
        String[] coreRecords = {"Note", "Task", "UserMessage", "ApprovalSignature", "ApprovalSummary", "Connection"};
        if (user.getUsername().equals("Demo"))
            return; /* Demo user should skip this test, as it's risky to delete core Records */

        for (String coreRecord : coreRecords) {
            if (recordDefinitionsPage.isRecordExistent(coreRecord)) {
                recordDefinitionsPage.selectRecord(coreRecord).clickDelete().confirmModalDialog(true);
                recordDefinitionsPage.verifyErrorMessagesAppear();
                recordDefinitionsPage.closeAllErrorAlerts();
                recordDefinitionsPage.verifyRecordDefinitionPresence(coreRecord, true).deSelectRecord(coreRecord);
            }
        }
    }
}
