package com.bmc.gibraltar.automation.items.element;

import com.bmc.gibraltar.automation.items.CommonEnumInterface;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import com.bmc.gibraltar.automation.pages.BasePage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;

import java.util.*;

import static com.bmc.gibraltar.automation.items.element.CommonGroupedElements.*;
import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.*;
import static java.lang.String.format;

public enum ElementProperties implements Property {
    NAME("Name:", EMPTY, InspectorGroup.PROPERTIES, "name", "name"),
    LABEL("Label:", ALL, InspectorGroup.PROPERTIES, "label", "label"),
    DESCRIPTION("Description:", ALL, InspectorGroup.PROPERTIES, "description", "description"),
    X("X:", ALL, InspectorGroup.GEOMETRY, "position/x", "450"),
    Y("Y:", ALL, InspectorGroup.GEOMETRY, "position/y", "350"),
    WIDTH("Width:", ALL, InspectorGroup.GEOMETRY, "width", "70"),
    HEIGHT("Height:", ALL, InspectorGroup.GEOMETRY, "height", "70"),
    OUTPUT_MAP_NAME("Name:", EMPTY, InspectorGroup.OUTPUT_MAP, "assignTarget", "baseCampTaskIdCreated"),//
    OUTPUT_MAP_SOURCE("Source:", EMPTY, InspectorGroup.OUTPUT_MAP, "expression", "${activityResults.CreateTodoAction.createBasecampTodoOut}"),
    CALLED_PROCESS("Called Process:", CALL_ACTIVITY, InspectorGroup.PROPERTIES, "calledProcessId", "Wait For Approval"),
    PARAMETER_NAME("Parameter Name:", RECEIVE_TASK, InspectorGroup.SIGNAL_PARAMETERS, "signalParams", "1"),//
    RECORD_DEFINITION("Record Definition:", USER_TASK, InspectorGroup.PROPERTIES, "recordDefinitionName", "Task"),
    COMPLETION_CRITERIA("Completion Criteria:", BUILD_COMPLETION_AND_USER_TASK, InspectorGroup.PROPERTIES, "completeTaskCondition", "${userTask.Status} = \"Assigned\""),
    COMPLETION_CRITERIA_PARAMETER("Completion Criteria Parameter:", BUILD_COMPLETION_CONDITION, InspectorGroup.INPUT_MAP, "completionCriteriaParameter", "\"Assigned\""),
    APPROVER_LIST("Approver List:", BUILD_COMPLETION_CONDITION_AND_APPROVALS, InspectorGroup.INPUT_MAP, "approverList", "12"),
    REQUEST_ID("Request Id:", BUILD_COMPLETION_CONDITION, InspectorGroup.INPUT_MAP, "requestId", "11"),
    RUN_AS("Run as:", ACTIONS, InspectorGroup.PROPERTIES, "runAsUser", "Inherit from Process"),

    // APPROVALS
    APPLICATION_RECORD_INSTANCE("Application Record Instance:", APPROVAL_PROCESS, InspectorGroup.INPUT_MAP, "inputMap/Application Record Instance", "\"default value\""),
    APPROVAL_FLOW_ID("Approval Flow Id:", APPROVAL_PROCESS, InspectorGroup.INPUT_MAP, "inputMap/Approval Flow Id", "\"default value\""),
    APPROVAL_SUMMARY_ID("ApprovalSummary_ID:", WAIT_FOR_APPROVAL, InspectorGroup.INPUT_MAP, "inputMap/ApprovalSummary_ID", "\"default value\""),
    APP_RECORD_INSTANCE_ID("AppRecordInstanceID:", WAIT_FOR_APPROVAL, InspectorGroup.INPUT_MAP, "inputMap/AppRecordInstanceID", "\"default value\""),
    APPROVER_LIST_FOR_WAIT_APPROVAL("ApproverList:", WAIT_FOR_APPROVAL, InspectorGroup.INPUT_MAP, "inputMap/ApproverList", "\"default value\""),

    //BASECAMP
    DEFINITION_NAME("Definition Name:", BASECAMP, InspectorGroup.INPUT_MAP, "definitionName", "\"Task\""),
    NAME_BCMP("Name:", CREATE_BASECAMP_PROJECT, InspectorGroup.INPUT_MAP, "name", "1"),
    BC_DESCRIPTION("Description:", HAVE_DESCRIPTION, InspectorGroup.INPUT_MAP, "description", "1"),
    PROJECT_ID("Project Id:", HAVE_PROJECT_ID, InspectorGroup.INPUT_MAP, "projectId", "1"),
    TODO_LIST_ID("Todo List Id:", HAVE_TODO_LIST_ID, InspectorGroup.INPUT_MAP, "todoListId", "1"),
    ASSIGNEE_ID("Assignee Id:", CREATE_BASECAMP_TO_DO, InspectorGroup.INPUT_MAP, "assigneeId", "1"),
    CONTENT("Content:", CREATE_BASECAMP_TO_DO, InspectorGroup.INPUT_MAP, "content", "1"),
    DUE_AT("Due At:", CREATE_BASECAMP_TO_DO, InspectorGroup.INPUT_MAP, "dueAt", "1"),
    ASSIGNEE_TYPE("Assignee Type:", CREATE_BASECAMP_TO_DO, InspectorGroup.INPUT_MAP, "assigneeType", "1"),
    TODO_LIST_NAME("Todo List Name:", CREATE_BASECAMP_TO_DO_LIST, InspectorGroup.INPUT_MAP, "todoListName", "1"),

    //TASK
    PRIORITY("Priority:", TASK, InspectorGroup.INPUT_MAP, "priority", "\"Critical\""),
    STATUS("Status:", TASKS, InspectorGroup.INPUT_MAP, "status", "\"Assigned\""),
    SUBMITTER("Submitter:", TASK, InspectorGroup.INPUT_MAP, "submitter", "$\\USER$"),
    SUMMARY("Summary:", TASK, InspectorGroup.INPUT_MAP, "summary", "1"),
    ASSIGNED_TO("Assigned To:", TASK, InspectorGroup.INPUT_MAP, "assignedTo", "\\USER$"),
    NOTES("Notes:", TASK, InspectorGroup.INPUT_MAP, "notes", "1"),
    TASK_NAME("Task Name:", TASK, InspectorGroup.INPUT_MAP, "taskName", "1"),
    TASK_INSTANCE_ID("Task Instance Id:", UPDATE_TASK_STATUS, InspectorGroup.INPUT_MAP, "taskInstanceId", "1"),

    //ANNOTATION
    NOTES_ANNOTATION("Notes:", ANNOTATION, InspectorGroup.PROPERTIES, "rxData/text", "1"),

    //EXPRESSIONS
    EXPRESSION_TEXT("Expression Text:", EVALUATE_EXPRESSION, InspectorGroup.INPUT_MAP, "expressionText", "1"),

    //SEND_MESSAGE
    SUBJECT("Subject:", MESSAGE, InspectorGroup.INPUT_MAP, "subject", "1"),
    BODY("Body:", MESSAGE, InspectorGroup.INPUT_MAP, "body", "1"),
    RECIPIENTS("Recipients:", MESSAGE, InspectorGroup.INPUT_MAP, "recipients", "\"mail@com.ua\""),
    TEMPLATE_NAME("Template Name:", SEND_MESSAGE_BY_TEMPL, InspectorGroup.INPUT_MAP, "templateName", "\"mail@com.ua\""),

    //RECORDS
    RECORD_DEFINITION_NAME("Record Definition Name:", RECORDS_AND_MESSAGE, InspectorGroup.INPUT_MAP, "inputMap/recordDefinitionName", "Task"),
    VALUES("Values:", RECORDS, InspectorGroup.INPUT_MAP, "values", "1"),
    RECORD_INSTANCE_ID("Record Instance Id:", RECORD_INSTANCE_AND_MESSAGE_AND_USER_TASK, InspectorGroup.NON, "recordInstanceId", "$PROCESSCORRELATIONID$"),
    DYNAMIC_PROPERTY("", EMPTY, InspectorGroup.INPUT_MAP, "", "1"),

    //MULTI INSTANCE LOOP
    LOOP_TYPE("Loop Type:", ACTIONS, InspectorGroup.MULTI_INSTANCE_LOOP, "loopType", ""),
    INPUT_DATA_ITEM("Input Data Item:", ACTIONS, InspectorGroup.MULTI_INSTANCE_LOOP, "inputDataItem", ""),
    INPUT_DATA("Input Data:", ACTIONS, InspectorGroup.MULTI_INSTANCE_LOOP, "loopDataInputRef", "$DATE$"),
    COMPLETION_CONDITION("Completion Condition:", ACTIONS, InspectorGroup.MULTI_INSTANCE_LOOP, "completionCondition", "${userTask.Status}=${process.pStatus}"),

    //TIMER
    TIMER_PROPERTY("Timer:", EMPTY, InspectorGroup.PROPERTIES, "timer", "PT1M"),

    // Gateways
    CONDITION("Condition:", GATEWAYS, InspectorGroup.PROPERTIES, "condition", "$\\USER$ = $\\USER$"),

    //SUPPORT CODE
    MOD("", EMPTY, InspectorGroup.NON, "", modForSpecific());

    private static final Map<String, ElementProperties> lookup = new HashMap<>();
    public static BasePage page;

    static {
        for (ElementProperties elPr : ElementProperties.values())
            lookup.put(elPr.getName(), elPr);
    }

    private String name;
    private CommonEnumInterface element;
    private InspectorGroup group;
    private String locatorDataAttrib;
    private String defaultValue;
    private String button;
    private String alternativeName;
    private boolean isAngular;
    private String locator;

    ElementProperties(String name, CommonEnumInterface element, InspectorGroup group, String locatorDataAttrib, String defaultValue) {
        this.name = name;
        this.element = element;
        this.group = group;
        this.locatorDataAttrib = locatorDataAttrib;
        this.defaultValue = defaultValue;
    }

    private static String getBttn(String button) {
        return format(bttn, button);
    }

    public static String commonLocator(String locator) {
        return "xpath=(" + locator(locator) + ")[last()]";
    }

    private static String locator(String locator) {
        return "//*[contains(@data-attribute, '" + locator + "')]";
    }

    private static String outputMapPosition(int position) {
        return "xpath=" + OUTPUT_MAP_NAME.group.getLocator() + "//div[@data-index='" + position + "']";
    }

    public static String getRequiredlocator(String locator) {
        return "xpath=//label[contains(@class, 'required')][contains(text(),'" + locator + "')]";
    }

    public static String getFieldNameLocator(String locator) {
        return "//label[contains(text(),'" + locator + "')]/..";
    }

    public static String outputMapLocator(ElementProperties property, int position) {
        return outputMapPosition(position) + locator(property.locatorDataAttrib);
    }

    public static String getOutputMapButton() {
        return "xpath=" + OUTPUT_MAP_NAME.group.getLocator() + getBttn("add");
    }

    public static String getOutputMapRemover(int position) {
        return outputMapPosition(position) + getBttn("del");
    }

    public static Map<Property, String> getElementProperties(ElementOfDesigner type, ProcessDefinitionEditorPage pg) {
        page = pg;
        Map<Property, String> elementProperties = new LinkedHashMap<Property, String>();
        for (ElementProperties p : values()) {
            if (p.isElementPresent(type))
                elementProperties.put(p, p.defaultValue);
        }
        return elementProperties;
    }

    public static ElementProperties get(String elPropertyName) {
        return lookup.get(elPropertyName);
    }

    /**
     * Get instance of enum (namely -concrete ElementProperies) from text
     *
     * @param text - the String of Properties name or data-attribute
     * @return ElementProperties
     */
    public static Property fromString(String text) {
        ElementProperties blockProp = isTheBlock(text);
        if (blockProp != null)
            return new DynamicProperty(blockProp);

        for (ElementProperties prop : values()) {
            if (text.equalsIgnoreCase(prop.getName())) {
                return prop;
            }
            if (text.equalsIgnoreCase(prop.getLocatorDataAttrib())) {
                return prop;
            }
            if (prop.alternativeName != null && text.equals(prop.alternativeName)) {
                return prop;
            }
        }

        return new DynamicProperty(text);
    }

    private static ElementProperties isTheBlock(String text) {
        List<ElementProperties> blocks = new ArrayList<>();
        blocks.add(OUTPUT_MAP_NAME);
        blocks.add(OUTPUT_MAP_SOURCE);
        for (ElementProperties prop : blocks) {
            if ((text.equalsIgnoreCase(prop.name)) || text.equalsIgnoreCase(prop.locatorDataAttrib)) {
                return prop;
            }
        }
        return null;
    }

    private static String modForSpecific() {
        String ADD = "add";
        String SELECTOR = "selector";
        String SELECT = "select";
        OUTPUT_MAP_NAME.button = PARAMETER_NAME.button = ADD;
        RECORD_DEFINITION.button = SELECTOR;
        RUN_AS.button = RECORD_DEFINITION_NAME.button = INPUT_DATA_ITEM.button = SELECT;
        OUTPUT_MAP_NAME.isAngular = CALLED_PROCESS.isAngular = true;
        LOOP_TYPE.button = "select";
        return "initialised";
    }

    private boolean isElementPresent(ElementOfDesigner type) {
        if (element == type || (element instanceof CommonGroupedElements && ((CommonGroupedElements) element).hasElement(type)))
            return true;
        else
            return false;
    }

    /**
     * Generates path from the label of the property
     *
     * @return
     */
    public String getFieldNameLocator() {
        return "xpath=//div[@data-name='" + group.getDataNameValue() + "']" + getFieldNameLocator(name);
    }

    /**
     * Main getLocator!
     *
     * @return universal xpath for editable field.
     */
    public String getLocator() {
        setDefaultLocator();
        if (button != null && button.startsWith("select")) {
            locator = format("xpath=(//*[starts-with(@data-attribute, 'rxData/')][contains(@data-attribute, '%s')])[last()]", locatorDataAttrib);
        }
        if (group == InspectorGroup.INPUT_MAP)
            locator = format(inputFieldByLabel, name);

        if (group == InspectorGroup.NON)
            locator = "xpath=(" + locator(locatorDataAttrib) + ")[last()]";

        if (alternativeName != null && page.isElementPresent("xpath=(" + locator(alternativeName) + ")[last()]")) {
            locator = "xpath=(" + locator(alternativeName) + ")[last()]";
        }

        if (isAngular) {
            page.click(locator + angularSelect2Toggle);
            locator = getLast(locator + angularSelect2Input);
        }
        return locator;
    }

    private void setDefaultLocator() {
        locator = getLast(group.getLocator() + locator(locatorDataAttrib));
    }

    public String getRequiredlocator() {
        return getRequiredlocator(name);
    }

    public String getAddButtonLocator() {
        return "xpath=" + group.getLocator() + getBttn(button);
    }

    public InspectorGroup getGroup() {
        return group;
    }

    public String getDefault() {
        return defaultValue;
    }

    public String getButton() {
        return button;
    }

    public String getName() {
        return name;
    }

    public String getNameWitNoSemicolon() {
        return name.replace(":", "");
    }

    /**
     * @return locatorDataAttrib (a part of xpath, to determine the particular Property on Inspector)
     */
    public String getLocatorDataAttrib() {
        return locatorDataAttrib;
    }

    public boolean isAngular() {
        return isAngular;
    }

    /**
     * Marks property as "angular"
     */
    public void setAngular(boolean isAngular) {
        this.isAngular = isAngular;
    }
}