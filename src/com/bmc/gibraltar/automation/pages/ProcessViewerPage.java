package com.bmc.gibraltar.automation.pages;

import com.bmc.gibraltar.automation.items.tab.ProcessInstanceViewerTabs;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

import static com.jayway.restassured.path.json.JsonPath.from;

public class ProcessViewerPage extends BaseEditorPage {
    private String elemActivity = "xpath=//*[contains(@class, 'element rx')]//div[.='%s']";
    private String elemSkin = "xpath=(//*[contains(@class,'body outer') " +
            "and ancestor-or-self::*[contains(@class, 'element rx')]//div[.='%s']])[1]";
    private String pageTitle = header + "/div[contains(text(),'Process Instance')]";
    private String processViewerContainer = "xpath=//div[contains(@class, 'rx-process-instance-details')]";

    public ProcessViewerPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void checkSuccessMessageWhileSavingDisplayed() {
    }

    @Override
    public boolean isPageLoaded() {
        return isElementPresent(pageTitle);
    }

    @Step
    public ProcessViewerPage verifyProcName(String name) {
        verifyTrue(getText(pageTitle).equals("Process Instance " + name));
        return this;
    }

    @Step
    public ProcessViewerPage verifyElementByLabel(String label) {
        verifyTrue(isElementPresent(String.format(elemActivity, label)));
        return this;
    }

    @Step
    public ProcessViewerPage verifyElementIsActive(String elmLabel) {
        verifyTrue(isElmHighlightedWithColor(elmLabel, ActivityColor.GREEN));
        return this;
    }

    @Step
    public ProcessViewerPage verifyElementActivityDone(String elmLabel) {
        verifyTrue(isElmHighlightedWithColor(elmLabel, ActivityColor.GREY));
        return this;
    }

    @Step
    public ProcessViewerPage verifyElementActivityNotStarted(String elmLabel) {
        verifyTrue(isElmHighlightedWithColor(elmLabel, ActivityColor.NONE));
        return this;
    }

    @Step
    public boolean isElmHighlightedWithColor(String label, ActivityColor color) {
        String colorId = getElement(String.format(elemSkin, label))
                .getAttribute("stroke");
        String expected;
        switch (color) {
            case GREEN:
                expected = "#89c341"; /* after DPL implemented might be changed on UI */
                break;
            case GREY:
                expected = "#999999"; /* after DPL implemented might be changed on UI */
                break;
            case NONE:
            default:
                expected = "#000000";
        }
        return colorId.equals(expected);
    }

    @Step
    public void verifyProcessViewerContentIsReadonly() {
        verifyTrue(null != getElement(designerJsonTextarea).getAttribute("readonly"));
    }

    @Step
    public void verifyClosingProcessReturnsToManageTab() {
        ManageTabPage mngTab = this.close();
        sleep(3);
        verifyTrue(mngTab.isPageLoaded());
    }

    @Step("Close Process Instance Viewer")
    public ManageTabPage close() {
        click(closeButton);
        return new ManageTabPage(wd);
    }

    @Step
    public ProcessInstanceViewerTabs toInspectorTab(ProcessInstanceViewerTabs.Tab tab) {
        ProcessInstanceViewerTabs inspectorTab = new ProcessInstanceViewerTabs(wd, this, tab);
        inspectorTab.switchToTab();
        return inspectorTab;
    }

    @Step
    public ProcessInstanceViewerTabs toActivityResultsTab(String activityLabel) {
        click(String.format(elemActivity, activityLabel));
        return toInspectorTab(ProcessInstanceViewerTabs.Tab.ACTIVITY_RESULTS);
    }

    /**
     * This method extracts the instanceId from the JSon that is presented in the "Process Viewer" page
     *
     * @return
     */
    public String getInstanceId() {
        String processViewerContent = (getElement(designerJsonTextarea).getAttribute("value"));
        return from(processViewerContent).get("instanceId");
    }

    public boolean isProcessInstanceViewerPresent() {
        return isElementPresent(processViewerContainer);
    }

    public boolean isJsonEditorPresent() {
        return isElementPresent(designerJsonTextarea);
    }

    /**
     * Verifies that "instanceId" property in the "Process Viewer" page equals to {@instanceId}
     *
     * @param instanceId
     */
    public void verifyProcessInstanceId(String instanceId) {
        verifyEquals(getInstanceId(), instanceId);
    }

    public enum ActivityColor {
        GREEN,
        GREY,
        NONE
    }
}