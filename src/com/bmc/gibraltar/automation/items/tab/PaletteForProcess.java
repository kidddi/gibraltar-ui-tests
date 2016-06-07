package com.bmc.gibraltar.automation.items.tab;

import com.bmc.gibraltar.automation.framework.utils.web.WebUtils;
import com.bmc.gibraltar.automation.items.CommonHandlers;
import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.element.PaletteGroup;
import com.bmc.gibraltar.automation.items.element.StencilGroup;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import org.apache.commons.lang3.text.WordUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PaletteForProcess extends Tab implements CommonHandlers {
    private static final String CLEAR_RECENT_BUTTON = " button.rx-clear-recent-stencil";
    private static final String ELEMENT_GROUP = " div.group";
    private static final String ELEMENT = " g.element";
    private static List<ElementOfDesigner> expectedElementsOnPalette;
    protected ProcessDefinitionEditorPage page;
    private String palettePanel = "//div[contains(concat('', @class, ''), 'stencil')]";
    private String searchField = "xpath=" + palettePanel + "//input[contains(@class, 'search')]";
    private Palette palette;
    private String paletteElementsInGroup = "//div[@data-name='%s']/div[@class ='elements']//*[text()]";

    public PaletteForProcess(WebDriver driver, ProcessDefinitionEditorPage page, String tabLink, String panelXpath) {
        super(driver, palettePanelContainer, tabLink, panelXpath);
        this.page = page;
    }

    /**
     * Get elements names that are displayed under concrete group @groupName
     * (or often called as categories)on Pallet
     *
     * @param group palette group
     * @return Set<String>
     */
    @Step
    public List<String> getElementsPresentInGroup(StencilGroup group) {
        List<String> elements = new ArrayList<>();
        List<WebElement> paletteElements = getListOfElements(
                panelXpath + String.format(paletteElementsInGroup, WordUtils.capitalizeFully(group.getGroupName())));
        if (group.equals(PaletteGroup.ANNOTATIONS) && isElementPresent("xpath=" + group.getExpandedGroupPath())) {
            // because the "Annotation" element does not have an label in the default and favorites tabs
            elements.add("Annotation");
        } else {
            elements.addAll(paletteElements.stream()
                    .filter((WebUtils::isElementDisplayed)).map(WebElement::getText).collect(Collectors.toList()));
        }
        if (elements.size() == 0) {
            log.warn("There is no elements on Palette under the group: " + group.getGroupName());
        }
        return elements;
    }

    @Step
    public Set<String> getElementsForGroup(PaletteGroup group) {
        return getElementsPresentInGroup(group).stream().collect(Collectors.toSet());
    }

    @Step
    public int getNumberElementsUnderGroup(PaletteGroup group) {
        return getElementsForGroup(group).size();
    }

    @Step
    public SettingsTab switchToSettingsTab() {
        SettingsTab tab = new SettingsTab(wd, container, tabLink, panelXpath);
        tab.switchToTab();
        setTab(Palette.SETTINGS_TAB);
        return tab;
    }

    /**
     * Verifies that all elements of {@param arrayElementsToVerify}
     * are present under concrete group {@param group} on Palette
     *
     * @param group                 on the Palette
     * @param arrayElementsToVerify elements that should be present
     */
    @Step
    public void verifyElementsForGroup(PaletteGroup group, ElementOfDesigner[] arrayElementsToVerify) {
        Set<String> elementsOfGroup = getElementsForGroup(group);
        Arrays.asList(arrayElementsToVerify).stream()
                .forEach(element -> verifyTrue(elementsOfGroup.contains(element.getName()),
                        "! Element '" + element + "' is not present in group: " + group.getGroupName()));
    }

    @Step
    public void searchElement(String elementName) {
        typeKeys(searchField, elementName);
    }

    public void setTab(Palette tab) {
        palette = tab;
    }

    public Palette getPaletteName() {
        return palette;
    }

    private String getCSSPath(String[] objects) {
        String cssPath = "css=";
        for (String obj : objects) {
            cssPath += obj;
        }
        return cssPath;
    }

    /**
     * Selects i amount elements (range like [1, i]) from ElementOfDesigner enum and drops them to the canvas (center).
     * Without creating ActiveElement objects.
     * Just easy and fast way.
     * BUT! Returns only <= 20 last dropped (because the "Recently Used" tab in the Stencil contains only 20 last dropped elements).
     *
     * @param count shows how many elements should to drop on the canvas.
     */
    @Step
    public ElementOfDesigner[] dropDownFirstElements(int count) {
        ElementOfDesigner[] elements = new ElementOfDesigner[]{};
        List<ElementOfDesigner> elementsArray = page.dragAndDrop(ElementOfDesigner.getFirstFew(count));
        if (expectedElementsOnPalette == null && count <= 20) {
            expectedElementsOnPalette = elementsArray;
        } else {
            expectedElementsOnPalette = expectedElementsOnPalette == null ? elementsArray : expectedElementsOnPalette;
            if (expectedElementsOnPalette.size() <= 20) {
                expectedElementsOnPalette.addAll(elementsArray);
            }
            if (expectedElementsOnPalette.size() > 20) {
                int spillOver = expectedElementsOnPalette.size() - 20;
                expectedElementsOnPalette = expectedElementsOnPalette.subList(spillOver, expectedElementsOnPalette.size());
            }
        }
        return expectedElementsOnPalette.toArray(elements);
    }

    private List<String> getAllGroupsTagsFromPalette() {
        List<WebElement> list = page.getElementList(getCSSPath(new String[]{palette.cssPath, ELEMENT_GROUP}));
        return list.stream().map(elem -> elem.getAttribute("data-name")).collect(Collectors.toList());
    }

    public List<String> getAllElementsTagsFromPalette() {
        List<WebElement> list = page.getElementList(getCSSPath(new String[]{palette.cssPath, ELEMENT_GROUP, ELEMENT}));
        List<String> values = new ArrayList<>();
        for (WebElement elem : list) {
            String elementLabel = elem.getText();
            String classAtr = elem.getAttribute("class").replaceAll("(.*//*)*", "").replaceAll("(.* )*", "");
            if (classAtr.equals("TextAnnotation")) {
                elementLabel = "Annotation";
            }
            values.add(elementLabel);
        }
        return values;
    }

    public List<ElementOfDesigner> getAllElementsFromPalette() {
        log.info("Get list of all present elements in the Palette.");
        return ElementOfDesigner.getElementsByValues(getAllElementsTagsFromPalette());
    }

    @Step
    public void verifyPaletteHasElements(ElementOfDesigner[] elements) {
        List<ElementOfDesigner> actual = getAllElementsFromPalette();
        List<ElementOfDesigner> expected = Arrays.asList(elements);
        verifyEquals(expected.size(), actual.size());
        verifyTrue(actual.containsAll(expected),
                " \n Actual Count = " + actual.size() + " \n Actual array: " + actual.toString() +
                        "\n Expected Count = " + expected.size() + " \n Expected array:  " + expected.toString());
    }

    @Step
    public void verifyPaletteHasElements(String[] elements) {
        List<String> actual = getAllElementsTagsFromPalette();
        List<String> expected = Arrays.asList(elements);
        verifyEquals(expected.size(), actual.size());
        verifyTrue(actual.containsAll(expected),
                " \n Actual Count = " + actual.size() + " \n Actual array: " + actual.toString() +
                        "\n Expected Count = " + expected.size() + " \n Expected array:  " + expected.toString());
    }

    @Step
    public void isGroupsInAlphabeticalOrder() {
        verifyTrue(isAlphabetical(getAllGroupsTagsFromPalette()));
    }

    /**
     * This method clicks on "Clear Recent" button on Recently Used Tab. Only for Recently Used Tab!
     */
    @Step
    public void clearRecentsButton() {
        click(getCSSPath(new String[]{CLEAR_RECENT_BUTTON}));
        expectedElementsOnPalette = null;
    }

    @Step
    public void verifyRecentTabElementsAmountPresent(int amount) {
        verifyEquals(getElements(getCSSPath(new String[]{palette.cssPath, ELEMENT_GROUP, ELEMENT})).size(), amount);
    }
}