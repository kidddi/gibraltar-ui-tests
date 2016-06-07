package com.bmc.gibraltar.automation.dataprovider;

import com.bmc.gibraltar.automation.utils.JsonUtils;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.List;

//TODO: create a class that will contain all jsonFileNames as constants, and use it here
public class CommonSteps {
    private static Logger log = Logger.getLogger(CommonSteps.class);
    private RestDataProvider dataProvider;

    public CommonSteps(RestDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Step
    public static List<String> getIdFromProcessVariablesOfStartedInstance(String processInstanceInfoJson) {
        JSONObject fullJson = new JSONObject(processInstanceInfoJson);
        final JSONObject data = new JSONObject(fullJson
                .getJSONObject("processVariables")
                .getString("Process Instance Activity Results")
                .replaceAll("\\\\", ""));
        List<String> result = new ArrayList<>();
        JSONArray activities = fullJson.getJSONArray("activities");
        for (int i = 0; i < activities.length(); i++) {
            JSONObject activity = activities.optJSONObject(i);
            if (activity.isNull("endTime")) {
                JSONObject executionId = data.getJSONObject(activity.getString("executionId"));
                JSONObject activityId = executionId.getJSONObject(activity.getString("activityId"));
                if (activityId.getString("recordDefinitionName").equals("task-manager:Task")) {
                    result.add(activityId.getString("id"));
                }
            }
        }
        return result;
    }

    public String createTask() {
        String taskName = "Task" + Long.toHexString(System.currentTimeMillis());
        log.info("Creating the task with the name: " + taskName);
        dataProvider.createTask("task-manager:Task", "submitter-value", "assigned to Jonnie", "0", "hello", "notes",
                taskName, "2", null);
        String taskIdJsonPath = "data.id";
        ArrayList<String> guids = JsonPath.from(dataProvider.getTaskByTaskName(taskName)).get(taskIdJsonPath);
        String taskGuid = guids.get(0);
        log.info("Task with the GUID: " + taskGuid + " was created.");
        return dataProvider.getTaskInfo(taskGuid);
    }

    /**
     * Will create a disabled process definition with Start -> End
     * Note! To start process instances, all values for the process inputs need to be specified
     * (this process definition contains REQUIRED text input parameter with the name: "Text Input"
     *
     * @param processDefName name that will have new process definition
     * @return json of the process definition
     */
    public String createDisabledProcessDefinition(String processDefName) {
        log.info("Creating the disabled process definition with the name: " + processDefName);
        String pdJson = JsonUtils.getJsonFromFile("processDefinitionWithPermissionEntry.json");
        pdJson = JsonUtils.updateJsonProperty(pdJson, "$.isEnabled", false).json().toString();
        DocumentContext jsonContext = JsonUtils.updateJsonProperty(pdJson, "$.permissions", null);
        return createCopyOfProcessDefinition(jsonContext, processDefName);
    }

    /**
     * Will create a process definition with ONE permission entry with the specified type
     * Note! To start process instances, all values for the process inputs need to be specified
     * (this process definition contains REQUIRED text input parameter with the name: "Text Input"
     *
     * @param processDefinitionName name that will have new process definition
     * @param permissionType        should be "READ" or "EXECUTE"
     * @return json of the process definition
     */
    public String createProcessDefinitionWithPermissionEntry(String processDefinitionName, String permissionType) {
        log.info("Creating the process definition with the name: " + processDefinitionName);
        String pdJson = JsonUtils.getJsonFromFile("processDefinitionWithPermissionEntry.json");
        DocumentContext jsonContext = JsonUtils.updateJsonProperty(pdJson,
                "$.permissions..permissionType", permissionType.toUpperCase());
        return createCopyOfProcessDefinition(jsonContext, processDefinitionName);
    }

    /**
     * Will create a process definition with no permission entries / inputs parameters
     *
     * @param processDefinitionName name that will have new process definition
     * @return json of the process definition
     */
    public String createProcessDefinition(String processDefinitionName) {
        log.info("Creating the process definition with the name: " + processDefinitionName);
        String pdJson = JsonUtils.getJsonFromFile("processDefinitionWithPermissionEntry.json");
        pdJson = JsonUtils.updateJsonProperty(pdJson, "$.inputParams", null).json().toString();
        DocumentContext jsonContext = JsonUtils.updateJsonProperty(pdJson, "$.permissions", null);
        return createCopyOfProcessDefinition(jsonContext, processDefinitionName);
    }

    public String createProcessDefinitionWithCreateTaskElement(String nameOfProcessDefinitionCopy) {
        return createCustomProcessDefinition(nameOfProcessDefinitionCopy,
                "processDefinitionWithCreateTaskElement.json");
    }

    public String createCustomProcessDefinition(String nameOfProcessDefinitionCopy, String fileName) {
        log.info("Creating the process definition with the name: " + nameOfProcessDefinitionCopy);
        String pdJson = JsonUtils.getJsonFromFile(fileName);
        DocumentContext jsonContext = JsonUtils.getDocumentContext(pdJson);
        return createCopyOfProcessDefinition(jsonContext, nameOfProcessDefinitionCopy);
    }

    /**
     * Will create a process definition copy
     *
     * @param jsonContext                 jsonContext of the process definition for which the copy will be created
     * @param nameOfProcessDefinitionCopy name that the copy will have
     * @return json of the process definition copy
     */
    public String createCopyOfProcessDefinition(DocumentContext jsonContext, String nameOfProcessDefinitionCopy) {
        String jsonOfProcessDefinitionCopy = JsonUtils
                .createCopyOfProcessDefinitionJson(jsonContext, nameOfProcessDefinitionCopy);
        dataProvider.createProcessDefinition(jsonOfProcessDefinitionCopy);
        return jsonOfProcessDefinitionCopy;
    }

    /**
     * This method will create a record definition that contains only core fields.
     *
     * @param recordDefinitionName record definition name
     * @return updated json for the request
     */
    public String createRecordDefinitionWithCoreFieldsOnly(String recordDefinitionName) {
        return createRecordDefinitionByTemplate(recordDefinitionName, "recordDefinitionWithCoreFieldsOnly.json");
    }

    /**
     * This method will create a record definition using the json template from {@param fileNameWithTemplateJson}.
     * The ID of the record definition should not be updated,
     * because the  {@param recordDefinitionName} is also the ID of the record definition.
     * The record definition will be created in the task-manager bundle
     *
     * @param recordDefinitionName     record definition name
     * @param fileNameWithTemplateJson file that contains a record definition json
     * @return updated json for the request
     */
    public String createRecordDefinitionByTemplate(String recordDefinitionName, String fileNameWithTemplateJson) {
        log.info("Creating the record definition with the name: " + recordDefinitionName);
        String jsonFromFile = JsonUtils.getJsonFromFile(fileNameWithTemplateJson);
        String recordDefinitionJson = JsonUtils.updateJsonProperty(jsonFromFile,
                "$.name", "task-manager:" + recordDefinitionName).json().toString();
        dataProvider.createRecordDefinition(recordDefinitionJson);
        return recordDefinitionJson;
    }
}
