package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.definition;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Bug;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.ActionBar.Filter;
import com.bmc.gibraltar.automation.items.Application;
import com.bmc.gibraltar.automation.items.CommonHandlers;
import com.bmc.gibraltar.automation.items.Icon;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.pages.LoginPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import com.bmc.gibraltar.automation.utils.JsonUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;

import static com.bmc.gibraltar.automation.items.ActionBar.Action.*;
import static com.bmc.gibraltar.automation.items.tab.InspectorGroup.PROPERTIES;

public class ProcessDefinitionsTabGridTest extends AppManagerBaseTest implements CommonHandlers {
    private ProcessDefinitionsTabPage procTab;
    private ProcessDefinitionEditorPage procEditor;
    private List<String> ownersList;
    private Filter filter;

    @BeforeMethod
    public void getOwnersForFilter() {
        procTab = new ProcessDefinitionsTabPage(wd).navigateToPage();
        ownersList = filter.getAllOwners();
        filter = procTab.actionBar().getFilter();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyFilterHideOtherOwners")
    @Features("Process definitions")
    @Stories("US205657")
    @GUID("52e60507-6391-4d36-9347-efc9c163ff0a")
    public void verifyFilterHideOtherOwners() {
        FuncTwoVoid<List<String>, String> ownerColumn = (ownersList, owner) -> procTab
                .assertTrue(ownersList.stream().allMatch(s -> s.equals(owner)));
        FuncInt<String> ownerColumnSize = owner -> ownersList.stream().filter(s -> s.equals(owner)).toArray().length;
        for (String owner : ownersList) {
            filter.clickFilter().expandOwner().select(owner);
            List<String> currentOwnersList = procTab.getOwnersList();
            ownerColumn.that(currentOwnersList, owner);
            procTab.assertTrue(ownerColumnSize.test(owner) == currentOwnersList.size());
            filter.clickFilter();
        }
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyFilterClearAllAndAlphabeticalOrder")
    @Features("Process definitions")
    @Stories("US205657")
    @GUID("8879d0c1-11d3-453e-acba-aa84586eeb68")
    public void verifyFilterClearAllAndAlphabeticalOrder() {
        filter.clickFilter().expandOwner().selectAll();
        filter.clickFilter().clearAll();
        ownersList.containsAll(procTab.getOwnersList());
        procTab.assertTrue(isAlphabetical(filter.getAllOwners()));
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyFilterHideOtherOwners")
    @Features("Process definitions")
    @Stories("US205884")
    @GUID("2c5e4ea5-efea-4f78-a9ab-97fdc2732f39")
    public void verifyActionBar() {
        procTab.actionBar(NEW).verifyActionEnabled();
        procTab.actionBar().verifyPresent(NEW, RESTORE_TO_ORIGINAL, DELETE, COPY, GRID, CARD_GRID, CARD_LIST, REFRESH);
        procTab.actionBar(NEW).isActionEnabled();
        procTab.actionBar(RESTORE_TO_ORIGINAL).verifyActionDisabled();
        procTab.actionBar().getFilter().clickFilter();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyFilterHideOtherOwners")
    @Features("Process definitions")
    @Stories("US205884")
    @Issue("SW00499282, SW00499276, SW00499275, SW00499167, SW00496281, SW00496287")
    @GUID("27a3590a-a677-403b-bfdd-d61b4965681c")
    @Bug("SW00499282, SW00499276, SW00499275, SW00499167, SW00496281, SW00496287")
    public void verifyShowOrganisationScope() {
        // START test Data for Organizations scope tests
        String differentOrg = "Different Org";
        String currentOrg = "Current Org";
        String infoIconLocator;
        String infoIconText = "This definition belongs to a different organization";
        String organization = "CokeHR";
        RestDataProvider rest = new RestDataProvider("jonnie@coke.com", "password");
        rest.createProcessDefinition(differentOrg, "ProcessDefinitionTemplate.json");
        String json = JsonUtils.getJsonFromFile("records/CreateOrganizationRecord.json");
        rest.createOrganization(json);
        String user = rest.createUserByLicense("CokeHRUser", "", "coke", "Fixed", "Administrator", organization);
        appManager.logout();
        LoginPage loginPage = new LoginPage(wd, Application.APPLICATION_MANAGER);
        loginPage.loginToApplicationManager(user, "");
        procTab = new ProcessDefinitionsTabPage(wd).navigateToPage();
        String processCell = procTab.getRowLocator(differentOrg);
        infoIconLocator = procTab.getOrganizationInfoIconLocator();
        Icon icon = procTab.forIcon(infoIconLocator).verifyIconPresentOn(processCell).verifyIconContainsTextOnClick(infoIconText);
        procTab.sortTableBy("Process Definition Name");
        icon.verifyIconPresentOn(processCell).verifyIconContainsTextOnClick(infoIconText);
        String processInformationLocator = ProcessPropertiesTab.getLocatorByTitle("Process Information");
        procTab.selectProcessDefinition(differentOrg)
                .actionBar(DELETE).verifyActionDisabled()
                .forAction(RESTORE_TO_ORIGINAL).verifyActionDisabled();
        procTab.selectAllProcessDefinitions().actionBar(DELETE).verifyActionDisabled();
        procTab.openTheSavedProcess(differentOrg);
        procEditor = new ProcessDefinitionEditorPage(wd);
        infoIconLocator = ProcessDefinitionEditorPage.organizationInfoIconLocator;
        icon = procEditor.forIcon(infoIconLocator)
                .verifyIconPresentOn(processInformationLocator)
                .verifyIconContainsTextOnClick(infoIconText);
        procEditor.setProcessDescription("some text");
        procEditor.saveProcess().closeProcess();
        procTab.selectProcessDefinition(differentOrg)
                .actionBar(DELETE).verifyActionDisabled().forAction(RESTORE_TO_ORIGINAL).verifyActionEnabled().click();
        procTab.openTheSavedProcess(differentOrg);
        procEditor.getProcessPropertiesTab().verifyPropertyValue(PROPERTIES, "Description:", "");
        procEditor.closeProcess();
        procTab.initiateNewProcess();
        procEditor.setProcessName(currentOrg);
        icon.verifyIconNOTPresentOn(processInformationLocator)
                .verifyIconContainsTextOnClick(infoIconText);
        procEditor.bindElements(procEditor.getDroppedElement(ElementOfDesigner.START), procEditor.getDroppedElement(ElementOfDesigner.END));
        procEditor.saveProcess().closeProcess();
        procTab.selectProcessDefinition(differentOrg)
                .actionBar(RESTORE_TO_ORIGINAL).verifyActionDisabled()
                .forAction(DELETE).verifyActionEnabled().click();
        procTab.verifyProcessDoesNotExist(differentOrg);
    }
}