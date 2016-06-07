package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.definition;

import com.bmc.gibraltar.automation.dataprovider.CommonSteps;
import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

public class DeleteProcessDefinitionTest extends AppManagerBaseTest {
    private ProcessDefinitionsTabPage processDefinitionsTabPage;
    private String processDefinitionName;

    @BeforeClass
    protected void createDefinition() {
        processDefinitionName = "Delete" + Long.toHexString(System.currentTimeMillis());
        new CommonSteps(new RestDataProvider()).createProcessDefinition(processDefinitionName);
        processDefinitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "checkDeleteButtonState")
    @Features("Process Definitions tab")
    @Stories("US204059")
    @GUID("a41b09fe-1fb1-4b25-854b-d2d70e6be284")
    public void checkDeleteButtonState() {
        processDefinitionsTabPage.verifyDeleteButtonDisabled();
        processDefinitionsTabPage.selectProcessDefinition(processDefinitionName);
        processDefinitionsTabPage.verifyDeleteButtonEnabled();
        processDefinitionsTabPage.deleteProcessDefinition(false, processDefinitionName);
        assertTrue(processDefinitionsTabPage.getNamesOfSavedProcess().contains(processDefinitionName));
    }

    @Test(groups = Groups.CATEGORY_SANITY, description = "processDefinitionCanHaveNameAsDeletedHad")
    @Features("Process Definitions tab")
    @Stories("US204059")
    @GUID("79a3de5d-dd2e-4797-8a55-29a931fc3d2e")
    public void processDefinitionCanHaveNameAsDeletedHad() {
        // deletion a process definition
        processDefinitionsTabPage.deleteProcessDefinition(true, processDefinitionName);
        processDefinitionsTabPage.refreshProcessDefinitionsList();
        assertFalse(processDefinitionsTabPage.getNamesOfSavedProcess().contains(processDefinitionName));
        processDefinitionsTabPage.verifyDeleteButtonDisabled();

        // creation a process definition with the same name as the deleted had
        ProcessDefinitionEditorPage processDefinitionEditorPage = processDefinitionsTabPage.initiateNewProcess();
        processDefinitionEditorPage.setProcessName(processDefinitionName);
        ActiveElement startElement = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.START);
        ActiveElement endElement = processDefinitionEditorPage.getDroppedElement(ElementOfDesigner.END);
        processDefinitionEditorPage.bindElements(startElement, endElement);
        processDefinitionEditorPage.saveProcess().closeProcess();
        processDefinitionsTabPage.refreshProcessDefinitionsList();
        assertThat("Created process definition is not present.", processDefinitionsTabPage.getNamesOfSavedProcess(),
                hasItem(processDefinitionName));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "selectAllProcessDefinitionsForDeletion")
    @Features("Process Definitions tab")
    @Stories("US204059")
    @GUID("323e9ba9-6503-4300-ad7f-108560ceea58")
    public void selectAllProcessDefinitionsForDeletion() {
        List<String> listOfProcessDefinitions = processDefinitionsTabPage.getNamesOfSavedProcess();
        String[] prDefinitions = listOfProcessDefinitions.toArray(new String[listOfProcessDefinitions.size()]);
        processDefinitionsTabPage.selectAllProcessDefinitions();
        processDefinitionsTabPage.verifyProcessDefinitionsAreSelected(prDefinitions);
        processDefinitionsTabPage.deselectProcessDefinitions(prDefinitions);
    }
}