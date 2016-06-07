package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.items.record.RecordDefinition;
import com.bmc.gibraltar.automation.pages.RecordDefinitionsPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;

import static com.bmc.gibraltar.automation.items.parameter.DataType.DECIMAL;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecordDefinitionsTests extends AppManagerBaseTest {

    @BeforeClass
    public void openRecordDefinitionsPage() {
        new RecordDefinitionsPage(wd, "task-manager").navigateToPage();
    }

    @Test
    @Features("Application Manager")
    @Stories("US204352")
    public void createRecordDefinitionInAppManager() {
        String recordDefinitionName = "RecordDefinitionInAppManager" + RandomStringUtils.randomAlphanumeric(16);

        RecordDefinition record = new RecordDefinition(wd, recordDefinitionName, new RecordDefinitionsPage(wd));
        record.addNewFields(DECIMAL);
        RecordDefinitionsPage recordDefinitionsPage = record.save().close();
        List<String> recordDefinitions = recordDefinitionsPage.getNamesOfExistingRecords();
        assertThat(String.format("Record definition with the name %s in not present.", recordDefinitionName),
                recordDefinitions, hasItem(recordDefinitionName));
    }
}
