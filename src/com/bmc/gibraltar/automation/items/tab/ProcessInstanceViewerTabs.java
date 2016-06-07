package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.pages.ProcessViewerPage;
import org.openqa.selenium.WebDriver;

public class ProcessInstanceViewerTabs extends InspectorTab {
    protected ProcessViewerPage page;

    /**
     * This constructor can be used for any tab of Inspector for Process Instance Viewer (as in these tabs we only read data, no filling/typing )
     */
    public ProcessInstanceViewerTabs(WebDriver driver, ProcessViewerPage procInstancePage, ProcessInstanceViewerTabs.Tab tab) {
        super(driver, tab.getTabLink(), tab.getTabPath());
        this.page = procInstancePage;
    }

    public enum Tab {
        PROCESS_INFORMATION("Process Information", "rx-blade-process-info-tab", "processInfo"),
        PROCESS_VARIABLES("Process Variables", "rx-blade-process-property-tab", "processVariables"),
        ACTIVITY_RESULTS("Activity Results", "rx-blade-process-element-tab", "activityResults");
        String name;
        String tabLink;
        String tabPath;

        Tab(String name, String tabLink, String tabPath) {
            this.name = name;
            this.tabLink = tabLink;
            this.tabPath = tabPath;
        }

        public String getName() {
            return name;
        }

        public String getTabLink() {
            return "//a[contains(@class,'" + tabLink + "')]";
        }

        public String getTabPath() {
            return "xpath=//*[@ng-model='" + tabPath + "']";
        }
    }
}
