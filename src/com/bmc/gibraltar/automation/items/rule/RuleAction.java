package com.bmc.gibraltar.automation.items.rule;

import com.bmc.gibraltar.automation.items.element.ElementAction;
import com.bmc.gibraltar.automation.items.tab.Palette;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bmc.gibraltar.automation.items.rule.RuleStencilGroup.*;

/**
 * This enum describes all elements present in the Rule Designer.
 */
public enum RuleAction implements ElementAction {
    TRIGGER("Trigger", null, "RuleTrigger"),
    RULE_QUALIFICATION("RuleQualification", QUALIFICATION, "RuleQualification"),
    CREATE_RECORD_INSTANCE("Create Record Instance", PLATFORM_ACTIONS, "createRecordInstance"),
    UPDATE_RECORD_INSTANCE("Update Record Instance", PLATFORM_ACTIONS, "updateRecordInstance"),
    SET_FIELD("Set Field", PLATFORM_ACTIONS, "RuleSetField"),
    SHOW_MESSAGE("Show Message", PLATFORM_ACTIONS, "RuleShowMessage"),
    START_PROCESS("Start Process", PLATFORM_ACTIONS, "RuleStartProcess"),
    CANCEL_PROCESS("Cancel Process", PLATFORM_ACTIONS, "-tbd-"),
    BUILD_COMPLETION_CONDITION("Build Completion Condition", APPROVALS, "buildCompletionCondition"),
    CREATE_APPROVAL_SUMMARY_INSTANCE("Create Approval Summary Instance", APPROVALS, "createApprovalSummaryInstance"),
    CREATE_CONFIGURATION("Create Configuration", APPROVALS, "createConfiguration"),
    FIND_APPROVAL_PROCESS("Find Approval Process", APPROVALS, "findApprovalProcess"),
    UPDATE_SUMMARY_INSTANCE("Update Summary Instance", APPROVALS, "updateSummaryInstance"),
    CREATE_BASECAMP_PROJECT("Create Basecamp Project", BASECAMP, "createBasecampProject"),
    CREATE_BASECAMP_TO_DO("Create Basecamp To-Do", BASECAMP, "createBasecampTodo"),
    CREATE_BASECAMP_TO_DO_LIST("Create Basecamp To-Do List", BASECAMP, "createBasecampTodoList"),
    DELETE_BASECAMP_PROJECT("Delete Basecamp Project", BASECAMP, "deleteBasecampProject"),
    GET_BASECAMP_PROJECT("Get Basecamp Project", BASECAMP, "getBasecampProjects"),
    GET_BASECAMP_TO_DO_LIST("Get Basecamp To-Do List", BASECAMP, "getBasecampTodoList"),
    GET_BASECAMP_TO_DO("Get Basecamp To-Do", BASECAMP, "getBasecampTodos"),
    EVALUATE_EXPRESSION("Evaluate Expression", EXPRESSIONS, "evaluateExpression"),
    GET_RECORD_INSTANCES("Get Record Instances", RECORDS, "getRecordInstances"),
    UPDATE_TASK_STATUS("Update Task Status", TASKS, "updateTaskInstanceStatus"),
    SEND_MESSAGE("Send Message", USER_MESSAGES, "sendUserMessage"),
    SEND_MESSAGE_BY_TEMPL("Send Message by Templ", USER_MESSAGES, "sendUserMessageByTemplate");
    private String name;
    private RuleStencilGroup paletteGroup;
    private String type;
    private String canvasInDesigner = "xpath=//div[@class='paper']";

    RuleAction(String name, RuleStencilGroup paletteGroup, String type) {
        this.name = name;
        this.paletteGroup = paletteGroup;
        this.type = type;
    }

    public static List<String> getValues() {
        return Arrays.asList(values()).stream().map(RuleAction::getName).collect(Collectors.toList());
    }

    public static RuleAction[] getElementsByGroup(RuleStencilGroup group) {
        List<RuleAction> elements = Arrays.asList(RuleAction.values())
                .stream().filter(el -> el.getPaletteGroup().equals(group)).collect(Collectors.toList());
        RuleAction[] elOfGroup = new RuleAction[elements.size()];
        return elements.toArray(elOfGroup);
    }

    public static RuleAction getElementByType(String type) {
        return Stream.of(values()).filter(element -> type.contains(element.getType())).findFirst().get();
    }

    public static RuleAction getElementByName(String elementName) {
        return Stream.of(values()).filter(element -> element.getName().equals(elementName)).findFirst().get();
    }

    public String getName() {
        return name;
    }

    public RuleStencilGroup getPaletteGroup() {
        return paletteGroup;
    }

    public String getXpathOnPalette() {
        return getXpathOnPalette(Palette.DEFAULT_TAB);
    }

    public String getXpathOnPalette(Palette paletteTab) {
        return paletteTab.getTabPath() + "//*[contains(concat(' ', @class, ' '), '" + type + "')]";
    }

    /**
     * @return element's type as used in HTML (useful for xpath)
     */
    public String getType() {
        return type;
    }

    public String locatorOnCanvas() {
        return canvasInDesigner + String.format("//*[contains(@class,'%s')]", type) + "[last()]";
    }

    public String getNameOnInspector() {
        return name;
    }
}