package com.bmc.gibraltar.automation.items.component.runtime;

import com.bmc.gibraltar.automation.framework.utils.Bindings;
import com.bmc.gibraltar.automation.items.component.Component;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecordEditor extends Bindings {
    private String recordInsContainer = "xpath=" + Component.RECORD_INSTANCE_EDITOR.getLocatorInRuntime();
    private String fieldsLabelInRunTime = "//label[@class='d-textfield__label']";
    private String recordInsLabelPathInRunTime = "//div[contains(@class, '__required-field') and ./label/span[.='%s:']]";

    public RecordEditor(WebDriver driver) {
        wd = driver;
    }

    public String getRecordInsContainer() {
        return recordInsContainer;
    }

    @Step
    public List<String> getListOfFields() {
        List<String> visibleFields = new ArrayList<>();
        List<WebElement> fields = getElements(recordInsContainer + fieldsLabelInRunTime);
        visibleFields.addAll(fields.stream().map(we
                -> we.findElement(By.tagName("span")).getText()).collect(Collectors.toList()));
        return visibleFields;
    }

    @Step
    public boolean isFieldRequired(String fieldInRunTime) {
        String labelPath = String.format(recordInsLabelPathInRunTime, fieldInRunTime);
        return isElementPresent(recordInsContainer + labelPath);
    }
}
