package com.bmc.gibraltar.automation.items.component;

import com.bmc.gibraltar.automation.framework.utils.Bindings;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttachmentsPanel extends Bindings {
    private String attachmentsContainer = "xpath=";
    private String addedAttachments = "//a[@ng-click='fireOpen(attachment)']";
    private String addedAttachmentName = "//a[@ng-click='fireOpen(attachment)'][.='%s']";
    private String fileInput = "//input[@type='file']";
    private String removeAttachmentIcon = "//span[contains(@class, 'rx-attachment-item') " +
            "and //a[@title='%s']]//span[@class='rx-attachment-remove-icon']";
    private String loadingSpinner = "xpath=//div[contains(@class, 'cg-busy-animation')]";

    public AttachmentsPanel(WebDriver driver, String attachmentsContainer) {
        wd = driver;
        this.attachmentsContainer = "xpath=" + attachmentsContainer;
    }

    @Step
    public List<String> getAttachmentNames() {
        List<String> attachments = new ArrayList<>();
        List<WebElement> allAttachments = getElements(attachmentsContainer + addedAttachments);
        attachments.addAll(allAttachments.stream().map(this::getText).collect(Collectors.toList()));
        return attachments;
    }

    @Step
    public AttachmentsPanel addAttachment(File f) {
        log.info("Uploading an attachment with the name: " + f.getName());
        waitForElementNotPresent(loadingSpinner);
        executeJS("document.getElementsByClassName('rx-attachment-input')[0].style.position = 'static';");
        getElement(attachmentsContainer + fileInput).sendKeys(f.getAbsolutePath());
        executeJS("document.getElementsByClassName('rx-attachment-input')[0].style.position = 'absolute';");
        waitForElementPresent(String.format(attachmentsContainer + addedAttachmentName, f.getName()), 15);
        return this;
    }

    @Step
    public AttachmentsPanel removeAttachment(String attachmentName) {
        click(attachmentsContainer + String.format(removeAttachmentIcon, attachmentName));
        waitForElementNotPresent(attachmentsContainer + String.format(addedAttachmentName, attachmentName), 10);
        return this;
    }
}
