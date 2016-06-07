package com.bmc.gibraltar.automation.items;

import com.bmc.gibraltar.automation.framework.utils.Bindings;
import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static java.lang.String.format;

/**
 * Example:
 * <p/>
 * Test sample:
 * Icon icon = procEditor.forIcon(infoIconLocator)
 * .verifyPresentOn(processInformationLocator)
 * .verifyContainsTextOnClick(infoIconText);
 */

public class Icon extends Bindings {

    private By icon;

    public Icon(String icon, WebDriver driver) {
        wd = driver;
        this.icon = byLocator(icon);
    }

    private boolean isIconPresentOn(String iconLocatedOnThis) {
        WebElement placeForIcon = getElement(iconLocatedOnThis);
        return WebUtils.isElementDisplayed(placeForIcon.findElement(icon));
    }

    public Icon verifyIconPresentOn(String iconLocatedOnThis) {
        verifyTrue(isIconPresentOn(iconLocatedOnThis), format("Icon %s is not present on %s", icon.toString(), iconLocatedOnThis));
        return this;
    }

    public Icon verifyIconNOTPresentOn(String iconLocatedOnThis) {
        verifyTrue(!isIconPresentOn(iconLocatedOnThis), format("Icon %s is present on %s", icon.toString(), iconLocatedOnThis));
        return this;
    }

    public Icon verifyIconContainsTextOnClick(String buttonText) {
        click(icon);
        String realButtonText = getElement(icon).getText();
        verifyEquals(buttonText, realButtonText,
                format("Actual: %s.But Expected: %s.", realButtonText, realButtonText));
        return this;
    }
}