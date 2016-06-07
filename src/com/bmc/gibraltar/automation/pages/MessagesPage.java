package com.bmc.gibraltar.automation.pages;

import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

public class MessagesPage extends TaskManagerHomePage {
    public String pageHeader = "xpath=//h1[text()='My Messages']";
    public String messagesCounter = "xpath=//a[@ui-sref='tms.messages']";

    public MessagesPage(WebDriver driver) {
        super(driver);
        tabName = "Messages";
    }

    @Override
    public String getPageUrl() {
        return TASK_MANAGER_URL + "/tms/messages";
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(pageHeader);
    }

    public int getMessagesCount() {
        return Integer.parseInt(getText(messagesCounter));
    }

    @Step
    public void verifyMessagesCount(int expectedCount) {
        sleep(3);
        int actualCountOfMessages = getMessagesCount();
        verifyEquals(actualCountOfMessages, expectedCount, "Expected " + expectedCount + ", but found " + actualCountOfMessages + " messages.");
    }
}