package com.bmc.gibraltar.automation.dataprovider;

import com.bmc.gibraltar.automation.api.*;
import com.bmc.gibraltar.automation.framework.utils.PropertiesUtils;
import com.bmc.gibraltar.automation.items.component.Component;
import com.bmc.gibraltar.automation.items.element.Property;
import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.record.RecordField;
import com.bmc.gibraltar.automation.utils.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import ru.yandex.qatools.allure.annotations.Step;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class RestDataProvider {
    protected static final Logger LOG = Logger.getLogger(RestDataProvider.class);
    private static String appServerUrl = PropertiesUtils.getAppServerUrl();
    private AssociationApi associationApi;
    private OrganizationApi organizationApi;
    private ProcessApi processApi;
    private RecordApi recordApi;
    private UserManagementApi userManagementApi;
    private ViewApi viewApi;
    private RuleApi ruleApi;

    public RestDataProvider() {
        RestAssured.baseURI = appServerUrl;
        userManagementApi = new UserManagementApi();
        initializeRestData(userManagementApi.getRequestSpecification());
    }

    public RestDataProvider(String loginName, String userPassword) {
        RestAssured.baseURI = appServerUrl;
        userManagementApi = new UserManagementApi(loginName, userPassword);
        initializeRestData(userManagementApi.getRequestSpecification());
    }

    private static JsonObject generateJsonForUserCreation(String fullName, String loginName, String password
            , String emailAddress, String[] permissionGroups) {
        JsonObject request = new JsonObject();
        request.addProperty("fullName", fullName);
        request.addProperty("loginName", loginName);
        request.addProperty("password", password);
        request.addProperty("emailAddress", emailAddress);
        request.add("groups", new Gson().toJsonTree(permissionGroups));
        return request;
    }

    private void initializeRestData(RequestSpecification requestSpecification) {
        associationApi = new AssociationApi(requestSpecification);
        organizationApi = new OrganizationApi(requestSpecification);
        processApi = new ProcessApi(requestSpecification);
        recordApi = new RecordApi(requestSpecification);
        viewApi = new ViewApi(requestSpecification);
        ruleApi = new RuleApi(requestSpecification);
    }

    private List<String> getBundleScopeHeaders() {
        return processApi.getBundleScopeHeaders();
    }

    private String generateAbsoluteDefinitionNameBasedOnHeader(String header, String recordDefinitionName) {
        return processApi.generateAbsoluteDefinitionNameBasedOnHeader(header, recordDefinitionName);
    }

    public String getCurrentUserLoginName() {
        return getUserProfilePart("$USER$", "loginName");
    }

    private String getUserProfilePart(String user, String infoPart) {
        return JsonPath.from(userManagementApi.getUserInfo(user).and().extract().asString()).get(infoPart);
    }

    /**
     * This method allows to get user groups or computedGroups
     *
     * @param userLoginName $USER$ if current logged user, or 'username'
     * @param groups        should equal to 'groups' or 'computedGroups'
     * @return list of the user's groups
     */
    public List<String> getUserGroups(String userLoginName, String groups) {
        return JsonPath.from(userManagementApi.getUserInfo(userLoginName).extract().asString()).get(groups);
    }

    public boolean isUserExistent(String userLoginName) {
        int statusCode = userManagementApi.getUserInfo(userLoginName).extract().statusCode();
        return statusCode == HttpURLConnection.HTTP_OK;
    }

    /**
     * Will create a user with the specified info.
     *
     * @param fullName         the user's full name
     * @param loginName        the user's loginName (<loginname>@<tenant domain identifier>)
     *                         the user will be created in the same tenant group as the admin who creates him,
     *                         so  <tenant domain identifier> is set automatically
     * @param password         the user's password
     * @param emailAddress     the user's email address
     * @param permissionGroups groups to which the user belongs to.
     *                         To get all available permitted groups for currently logged user:
     *                         GET /api/rx/application/datapage?dataPageType=com.bmc.arsys.rx.application.group
     *                         .datapage.GroupDataPageQuery&pageSize=-1&startIndex=0
     */
    public void createUser(String fullName, String loginName, String password, String emailAddress
            , String[] permissionGroups) {
        JsonObject request = generateJsonForUserCreation(fullName, loginName, password, emailAddress, permissionGroups);
        String jsonString = new GsonBuilder().create().toJson(request);
        if (!isUserExistent(loginName)) {
            userManagementApi.createUserRequest(jsonString);
        }
    }

    /**
     * Allows to create a user for organizations.
     *
     * @param name             example "CokeHRUser".
     * @param license          there are 4 types("Read", "Restricted", "Fixed", "Floating") .
     * @param domain           there are 2 types("pepsi", "coke").
     * @param permissionGroups example(..., "Administrator", "CokeHR" ), where "CokeHR" - organization name.
     */
    public String createUserByLicense(String name, String password, String domain, String license, String... permissionGroups) {
        String loginName = format("%s@%s.com", name, domain);
        JsonObject request = generateJsonForUserCreation(name, name, password, loginName, permissionGroups);
        request.addProperty("status", "Current");
        request.addProperty("password", password);
        request.addProperty("licenseType", license);
        String jsonString = new GsonBuilder().create().toJson(request);
        userManagementApi.createUserRequest(jsonString);
        return loginName;
    }

    // TODO: Rewrite for all record definitions.
    /**
     * The method creates an association with name []To[] example createAssociation("A", "B") creates AToB association.
     *
     * @param fromRecordDefinitionName a name of special Record Definition, with hardcoded id. Was send by server side.
     *                                 AssociationRecordDefinition.json - template of mentioned record.
     * @param toRecordDefinitionName   the same as fromRecordDefinitionName.
     */
    public void createAssociation(String fromRecordDefinitionName, String toRecordDefinitionName) {
        String json = JsonUtils.getJsonFromFile("AssociationTemplate.json").replaceAll("APoint", fromRecordDefinitionName)
                .replaceAll("BPoint", toRecordDefinitionName);
        associationApi.createAssociationDefinition(json);
    }

    public String createOrganization(String json) {
        return organizationApi.createOrganization(json).toString();
    }

    private String createJsonForDefinition(String definitionName, String jsonTemplateFileName) {
        String json = JsonUtils.getJsonFromFile(jsonTemplateFileName);
        return json.replace("UniqueNameShouldBeChanged", definitionName)
                .replaceAll("IDShouldBeGenerated", RandomStringUtils.randomAlphanumeric(16));
    }

    /**
     * Creates a new rule, based on json template. Template includes trigger and is based on Task record.
     * Before creation, it should post createProcessAndRecordForRulesCreation() only once for a build!
     */
    public void createRuleByTemplate(String ruleName) {
        ruleApi.createRuleDefinition(createJsonForDefinition(ruleName, "RuleTemplate.json"));
    }

    public String createProcessDefinition(String requestBody) {
        return processApi.createProcessDefinition(requestBody).extract().asString();
    }

    public void createProcessDefinition(String definitionName, String jsonTemplateFileName) {
        processApi.createProcessDefinition(createJsonForDefinition(definitionName, jsonTemplateFileName));
    }

    public void createRecordDefinition(String definitionName, String jsonTemplateFileName) {
        recordApi.createRecordDefinition(createJsonForDefinition(definitionName, jsonTemplateFileName));
    }

    public void createRecordDefinition(String requestBody) {
        recordApi.createRecordDefinition(requestBody);
    }

    public boolean isUserInComputedGroup(String user, String computedGroup) {
        List<String> group = getUserGroups(user, "computedGroups");
        return group != null && group.contains(computedGroup);
    }

    public boolean isUserInGroup(String user, String group) {
        List<String> groups = getUserGroups(user, "groups");
        return groups != null && groups.contains(group);
    }

    public String getProcessDefinitionID(String processDefinitionName) {
        return JsonPath.from(getProcessDefinitionInfo(processDefinitionName)).get("id");
    }

    public String getProcessDefinitionInfo(String processDefinitionName) {
        try {
            return processApi.getProcessDefinitionJson(processDefinitionName).extract().asString();
        } catch (AssertionError e) {
            String definitionName = getBundleScopeHeaders().stream()
                    .map(header -> generateAbsoluteDefinitionNameBasedOnHeader(header, processDefinitionName))
                    .filter(absoluteDefinitionName ->
                            processApi.getProcessDefinition(absoluteDefinitionName).extract().statusCode() == HttpURLConnection.HTTP_OK)
                    .findFirst().get();
            return processApi.getProcessDefinitionJson(definitionName).extract().asString();
        }
    }

    public List<String> getProcessDefinitionsNamesByState(boolean isEnabled) {
        String processDefinitionsInfo = processApi.getAllProcessDefinitionsInfo().extract().asString();
        List<String> processDefinitionNames = JsonPath.from(processDefinitionsInfo)
                .getList(" data.findAll { it.isEnabled == '" + isEnabled + "' }.name");
        return processDefinitionsWithoutBundle(processDefinitionNames);
    }

    /**
     * @return list of all created process definitions
     */
    public List<String> getProcessDefinitionsNames(boolean isAbsoluteName) {
        List<String> processDefinitionNames = processApi.getAllProcessDefinitionsInfo().extract().path("data.name");
        if (isAbsoluteName) {
            return processDefinitionNames;
        } else {
            return processDefinitionsWithoutBundle(processDefinitionNames);
        }
    }

    private List<String> processDefinitionsWithoutBundle(List<String> processDefinitionNames) {
        return processDefinitionNames.stream()
                .map(pd -> pd.replaceAll(RestApiData.TASK_MANAGER_HEADER + ":", ""))
                .map(pd -> pd.replaceAll(RestApiData.APPROVAL_HEADER + ":", ""))
                .map(pd -> pd.replaceAll(RestApiData.FOUNDATION_HEADER + ":", "")).collect(Collectors.toList());
    }

    /**
     * Start a process instance for the process definition with {@param processDefinitionId} and returns the response body
     *
     * @param processDefinitionId process definition ID for which a process instance will be started
     * @return response body
     */
    @Step
    public String startProcess(String processDefinitionId) {
        String bodyOfRequest = new GsonBuilder().create().toJson(buildRequestToStartProcessInstance(processDefinitionId));
        return processApi.startProcessInstance(bodyOfRequest).extract().toString();
    }

    private JsonObject buildRequestToStartProcessInstance(String processDefinitionId) {
        JsonObject request = new JsonObject();
        request.addProperty("processDefinitionId", processDefinitionId);
        request.addProperty("resourceType", "com.bmc.arsys.rx.application.process.command.StartProcessInstanceCommand");
        return request;
    }

    public String startProcessWithInputs(String processDefinitionId, HashMap<String, String> inputs) {
        JsonObject request = buildRequestToStartProcessInstance(processDefinitionId);
        JsonObject inputsOfProcessDefinition = new JsonObject();
        for (Map.Entry<String, String> entry : inputs.entrySet()) {
            inputsOfProcessDefinition.addProperty(entry.getKey(), entry.getValue());
        }
        request.add("processInputValues", inputsOfProcessDefinition);
        String bodyOfRequest = new GsonBuilder().create().toJson(request);
        return processApi.startProcessInstance(bodyOfRequest).extract().toString();
    }

    /**
     * Returns JSon of the specified process definition with the {@param processDefinitionName}
     *
     * @param processDefinitionName process definition name
     * @return json of the process definition with the {@param processDefinitionName}
     */
    public String getJsonOfProcess(String processDefinitionName) {
        return processApi.getProcessDefinitionJson(processDefinitionName).extract().asString();
    }

    /**
     * Sends request to get all Group Data and extracts all existed Permissions.
     *
     * @return List of all existed Permissions from server side.
     */
    @Step("user extracted all existed Permissions")
    public List<String> getGroupNames() {
        return JsonPath.from(userManagementApi.getAllPermittedGroups().toString()).get("groupName");
    }

    /**
     * In the Process Designer should be present groups which 'groupId > 9'
     *
     * @return list of permitted groups visible for the currently logged user
     */
    public List<String> getPermittedGroups() {
        return JsonPath.from(userManagementApi.getAllPermittedGroups().toString())
                .getList(" data.findAll { it.groupId > 9 }.groupName");
    }

    /**
     * This method collects process inputs of the specified process definition
     *
     * @param processDefinitionName process definition name
     * @return process inputs of the specific process definition with the {@param processDefinitionName}
     */
    public String getInputsOfProcessDefinition(String bundle, String processDefinitionName) {
        return processApi.getInputsOfProcessDefinition(bundle + processDefinitionName).extract().asString();
    }

    public List<String> getRequiredInputsOfProcessDefinition(String bundle, String decodedProcessDefinitionName) {
        return JsonPath.from(getInputsOfProcessDefinition(bundle, decodedProcessDefinitionName))
                .<String>getList(" findAll { it.fieldOption == 'REQUIRED' " +
                        "&& (it.defaultValue == null || it.defaultValue == '')}.name");
    }

    public List<String> getCustomFieldDefinitionTypes() {
        return processApi.getCustomFieldDefinition().extract().path("data.fieldDefinitionType");
    }

    /**
     * Returns all Record Definitions names, visible for current user
     */
    public List<String> getRecordDefinitionNames() {
        List<String> parsedRecords = recordApi.getAbsoluteRecordDefinitionNames().and().extract().body().path("data");
        parsedRecords.stream().map(line -> line
                .replaceAll(RestApiData.APPROVAL_HEADER + ":", "")
                .replaceAll(RestApiData.TASK_MANAGER_HEADER + ":", "")
                .replaceAll(RestApiData.USER_MESSAGE_HEADER + ":", "")
                .replaceAll(RestApiData.FOUNDATION_HEADER + ":", "")
                .replaceAll(RestApiData.RXN_HEADER, "/")).collect(Collectors.toList());
        LOG.info("\n All Records: \n" + parsedRecords);
        return parsedRecords;
    }

    /**
     * Returns list of  Record Field names for a particular Record Definition name
     *
     * @param record record definition name
     */
    public List<String> getRecordFields(String record) {
        String response = getRecFields(record);
        List<String> output = JsonPath.from(response).getList("fieldDefinitions.name");
        LOG.info("\n Record Definition: '" + record
                + "'  has such [ALL] RecordFields: " + output.toString());
        return output;
    }

    private List<String> getListOfFields(String record, String propertyName, String expectedPropertyValue) {
        String response = getRecFields(record);
        List<String> output = JsonPath.from(response)
                .getList("fieldDefinitions.findAll { it." + propertyName + " == '" + expectedPropertyValue + "'}.name");
        LOG.info("\n Record Definition '" + record
                + "'  has such fields :" + output.toString() + " that have " + propertyName
                + " with '" + expectedPropertyValue + "' value.");
        return output;
    }

    public List<String> getListOfFieldsWithoutDefaultValue(RecordField.Option option, String recordDefinitionName) {
        String response = getRecFields(recordDefinitionName);
        return JsonPath.from(response).getList("fieldDefinitions.findAll { it.fieldOption == '" + option
                + "' && (it.defaultValue == null || it.defaultValue == '')}.name");
    }

    public List<String> getListOfFieldsByType(String recordDefinitionName, DataType type) {
        String response = getRecFields(recordDefinitionName);
        return JsonPath.from(response).getList("fieldDefinitions.findAll { it.resourceType == '"
                + type.getResourceType() + "'}.name");
    }

    /**
     * Returns list of  Record Field names of a particular fieldOption (SYSTEM/REQUIRED/OPTIONAL)
     * and for a particular Record Definition name
     *
     * @param option should be be SYSTEM, REQUIRED or OPTIONAL
     * @param record record definition name
     * @return list of the fields
     */
    public List<String> getRecordFieldsNamesByFieldOption(RecordField.Option option, String record) {
        return getListOfFields(record, "fieldOption", option.name());
    }

    public List<String> getRecordFieldsNamesByProperty(String record, String propertyName, String expectedPropertyValue) {
        return getListOfFields(record, propertyName, expectedPropertyValue);
    }

    /**
     * Collect all selection fields and their options from the specified {@param record} record definition name
     *
     * @param record record definition name
     * @return HashMap where 'String' is a name of the selection field, and 'List<String>' - its options
     */
    public Map<String, List<String>> getSelectionFieldsAndOptions(String record) {
        JsonPath response = JsonPath.from(getRecFields(record));
        String optionsPath = "fieldDefinitions.find { it.name == '%s'}.options";
        List<String> selectionFields = getRecordFieldsNamesByProperty(record, "resourceType", DataType.SELECTION.getResourceType());
        return selectionFields.stream().collect(
                Collectors.toMap(
                        selectionField -> selectionField,
                        selectionField -> response.getList(String.format(optionsPath, selectionField))));
    }

    private String getRecFields(String recordDefinitionName) {
        try {
            return recordApi.getRecordDefinitionJson(recordDefinitionName).extract().asString();
        } catch (AssertionError e) {
            String definitionName = getBundleScopeHeaders().stream()
                    .map(header -> generateAbsoluteDefinitionNameBasedOnHeader(header, recordDefinitionName))
                    .filter(absoluteDefinitionName ->
                            recordApi.getRecordDefinition(absoluteDefinitionName).extract().statusCode() == HttpURLConnection.HTTP_OK)
                    .findFirst().get();
            return recordApi.getRecordDefinitionJson(definitionName).extract().asString();
        }
    }

    @Step
    public String getTaskInfo(String taskGuid) {
        return recordApi.getRecordInstance(RestApiData.TASK_RECORD_DEFINITION, taskGuid).extract().asString();
    }

    @Step
    public String getTasks() {
        return recordApi.getRecordInstances(RestApiData.TASK_RECORD_DEFINITION).extract().asString();
    }

    @Step
    public int getTasksCount() {
        return JsonPath.from(getTasks()).get("totalSize");
    }

    @Step
    public String getTaskByTaskName(String taskName) {
        return recordApi.getRecordInstancesByFieldValue(RestApiData.TASK_RECORD_DEFINITION, "10007000", taskName)
                .extract().asString();
    }

    @Step
    public List<String> getInstanceIds(String recordDefinitionName) {
        return JsonPath.from(recordApi.getRecordInstances(recordDefinitionName).extract().asString()).get("data.id");
    }

    @Step
    public void createTask(String recordDefinitionName, String submitter, String assignee
            , String statusCode, String summary, String notes, String taskName, String priorityCode
            , String dueDate) {
        JsonObject bodyRequest = new JsonObject();
        bodyRequest.addProperty("resourceType", "com.bmc.arsys.rx.services.record.domain.RecordInstance");
        bodyRequest.addProperty("recordDefinitionName", recordDefinitionName);
        // fields to create a record instance
        JsonObject recordProperty = new JsonObject();
        addRecordFieldToRequest(recordProperty, "2", submitter); // submitter of the record instance
        addRecordFieldToRequest(recordProperty, "4", assignee); // assignee
        addRecordFieldToRequest(recordProperty, "7", statusCode); // status: 0 ( staged )
        addRecordFieldToRequest(recordProperty, "8", summary); // summary
        addRecordFieldToRequest(recordProperty, "10000101", notes); // notes
        addRecordFieldToRequest(recordProperty, "10007000", taskName); // task-name
        addRecordFieldToRequest(recordProperty, "10007122", priorityCode); // priority: 2 ( medium )
        addRecordFieldToRequest(recordProperty, "536870913", dueDate); // due date
        bodyRequest.add("fieldInstances", recordProperty);
        String request = new GsonBuilder().create().toJson(bodyRequest);
        recordApi.createRecordInstance(request);
    }

    @Step
    public void updateTask(String taskGuid, String fieldToUpdate, String newFieldValue) {
        JsonObject bodyRequest = new JsonObject();
        bodyRequest.addProperty("resourceType", "com.bmc.arsys.rx.services.record.domain.RecordInstance");
        bodyRequest.addProperty("id", taskGuid);
        bodyRequest.addProperty("recordDefinitionName", RestApiData.TASK_RECORD_DEFINITION);
        JsonObject recordProperty = new JsonObject();
        addRecordFieldToRequest(recordProperty, fieldToUpdate, newFieldValue); // submitter of the record instance
        bodyRequest.add("fieldInstances", recordProperty);
        String request = new GsonBuilder().create().toJson(bodyRequest);
        recordApi.updateRecordInstance(RestApiData.TASK_RECORD_DEFINITION, taskGuid, request);
    }

    private JsonObject addRecordFieldToRequest(JsonObject recordProperty, String fieldId, String fieldValue) {
        JsonObject nestedArray = new JsonObject();
        nestedArray.addProperty("resourceType", "com.bmc.arsys.rx.services.record.domain.FieldInstance");
        nestedArray.addProperty("id", fieldId);
        nestedArray.addProperty("value", fieldValue);
        recordProperty.add(fieldId, nestedArray);
        return recordProperty;
    }

    /**
     * Get all existing view definitions
     *
     * @return list of all created view definitions
     */
    public String getAllViewDefinitions() {
        return viewApi.getAllViewDefinitions().extract().asString();
    }

    /**
     * Get ID of the specified view definition with the {@param viewDefinitionName}
     * View definition should be in the task-manager bundle.
     *
     * @param viewDefinitionName view definition name
     * @return view definition ID
     */
    public String getViewDefinitionId(String viewDefinitionName) {
        String absoluteViewDefinitionName = RestApiData.TASK_MANAGER_HEADER + ":" + viewDefinitionName;
        return JsonPath.from(getAllViewDefinitions()).get("data.find { it.name == '" + absoluteViewDefinitionName + "' }.id");
    }

    /**
     * Get json of the view definition with the {@param viewDefinitionId}
     *
     * @param viewDefinitionId ID of the view definition
     * @return view definition json
     */
    public String getViewDefinition(String viewDefinitionId) {
        return viewApi.getViewDefinition(viewDefinitionId).extract().asString();
    }

    /**
     * @param viewDefinitionName name of View Definition
     * @return list of names for all Components, present in viewDefinitionName
     */
    public List<String> getAllComponentsForViewDefinition(String viewDefinitionName) {
        List<String> allComponents = JsonPath.from(getViewDefinition(getViewDefinitionId(viewDefinitionName)))
                .getList("componentDefinitions.type");
        for (String componentType : allComponents) {
            Collections.replaceAll(allComponents, componentType, Component.getNameByType(componentType));
        }
        LOG.info("\n--> Components for View '" + viewDefinitionName + "': " + allComponents);
        return allComponents;
    }

    /**
     * Get ID of the specified {@param componentDefinitionType} from the view definition with {@param viewDefinitionId}
     * If there are more than one the same typed component on a Canvas, this method returns the first one ID
     *
     * @param viewDefinitionId        ID of the view definition
     * @param componentDefinitionType type of the component (e.g. 'rx-activity-feed')
     * @return ID of the component
     */
    public String getComponentDefinitionId(String viewDefinitionId, String componentDefinitionType) {
        return JsonPath.from(getViewDefinition(viewDefinitionId)).getList("componentDefinitions.findAll " +
                "{ it.type == '" + componentDefinitionType + "' }.id").get(0).toString();
    }

    /**
     * @param viewDefinitionName
     * @param componentName
     * @return Map of properties and their values for required {@viewDefinitionName} and {@componentName}.
     * Where keys - properties of component, values - values of property correspondingly
     */
    public HashMap<String, String> getMapOfComponentProperties(String viewDefinitionName, String componentName) {
        String componentType = Component.getTypeByName(componentName);
        Map<String, String> propertiesMap = JsonPath.from(getViewDefinition(getViewDefinitionId(viewDefinitionName)))
                .getMap("componentDefinitions.find " +
                        "{ it.type == '" + componentType + "' }.propertiesByName");
        //Replacing all Map's keys from camelCaseFormat to Property Names Format
        HashMap<String, String> formattedMap = new HashMap<>();
        for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
            formattedMap.put(Property.splitCamelCase(entry.getKey()), entry.getValue());
        }
        return formattedMap;
    }

    /**
     * Download file with the {@param attachmentName} name that was uploaded for the task
     *
     * @param taskGuid       task GUID
     * @param attachmentName name of the file that was attached to the task
     * @return downloaded file
     */
    @Step
    public File getTaskAttachment(String taskGuid, String attachmentName) {
        LOG.info("Downloading an attachment with the name: " + attachmentName + " of the task with GUID: " + taskGuid);
        File downloadedFile = null;
        try {
            InputStream is =
                    recordApi.getRecordInstanceContent(RestApiData.TASK_RECORD_DEFINITION, taskGuid, attachmentName)
                            .extract().asInputStream();
            downloadedFile = new File(System.getProperty("java.io.tmpdir"), attachmentName);
            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(downloadedFile));
            byte[] b = new byte[1024 * 1024];
            int read;
            while ((read = is.read(b)) > -1) {
                bout.write(b, 0, read);
            }
            bout.flush();
            bout.close();
            is.close();
        } catch (IOException e) {
            LOG.error("Cannot download a file " + attachmentName + " from server: " + e);
            e.printStackTrace();
        }
        return downloadedFile;
    }

    public void logout() {
        userManagementApi.logout();
    }
}