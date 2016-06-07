package com.bmc.gibraltar.automation.dataprovider;

import org.testng.annotations.DataProvider;

public class UserDataProvider {

    @DataProvider(name = "allCredentials")
    private static Object[][] allCredentials() {
        return new Object[][]{
                new Object[]{"jonnie@pepsi.com", "password", new String[]{"Administrator"}},
                new Object[]{"ajay@pepsi.com", "password", new String[]{"Process Designer", "Struct Admin"}},
                new Object[]{"jim@pepsi.com", "password", new String[]{"Task User", "Service Desk"}},
                new Object[]{"manager@pepsi.com", "password", new String[]{"Process Manager"}},
                new Object[]{"designer@pepsi.com", "password", new String[]{"Process Designer"}},
                new Object[]{"serviceDesk@pepsi.com", "password", new String[]{"Service Desk"}},
        };
    }
}
