package com.bmc.gibraltar.automation.tests;

import com.bmc.gibraltar.automation.framework.autodb.RuntimeDescription;
import com.bmc.gibraltar.automation.framework.entities.User;
import com.bmc.gibraltar.automation.framework.utils.Bindings;
import com.bmc.gibraltar.automation.framework.utils.PropertiesUtils;
import com.bmc.gibraltar.automation.framework.utils.web.WebDriverManager;
import com.bmc.gibraltar.automation.items.CommonHandlers;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public class BaseTest extends Bindings implements RuntimeDescription, CommonHandlers {
    protected static User user = new User(PropertiesUtils.getUsername(), PropertiesUtils.getPassword());
    protected final Logger log = Logger.getLogger(getClass());
    /**
     * Test description - used for data-driven tests to provide a unique description
     */
    protected String testDescription = null;

    /**
     * Starts WebDriver.
     */
    @BeforeClass
    protected void init() {
        log.info("Setting up preconditions in BeforeClass\n");
        wd = WebDriverManager.getWebDriver();
        log.info("Preconditions are set\n");
    }

    @BeforeMethod(alwaysRun = true)
    protected void clearTestDescription() {
        testDescription = null;
    }

    @Override
    public String getTestDescription() {
        return testDescription;
    }

    /**
     * Closes WebDriver
     */
    @AfterClass(alwaysRun = true)
    protected void quit() {
        log.info("Going to shut down in AfterClass\n");
        wd.quit();
        log.info("Shut down completed in AfterClass\n");
    }
}
