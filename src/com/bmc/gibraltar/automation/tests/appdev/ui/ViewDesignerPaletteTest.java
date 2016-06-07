package com.bmc.gibraltar.automation.tests.appdev.ui;

import com.bmc.gibraltar.automation.items.element.ViewDesignerStencilGroup;
import com.bmc.gibraltar.automation.items.tab.Palette;
import com.bmc.gibraltar.automation.items.tab.PaletteForView;
import com.bmc.gibraltar.automation.pages.ViewDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ViewDefinitionsPage;
import com.bmc.gibraltar.automation.tests.AppManagerBaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ViewDesignerPaletteTest extends AppManagerBaseTest {
    private ViewDefinitionEditorPage viewDesigner;
    private PaletteForView palette;
    private String viewDefinitionName = "Edit Task";

    @BeforeClass
    public void openView() {
        log.info("BeforeClass start");
        ViewDefinitionsPage viewDefinitionsPage = new ViewDefinitionsPage(wd, "task-manager").navigateToPage();
        viewDesigner = viewDefinitionsPage.openView(viewDefinitionName);
        palette = viewDesigner.toPalette(Palette.DEFAULT_VIEW_DESIGNER_TAB);
        log.info("BeforeClass end");
    }

    @AfterClass(alwaysRun = true)
    public void closeDesigner() {
        log.info("AfterClass start");
        viewDesigner.close();
        log.info("AfterClass end");
    }

    @Features("P528 - Process owner tailors view")
    @Stories("US206674:'Basic View Designer'")
    @Test
    public void viewDefinitionNameInHeader() {
        assertThat("View definition name is wrong", viewDesigner.getViewDefinitionName(), equalTo(viewDefinitionName));
    }

    @Features("P528 - Process owner tailors view")
    @Stories("US206676:'View Designer Palette'")
    @Test
    public void presenceDeployedComponents() {
        palette.verifyComponentsPresent(
                "Container",
                "Record Instance Editor",
                "Activity Feed");
    }

    @Features("P528 - Process owner tailors view")
    @Stories("US206676:'View Designer Palette'")
    @Test
    public void validateGroupsComponents() {
        palette.verifyGroupsPresence(new ViewDesignerStencilGroup[]{ViewDesignerStencilGroup.BASIC_COMPONENTS});
    }

    @Features("P528 - Process owner tailors view")
    @Stories("US206676:'View Designer Palette'")
    @Test
    public void basicComponentsPresence() {
        palette.verifyComponentsPresentForGroup(ViewDesignerStencilGroup.BASIC_COMPONENTS,
                "Attachment Panel",
                "Container",
                "Record Grid",
                "Record Instance Editor");
    }

    @Features("P528 - Process owner tailors view")
    @Stories("US206676:'View Designer Palette'")
    @Test
    public void componentOnPaletteSortedAlphabetically() {
        List<String> actualBasicComponents = palette.getComponentNamesForGroup(ViewDesignerStencilGroup.BASIC_COMPONENTS);
        assertTrue(isAlphabetical(actualBasicComponents));
    }
}