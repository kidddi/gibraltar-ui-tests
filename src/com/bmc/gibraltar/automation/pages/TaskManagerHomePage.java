package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.framework.utils.web.AngularJs;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.List;

import static com.bmc.gibraltar.automation.items.ActionBar.Action.REFRESH;

public abstract class TaskManagerHomePage extends TabPage {
    protected String lnkManage = "xpath=//a[.='Manage Processes']";
    protected String lnkConfigure = "xpath=//a[.='Process Definitions']";
    protected String gridContainer = "xpath=//rx-grid";
    protected String checkboxForRow = "xpath=//label[contains(concat(' ', @class, ' '), 'cellSelectionLabel') " +
            "and ancestor-or-self::div[contains(@class, 'ng-scope ngRow')]//a[.='%s']]";
    protected String checkboxForAllRows = "xpath=//label[contains(@class, 'headerSelectionLabel')]";
    protected String entityNamesInGridView = "xpath=//div[contains(@class, 'col1')]//a[contains(@ui-sref,'row.entity.name')]";
    protected String tabNames = "xpath=//ul[@class='d-n-menu']//a[contains(concat('', @class, ''), 'link')]";

    public TaskManagerHomePage(WebDriver driver) {
        super(driver);
    }

    public TaskManagerHomePage(WebDriver driver, String bundle) {
        super(driver, bundle);
    }

    @Step
    public List<String> getAllTabsNames() {
        return getListOfWebElementsTextByLocator(tabNames);
    }

    @Step
    public void refresh() {
        actionBar(REFRESH).click();
        AngularJs.waitForAngularRequestsToFinish(wd);
    }

    @Step
    public LoginPage logout() {
        click(headerProfileArea);
        click(logoutLink);
        LoginPage loginPage = new LoginPage(wd);
        loginPage.waitForPageLoaded();
        return loginPage;
    }
}