package com.bmc.gibraltar.automation.items.element;

import com.bmc.gibraltar.automation.items.CommonEnumInterface;
import com.bmc.gibraltar.automation.items.CommonHandlers;
import com.bmc.gibraltar.automation.items.tab.Palette;
import com.bmc.gibraltar.automation.pages.BasePage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.*;
import java.util.stream.Collectors;

import static com.bmc.gibraltar.automation.items.element.PaletteGroup.*;

public enum ElementOfDesigner/*//TODO: rename to ProcessElement*/ implements CommonEnumInterface, ElementAction {
    START("Start", EVENT, "StartEvent"),
    END("End", EVENT, "EndEvent"),
    TIMER("Timer", EVENT, "TimerEvent"),
    ASSOCIATE("Associate", ASSOCIATION, "associate"),
    USER_TASK("User Task", ACTIVITY, "UserTask"),
    SUB_PROCESS("Sub Process", ACTIVITY, "SubProcess"),
    RECEIVE_TASK("Receive Task", ACTIVITY, "ReceiveTask"),
    CALL_ACTIVITY("Call Activity", ACTIVITY, "CallActivity"),
    BUILD_COMPLETION_CONDITION("Build Completion Condition", APPROVAL, "buildCompletionCondition"),
    CREATE_CONFIGURATION("Create Configuration", APPROVAL, "createConfiguration"),
    APPROVAL_PROCESS("Approval Process", APPROVAL, "CallActivity"),
    WAIT_FOR_APPROVAL("Wait For Approval", APPROVAL, "CallActivity"),
    CREATE_APPROVAL_SUMMARY_INSTANCE("Create Approval Summary Instance", APPROVAL, "createApprovalSummaryInstance"),
    FIND_APPROVAL_PROCESS("Find Approval Process", APPROVAL, "findApprovalProcess"),
    UPDATE_SUMMARY_INSTANCE("Update Summary Instance", APPROVAL, "updateSummaryInstance"),
    CREATE_BASECAMP_PROJECT("Create Basecamp Project", BASECAMP, "createBasecampProject"),
    CREATE_BASECAMP_TO_DO("Create Basecamp To-Do", BASECAMP, "createBasecampTodo"),
    CREATE_BASECAMP_TO_DO_LIST("Create Basecamp To-Do List", BASECAMP, "createBasecampTodoList"),
    DELETE_BASECAMP_PROJECT("Delete Basecamp Project", BASECAMP, "deleteBasecampProject"),
    GET_BASECAMP_PROJECT("Get Basecamp Project", BASECAMP, "getBasecampProjects"),
    GET_BASECAMP_TO_DO_LIST("Get Basecamp To-Do List", BASECAMP, "getBasecampTodoList"),
    GET_BASECAMP_TO_DO("Get Basecamp To-Do", BASECAMP, "getBasecampTodos"),
    CALCULATE_SLA_TIME("Calculate SLATime", TASKS, "calculateSLATime"),
    CREATE_TASK("Create Task", TASKS, "createTaskInstance"),
    CREATE_TASK_CUSTOM("Create Task Custom", TASKS, "createTaskInstanceWithCustomObject"),
    IDENTIFY_TASK_ASSIGNEE("Identify Task Assignee", TASKS, "identifyTaskAssignee"),
    UPDATE_TASK_STATUS("Update Task Status", TASKS, "updateTaskInstanceStatus"),
    SEND_MESSAGE("Send Message", USER_MESSAGES, "sendUserMessage"),
    SEND_MESSAGE_BY_TEMPL("Send Message by Templ", USER_MESSAGES, "sendUserMessageByTemplate"),
    PARALLEL_GATEWAY("Parallel", GATEWAY, "ParallelGateway"),
    EXCLUSIVE_GATEWAY("Exclusive", GATEWAY, "ExclusiveGateway"),
    ANNOTATION("Annotation", ANNOTATIONS, "TextAnnotation"),
    EVALUATE_EXPRESSION("Evaluate Expression", EXPRESSIONS, "evaluateExpression"),
    CREATE_RECORD_INSTANCE("Create Record Instance", RECORDS, "createRecordInstance"),
    GET_RECORD_INSTANCES("Get Record Instances", RECORDS, "getRecordInstances"),
    UPDATE_RECORD_INSTANCE("Update Record Instance", RECORDS, "updateRecordInstance");
    private static String processActionLocator = "xpath=//div[@class='rx-blade-tab-pane ng-scope active']" +
            "//div[@class='elements']//*[contains(@class, 'ProcessAction')]";
    private String name;
    private PaletteGroup paletteGroup;
    private String type;

    ElementOfDesigner(String name,
                      PaletteGroup paletteGroup,
                      String type) {
        this.name = name;
        this.paletteGroup = paletteGroup;
        this.type = type;
    }

    public static ElementOfDesigner getElementOfDesignerByValue(String value) {
        for (ElementOfDesigner elem : ElementOfDesigner.values()) {
            if (value.equals(elem.getType()))
                return elem;
            if (value.equals(elem.getName()))
                return elem;
        }
        return null;
    }

    /**
     * Returns compounded range elements, example the range like this: [1, 3, 4, 6, 8, 10]
     * returns element array [1, 2, 3, 4, 5, 6, 8, 10] from enum.
     */
    // TODO: check if this method can be removed
    @Step
    public static ElementOfDesigner[] getRange(Integer... range) {
        List<ElementOfDesigner> list = new ArrayList<>();
        for (int i = 0; i < range.length; i++) {
            list.addAll(Arrays.asList(values()).subList(range[i], range[++i]));
        }
        return (ElementOfDesigner[]) list.toArray();
    }

    /**
     * Returns the range [1,  @count] elements from enum.
     */
    public static ElementOfDesigner[] getFirstFew(int count) {
        return Arrays.copyOfRange(values(), 0, count);
    }

    /**
     * Returns elements list, by its value or name.
     * Example: getElementsByValues("Exclusive", "SubProcess", "Annotation")
     * returns { EXCLUSIVE_GATEWAY, SUB_PROCESS, ANNOTATION } list.
     */
    public static List<ElementOfDesigner> getElementsByValues(String... values) {
        return getElementsByValues(Arrays.asList(values));
    }

    /**
     * Returns elements list, by List of its values/names.
     * Example: getElementsByValues("Exclusive", "SubProcess", "Annotation") returns {EXCLUSIVE_GATEWAY, SUB_PROCESS, ANNOTATION}List.
     */
    public static List<ElementOfDesigner> getElementsByValues(List<String> values) {
        return values.stream()
                .map(ElementOfDesigner::getElementOfDesignerByValue)
                .filter(value -> value != null)
                .collect(Collectors.toList());
    }

    public static List<String> getValues() {
        return Arrays.asList(values()).stream().map(ElementOfDesigner::getName).collect(Collectors.toList());
    }

    public static ElementOfDesigner[] getElementsByGroup(PaletteGroup group) {
        List<ElementOfDesigner> elements = Arrays.asList(ElementOfDesigner.values())
                .stream().filter(el -> el.getPaletteGroup().equals(group)).collect(Collectors.toList());
        ElementOfDesigner[] elOfGroup = new ElementOfDesigner[elements.size()];
        return elements.toArray(elOfGroup);
    }

    /**
     * Collects all ProcessActions (Activities in US) elements from current (is opened now) palette.
     *
     * @param page (any page, just for getList of WebElements
     * @return all ProcessActions (Activities in US) elements from current (is opened now) palette
     */
    public static List<ElementOfDesigner> getAllProcessActions(BasePage page) {
        List<String> attrValues = page.getListOfWebElementsAttribute(processActionLocator, "class");
        return attrValues.stream()
                .map(element -> getElementOfDesignerByValue(CommonHandlers.getRegex(element, "\\W([a-zA-Z]{5,})$", 1)))
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    /**
     * @return the value, element has on Inspector under property 'Name' by default
     */
    public String getNameOnInspector() {
        return name;
    }

    /**
     * @param editPage - instance of ProcessDefinitionEditorPage
     * @return map of element's parameters, where key is parameter's name and value is its Group on Inspector
     */
    @Deprecated
    public Map<String, String> getParametersInGroups(ProcessDefinitionEditorPage editPage) {
        return new HashMap<>();
    }

    public PaletteGroup getPaletteGroup() {
        return paletteGroup;
    }

    public String getXpathOnPalette() {
        return getXpathOnPalette(Palette.DEFAULT_TAB);
    }

    public String getXpathOnPalette(Palette paletteTab) {
        if (getName().equals("Annotation")) {
            return paletteTab.getTabPath() + "//*[contains(concat(' ', @class, ' '), '" + type + "')]";
        }
        return paletteTab.getTabPath() + "//*[text()='" + name + "']";
    }

    /**
     * @return element's type as used in HTML (useful for xpath)
     */
    public String getType() {
        return type;
    }

    /**
     * @return String, value of xpath on Canvas for particular element,
     * the last added (if many of the same type elements present on Canvas);
     */
    public String locatorOnCanvas() {
        String locator = String.format("%s//*[contains(@class,'%s')]",
                ProcessDefinitionEditorPage.canvasFreeSpace,
                type);
        if (type.equals("ProcessAction")) // as similar types on Canvas cann't be found by orderOfSimilarTypesOnPalette
            locator = locator + "[contains(.,'" + name + "')]";
        return locator + "[last()]";
    }

    /**
     * Verify, is element type property exists in parameters range.
     */
    public boolean equals(ElementOfDesigner... elementTypes) {
        for (ElementOfDesigner elementType : elementTypes) {
            if (this == elementType) {
                return true;
            }
        }
        return false;
    }
}