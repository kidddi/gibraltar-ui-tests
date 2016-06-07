package com.bmc.gibraltar.automation.items.component;

import com.bmc.gibraltar.automation.items.DesignerElement;
import com.bmc.gibraltar.automation.pages.ViewDefinitionEditorPage;

/**
 * This class is for Components, that are activated on View Designer Canvas during their editing
 */
public class ActiveComponent extends DesignerElement {
    private Component componentType;
    private String viewComponentId;
    private ViewDefinitionEditorPage viewDesigner;

    public ActiveComponent(Component componentType, String viewComponentId, ViewDefinitionEditorPage viewDesigner) {
        this.viewDesigner = viewDesigner;
        this.componentType = componentType;
        this.viewComponentId = viewComponentId;
    }

    @Override
    public ViewDefinitionEditorPage getPage() {
        return viewDesigner;
    }

    public Component getComponentType() {
        return componentType;
    }

    public String getName() {
        return componentType.getName();
    }

    public String getXPath() {
        return componentType.getLocatorOnCanvas() + "[@rx-view-component-id='" + viewComponentId + "']";
    }
}
