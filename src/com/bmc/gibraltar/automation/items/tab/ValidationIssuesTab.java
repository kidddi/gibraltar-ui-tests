package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.items.CommonEnumInterface;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.pages.BasePage;
import com.bmc.gibraltar.automation.pages.HasValidator;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

public class ValidationIssuesTab extends InspectorTab {
    private static String validationTabLink = "//a[@tab-name = 'rightBladeConfiguration.tabs.validationIssues.name']";
    private static String validationPanelXpath = "xpath=//*[contains(@class, 'rx-validation-issues ng-scope')]";
    private String validIssuesHeader = "xpath=//span[contains(text(),'Validation Issues')]";
    private String refreshButton = "xpath=//div[contains(concat('', @class, ''), 'rx-refresh-validation-issues')]/span";
    private String issueMessage = "xpath=//h3[.='%s']/..//p[text()[contains(.,'%s')]][contains(concat('', @class, ''), " +
            "'rx-validation-issue-message')]";
    private String issue = "//*[text()[contains(.,'%s')]]";
    private String correctIssueLink = "xpath=//*[@class='rx-validation-issues ng-scope']" +
            "//*[text()[contains(.,'%s')]]/..//*[@ng-click='correctValidationIssue(validationIssue)']";
    private String correctLink = "xpath=//a[.='Correct']";
    private BasePage page;

    public <T extends BasePage & HasValidator> ValidationIssuesTab(WebDriver driver, HasValidator page) {
        super(driver, validationTabLink, validationPanelXpath);
        this.page = (T) page;
    }

    /**
     * Method clicks on Validation Issues Tab
     */
    @Step
    public ValidationIssuesTab clickToValidationTab() {
        int i = 0;
        while (!isElementPresent(validIssuesHeader) || i > 5) {
            click("xpath=" + validationTabLink);
            i++;
        }
        return this;
    }

    /**
     * Method verifies, that checked group is exist on Validation Issues Tab
     * (Applies ONLY for ProcessDesigner)
     *
     * @param element Active Element, which is checked on displaying it`s group (correct name)
     */
    public ValidationIssuesTab verifyIssueGroupPresent(ActiveElement element) {
        verifyIssueGroupPresent(element.getName());
        return this;
    }

    public ValidationIssuesTab verifyIssueGroupPresent(String groupName) {
        log.info("Verifying if the '" + groupName + "' group is present in the Validation Issues Tab.");
        String locator = String.format(validationPanelXpath + "//*[.='%s']", groupName);
        clickToValidationTab();
        verifyTrue(isElementPresent(locator));
        return this;
    }

    private boolean isIssueExist(CommonEnumInterface element, String issue) {
        clickToValidationTab();
        return isElementPresent(String.format(issueMessage, element.getName(), issue));
    }

    private boolean isIssueExist(String issueText) {
        clickToValidationTab();
        return isElementPresent(validationPanelXpath + String.format(issue, issueText));
    }

    /**
     * Method verifies, that validation issue exists on Validation Issues Tab
     *
     * @param issueText Common or particular validation fraze, which should displayed on Validation Issues Tab
     */
    @Step
    public ValidationIssuesTab verifyIssueExists(String issueText) {
        verifyTrue(isIssueExist(issueText));
        return this;
    }

    /**
     * This method verifies if the specified issue is present for the element (Applies ONLY for ProcessDesigner)
     *
     * @param element for which an issue should be present
     * @param issue   text of the issue
     */
    @Step
    public ValidationIssuesTab verifyIssueExists(CommonEnumInterface element, String issue) {
        verifyTrue(isIssueExist(element, issue));
        return this;
    }

    /**
     * This method verifies if the specified issue is not present for the element
     *
     * @param element for which an issue should not be present
     * @param issue   text of the issue
     */
    @Step
    public ValidationIssuesTab verifyIssueNotExists(CommonEnumInterface element, String issue) {
        assertFalse(isIssueExist(element, issue));
        return this;
    }

    @Step
    public ValidationIssuesTab verifyIssueNotExists(String issueText) {
        assertFalse(isIssueExist(issueText));
        return this;
    }

    /**
     * Clicks on "Correct" button and verify, that button has worked correctly, pointing to non-valid data.
     *
     * @param element Active Element, which is checked on displaying and works button.
     */
    public ValidationIssuesTab verifyСlickCorrectButton(ActiveElement element) {
        ProcessDefinitionEditorPage editView = (ProcessDefinitionEditorPage) page;
        InspectorTab inspectorTab = editView.getElementPropertiesTab();
        String locator = String.format(correctIssueLink, element.getName());
        clickToValidationTab();
        click(locator);
        switch (element.getType()) {
            case END:
            case START:
            case PARALLEL_GATEWAY:
            case EXCLUSIVE_GATEWAY:
                verifyTrue(element.isCurrentElementActivated());
                inspectorTab.verifyPropertyValue(InspectorGroup.PROPERTIES, "Label:", "");
                break;
            default:
                verifyTrue(element.isCurrentElementActivated());
                inspectorTab.verifyPropertyValue(InspectorGroup.PROPERTIES, "Label:", element.getName());
                break;
        }
        return this;
    }

    @Step
    public void verifyValidationTabHasWarnings() {
        verifyTrue(isValidationTabContainWarnings());
    }

    @Step
    public void verifyValidationTabHasNoErrors() {
        verifyFalse(isValidationTabContainErrors());
    }

    @Step
    public ValidationIssuesTab refreshIssuesList() {
        click(refreshButton);
        return this;
    }

    public boolean isValidationTabContainErrors() {
        return getElement("xpath=" + validationTabLink + "/div").getAttribute("class").contains("error");
    }

    public boolean isValidationTabContainWarnings() {
        return getElement("xpath=" + validationTabLink + "/div").getAttribute("class").contains("warning");
    }

    public void clickCorrectLink() {
        click(correctLink);
    }
}