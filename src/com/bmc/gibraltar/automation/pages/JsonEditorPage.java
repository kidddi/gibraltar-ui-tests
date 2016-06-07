package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.framework.utils.PropertiesUtils;
import com.bmc.gibraltar.automation.framework.utils.web.Browser;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

public class JsonEditorPage extends BaseEditorPage {

    public JsonEditorPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void checkSuccessMessageWhileSavingDisplayed() {
    }

    @Override
    public boolean isPageLoaded() {
        return waitForElementPresent(designerJsonContainer, 5);
    }

    @Step
    public String getJsonText() {
        return getValue(designerJsonTextarea);
    }

    /**
     * In Firefox Json textarea has attribute 'ng-readonly', but in Chrome it's 'readonly'
     */
    @Step
    public boolean isJsonEditorReadonly() {
        if (PropertiesUtils.getBrowser().equals(Browser.FF)) {
            return null == getAttr(designerJsonTextarea, "ng-readonly");
        } else {
            return !isElementReadOnly(designerJsonTextarea);
        }
    }

    @Step
    public boolean isJsonEditorViewEnabled() {
        return isElementPresent(designerJsonContainer);
    }
}