package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.dataprovider.UserDataProvider;
import com.bmc.gibraltar.automation.items.Application;
import com.bmc.gibraltar.automation.pages.ApplicationManagerHomePage;
import com.bmc.gibraltar.automation.pages.LoginPage;
import com.bmc.gibraltar.automation.tests.BaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class LoginTest extends BaseTest {

    @Test(dataProvider = "allCredentials", dataProviderClass = UserDataProvider.class)
    @Features("Permissions")
    @Stories("US204778, US204352")
    public void verifyUserHasAccessToApplicationManager(String email, String password, String[] permittedGroups) {
        RestDataProvider dataProvider = new RestDataProvider("jonnie@pepsi.com", "password");

        dataProvider.createUser(email + " Test User", email, password, email, permittedGroups);
        List<String> userGroups = dataProvider.getUserGroups(email, "groups");

        ApplicationManagerHomePage appManager = new ApplicationManagerHomePage(wd);

        LoginPage loginPage = new LoginPage(wd, Application.APPLICATION_MANAGER).navigateToPage();

        assertThat("User groups are not the same.", userGroups, hasItems(permittedGroups));

        loginPage.login(email, password);

        if (!(userGroups.contains("Struct Admin") || userGroups.contains("Administrator"))) {
            appManager.verifyUserHasNoAccessToAppManager();
            loginPage.closeAllErrorAlerts();
        } else {
            appManager.waitForPageLoaded();
            appManager.logout();
        }
        dataProvider.logout();
    }
}