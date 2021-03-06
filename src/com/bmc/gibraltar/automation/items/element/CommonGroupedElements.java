package com.bmc.gibraltar.automation.items.element;

import com.bmc.gibraltar.automation.items.CommonEnumInterface;

import java.util.Arrays;
import java.util.List;

import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.*;

public enum CommonGroupedElements implements CommonEnumInterface {
/*Grouped ElementsOfDesigner, that have common properties */

    ALL(ElementOfDesigner.values()),

    HAVE_OUTPUT_MAP(BUILD_COMPLETION_CONDITION, CREATE_BASECAMP_PROJECT, CREATE_BASECAMP_TO_DO, CREATE_BASECAMP_TO_DO_LIST, GET_BASECAMP_PROJECT, GET_BASECAMP_TO_DO_LIST, GET_BASECAMP_TO_DO,
            CREATE_TASK, CALL_ACTIVITY, USER_TASK),

    BASECAMP(CREATE_BASECAMP_PROJECT, CREATE_BASECAMP_TO_DO, CREATE_BASECAMP_TO_DO_LIST, DELETE_BASECAMP_PROJECT, GET_BASECAMP_PROJECT, GET_BASECAMP_TO_DO_LIST, GET_BASECAMP_TO_DO),

    HAVE_PROJECT_ID(CREATE_BASECAMP_TO_DO, CREATE_BASECAMP_TO_DO_LIST, DELETE_BASECAMP_PROJECT, GET_BASECAMP_TO_DO_LIST, GET_BASECAMP_TO_DO),

    HAVE_DESCRIPTION(CREATE_BASECAMP_PROJECT, CREATE_BASECAMP_TO_DO_LIST),

    HAVE_TODO_LIST_ID(CREATE_BASECAMP_TO_DO, GET_BASECAMP_TO_DO),

    TASK(CREATE_TASK, USER_TASK, CREATE_TASK_CUSTOM),

    TASKS(CREATE_TASK, USER_TASK, CREATE_TASK_CUSTOM, UPDATE_TASK_STATUS),

    RECORDS(UPDATE_RECORD_INSTANCE, CREATE_RECORD_INSTANCE),

    RECORDS_AND_MESSAGE(UPDATE_RECORD_INSTANCE, CREATE_RECORD_INSTANCE, SEND_MESSAGE_BY_TEMPL),

    MESSAGE(SEND_MESSAGE, SEND_MESSAGE_BY_TEMPL),

    BUILD_COMPLETION_AND_USER_TASK(BUILD_COMPLETION_CONDITION, USER_TASK),

    BUILD_COMPLETION_CONDITION_AND_APPROVALS(BUILD_COMPLETION_CONDITION, APPROVAL_PROCESS),

    RECORD_INSTANCE_AND_MESSAGE_AND_USER_TASK(UPDATE_RECORD_INSTANCE, SEND_MESSAGE_BY_TEMPL, USER_TASK),

    ACTIONS(CREATE_BASECAMP_PROJECT, CREATE_BASECAMP_TO_DO, CREATE_BASECAMP_TO_DO_LIST, DELETE_BASECAMP_PROJECT, GET_BASECAMP_PROJECT, GET_BASECAMP_TO_DO_LIST, GET_BASECAMP_TO_DO,
            EVALUATE_EXPRESSION, CREATE_RECORD_INSTANCE, UPDATE_RECORD_INSTANCE, CREATE_TASK, CREATE_TASK_CUSTOM, UPDATE_TASK_STATUS, SEND_MESSAGE, SEND_MESSAGE_BY_TEMPL, BUILD_COMPLETION_CONDITION),

    GATEWAYS(EXCLUSIVE_GATEWAY),

    EMPTY();

    private ElementOfDesigner[] elements;

    CommonGroupedElements(ElementOfDesigner... elements) {
        this.elements = elements;
    }

    public boolean hasElement(ElementOfDesigner element) {
        List<ElementOfDesigner> list = Arrays.asList(elements);
        return list.contains(element);
    }

    public String getName() {
        return "";
    }
}