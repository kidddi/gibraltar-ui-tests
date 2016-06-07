package com.bmc.gibraltar.automation.items.element;

import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;

/**
 * This class describes loop types, that are present for the process action elements
 * in the "Multi Instance Loop" group in the Inspector.
 */
public class LoopType {
    public static final LoopType PARALLEL = new LoopType("Parallel");
    public static final LoopType SEQUENTIAL = new LoopType("Sequential");
    public static final LoopType EMPTY = new LoopType("");
    private String loopType;
    private ElementProperties[] multiInstanceProperties = {INPUT_DATA_ITEM, INPUT_DATA, COMPLETION_CONDITION};

    private LoopType(String loopType) {
        this.loopType = loopType;
    }

    public String getLoopType() {
        return loopType;
    }

    /**
     * Specific icon should be displayed within {@param element} when a loop type is set
     *
     * @param element an action element on Canvas
     * @return true - if an element on Canvas contains an specific loop icon
     */
    public boolean isMultiInstanceIconPresent(ActiveElement element) {
        if (this.equals(PARALLEL)) {
            return element.isParallel();
        } else if (this.equals(SEQUENTIAL)) {
            return element.isSequential();
        } else {
            return !element.isParallel() && !element.isSequential();
        }
    }

    /**
     * If the loop type is not empty, additional fields should be present
     *
     * @return an array of the additional fields
     */
    public ElementProperties[] getMultiInstanceFields() {
        if (this.equals(PARALLEL) || this.equals(SEQUENTIAL)) {
            return multiInstanceProperties;
        } else {
            return new ElementProperties[]{};
        }
    }
}
