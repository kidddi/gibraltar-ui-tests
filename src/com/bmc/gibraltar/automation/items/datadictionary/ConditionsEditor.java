package com.bmc.gibraltar.automation.items.datadictionary;

import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.items.element.Property;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConditionsEditor extends DataDictionary {
    private String useDynamicQualificationBtn = "xpath=//div[contains(., 'Use Dynamic Qualification')]//input";
    private String disabledButton = operatorsButton + "[@disabled='disabled']";
    private List<String> operatorsButtons = Arrays.asList("(", ")", "+", "-", "*", "/", "%", ">", "<", "=", "!=", ">=",
            "<=", "AND", "OR", "NOT", "NULL", "LIKE");

    public ConditionsEditor(WebDriver driver, ActiveElement element, Property property) {
        super(driver, element, property);
        isExpressionBuilder = true;
        header = String.format(header, CONDITIONS_EXPRESSION);
    }

    /**
     * Verifies if operator buttons should be enabled/disabled in the Condition Editor.
     *
     * @param shouldBeEnabled true - operator buttons should be enabled;
     *                        false - operator buttons should be disabled;
     * @return this
     */
    @Step
    public ConditionsEditor verifyOperatorButtonsEnabling(boolean shouldBeEnabled) {
        List<WebElement> disabledOperators = getElements(disabledButton).stream()
                .filter(WebElement::isDisplayed).collect(Collectors.toList());
        if (shouldBeEnabled) {
            verifyTrue(disabledOperators.isEmpty(), "Operator buttons are disabled!");
        } else {
            verifyEquals(disabledOperators.size(), operatorsButtons.size(), "Operator buttons are enabled!");
        }
        return this;
    }

    public DataDictionary clickUseDynamicQualification() {
        click(useDynamicQualificationBtn);
        return this;
    }
}
