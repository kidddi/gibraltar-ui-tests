package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.items.CommonHandlers;
import com.bmc.gibraltar.automation.items.component.Component;
import com.bmc.gibraltar.automation.items.element.StencilGroup;
import com.bmc.gibraltar.automation.pages.ViewDefinitionEditorPage;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.List;

public class PaletteForView extends Tab {
    private String components = "xpath=//div[@class='rx-view-component ng-scope']";
    private String componentName = components + "//span[contains(@class,'view-component-name')]";
    private String componentsNameByGroup = "xpath=//span[contains(@class,'view-component-name') and " +
            "ancestor-or-self::div[contains(@class,'view-components-group')]//span[.='%s']]";
    private ViewDefinitionEditorPage designerPage;

    public PaletteForView(WebDriver driver, ViewDefinitionEditorPage page, String tabLink, String panelXpath) {
        super(driver, palettePanelContainer, tabLink, panelXpath);
        this.designerPage = page;
    }

    /**
     * @return names for all Components present on Palette (under all groups)
     */
    public List<String> getAllComponentNames() {
        return designerPage.getListOfWebElementsTextByLocator(componentName);
    }

    /**
     * @param group
     * @return names for Components present on Palette under specified group
     */
    public List<String> getComponentNamesForGroup(StencilGroup group) {
        return designerPage.getListOfWebElementsTextByLocator(
                String.format(componentsNameByGroup, group.getGroupName()));
    }

    /**
     * Is needed for a common root class Tab (to represent locator of expanded Palette Groups)
     *
     * @return locator for all Groups of Palette
     */
    @Override
    protected String getGroupPath() {
        return panelXpath + "//span[contains(@class,'components-group-title')]";
    }

    /**
     * Is needed for a common root class Tab(to represent locator of collapsed Palette Groups)
     *
     * @return locator for all Groups of Palette
     */
    protected String getClosedGroupPath() {
        return panelXpath;
    }

    /**
     * Verifies, that array of expectedComponents with type Components are present among all groups on Palette
     *
     * @param expectedComponents
     */
    @Step
    public void verifyComponentsPresent(Component... expectedComponents) {
        for (Component comp : expectedComponents) {
            verifyComponentsPresent(comp.getName());
        }
    }

    /**
     * Verifies, that array of expectedComponents with type String are present among all groups on Palette
     *
     * @param expectedComponents
     */
    @Step
    public void verifyComponentsPresent(String... expectedComponents) {
        for (String comp : expectedComponents) {
            verifyTrue(CommonHandlers.contains(comp, getAllComponentNames().toArray())
                    , "Expected Component [" + comp + "] is NOT present on Palette");
        }
    }

    /**
     * Verifies, that array of expectedComponents with type String are present on Palette under PaletteGroup group
     *
     * @param expectedComponents
     */
    @Step
    public void verifyComponentsPresentForGroup(StencilGroup group, String... expectedComponents) {
        for (String component : expectedComponents) {
            verifyTrue(CommonHandlers.contains(component, getComponentNamesForGroup(group).toArray())
                    , "Expected Component [" + component + "] is NOT present on under Palette Group: "
                    + group.getGroupName());
        }
    }
}