package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class WarningDialog<T extends BasePage> extends BasePage {

    @FindBy(css = "h5.d-modal__title")
    private WebElement dialogHeader;

    @FindBy(css = ".modal-content .d-button_primary")
    private WebElement okButton;

    @FindBy(css = ".modal-content .d-button_secondary")
    private WebElement cancelButton;

    private T parentPage;

    public WarningDialog(WebDriver driver, T parentPage) {
        super(driver);
        this.parentPage = parentPage;
    }

    @Override
    public boolean isPageLoaded() {
        return WebUtils.isElementDisplayed(dialogHeader);
    }

    public T clickOkButton() {
        okButton.click();
        return parentPage;
    }

    public T clickCancelButton() {
        cancelButton.click();
        return parentPage;
    }
}
