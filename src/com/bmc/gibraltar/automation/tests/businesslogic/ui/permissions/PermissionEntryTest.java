package com.bmc.gibraltar.automation.tests.businesslogic.ui.permissions;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.items.tab.ProcessPropertiesTab;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.apache.commons.lang3.ArrayUtils;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.Arrays;
import java.util.List;

public class PermissionEntryTest extends AppManagerBaseTest {

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyPermissionsGroupPresence")
    @Features("Inspector")
    @Stories("US204268")
    @GUID("09c74660-56f6-4f5f-ab2e-3819234f8f95")
    public void verifyPermissionsGroupPresence() {
        ProcessDefinitionsTabPage definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        ProcessDefinitionEditorPage editView = definitionsTabPage.initiateNewProcess();
        ProcessPropertiesTab processPropertiesTab = editView.getProcessPropertiesTab();
        processPropertiesTab.verifyGroupsPresence(new InspectorGroup[]{InspectorGroup.PERMISSIONS});
        editView.closeProcess();
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyPermissionEntryProperties")
    @Features("Inspector")
    @Stories("US204268")
    @GUID("f1001c31-db28-45d0-8375-4f792f34e1d4")
    public void verifyPermissionEntryProperties() {
        ProcessDefinitionsTabPage definitionsTabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        ProcessDefinitionEditorPage editView = definitionsTabPage.initiateNewProcess();
        ProcessPropertiesTab processPropertiesTab = editView.getProcessPropertiesTab();
        processPropertiesTab.verifyGroupsPresence(new InspectorGroup[]{InspectorGroup.PERMISSIONS});
        processPropertiesTab.clickAddParameter(InspectorGroup.PERMISSIONS);
        processPropertiesTab.verifyParametersCount(InspectorGroup.PERMISSIONS, 1);
        processPropertiesTab.verifyPropertiesPresence(
                InspectorGroup.PERMISSIONS, new String[]{"Permitted Group:", "Permission Type:"});

        List<String> permittedGroups = processPropertiesTab.getAvailableOptionsFromDropdown(
                InspectorGroup.PERMISSIONS, "Permitted Group:");
        List<String> allPermittedGroups = new RestDataProvider().getPermittedGroups();
        verifyTrue(permittedGroups.containsAll(allPermittedGroups));

        List<String> permissionTypes = processPropertiesTab.getAvailableOptionsFromDropdown(
                InspectorGroup.PERMISSIONS, "Permission Type:");
        verifyTrue(permissionTypes.containsAll(Arrays.asList("Read", "Execute")));
        processPropertiesTab.verifyPropertyValue(InspectorGroup.PERMISSIONS, "Permission Type:", "READ");
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "requestForAllPermissionsAreGotByApi")
    @Features("Permissions")
    @Stories("US204339")
    @GUID("1747b910-0f4b-48cc-a7bf-7eb9fc763a1b")
    public void requestForAllPermissionsAreGotByApi() {
        String[] ajayPermissions = {"Process Manager", "Task User", "Process Designer Computed",
                "Process Manager Computed", "Task User Computed", "Service Desk"};
        String[] jonniePermissions = {"AssigneeGroupAccess", "Approval Admin", "ASE-Administrator", "Process Designer"};
        String[] permissions = ajayPermissions;
        String[] users = {"ajay@coke.com", "ajay@pepsi.com", "jonnie@coke.com", "jonnie@pepsi.com"};
        for (String user : users) {
            RestDataProvider provider = new RestDataProvider(user, "password");
            String name = provider.getCurrentUserLoginName();
            List<String> allPermessions = provider.getGroupNames();
            if ("jonnie".equals(name))
                permissions = ArrayUtils.addAll(ajayPermissions, jonniePermissions);
            for (String permission : permissions) {
                if (!allPermessions.contains(permission)) {
                    log.info(permission + " Is NOT present!");
                    fail();
                }
                log.info(user + ": " + permission + " Is present!" + name);
            }
        }
    }
}