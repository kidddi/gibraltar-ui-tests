package com.bmc.gibraltar.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

public final class JsonUtils {
    private static final Configuration CONFIGURATION = Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();

    private JsonUtils() {
    }

    private static DocumentContext removeKeys(DocumentContext jsonContext, String[] keysThatShouldBeRemoved) {
        for (String path : keysThatShouldBeRemoved) {
            jsonContext = jsonContext.delete(path);
        }
        return jsonContext;
    }

    private static DocumentContext clearKeysValue(DocumentContext jsonContext, String[] keysThatShouldBeCleared) {
        for (String path : keysThatShouldBeCleared) {
            jsonContext = jsonContext.set(path, "");
        }
        return jsonContext;
    }

    /**
     * Removes/clears process definition json from system info
     *
     * @return json without system info
     */
    public static DocumentContext cleanJson(String originalJson) {
        DocumentContext jsonContext = JsonPath.using(CONFIGURATION).parse(originalJson);
        String[] keysToRemove = {"$..owner", "$..version", "$..lastUpdateTime", "$..lastChangedBy",
                "$..isOriginalDefinition", "$..organizationScope"};
        String[] keysToClear = {"$.name", "$.runAsUser"};
        jsonContext = clearKeysValue(jsonContext, keysToClear);
        jsonContext = removeKeys(jsonContext, keysToRemove);
        return jsonContext;
    }

    public static String removeAllIDs(DocumentContext jsonContext) {
        String originalJson = jsonContext.json().toString();
        // removing the process definition ID
        JsonNode processDefinitionId = jsonContext.read("$.id");
        String processDefId = processDefinitionId.textValue().replaceAll("rx-", "");
        originalJson = originalJson.replaceAll(processDefId, "");
        // removing the flowElement IDs
        ArrayNode flowElementsIds = jsonContext.read("$.flowElements..id");
        for (JsonNode id : flowElementsIds) {
            String elId = id.textValue().replaceAll("rx-", "");
            originalJson = originalJson.replaceAll(elId, "");
        }
        return originalJson;
    }

    public static String generateNewFlowElementIDs(String originalJson) {
        DocumentContext jsonContext = JsonPath.using(CONFIGURATION).parse(originalJson);
        ArrayNode flowElementsIds = jsonContext.read("$.flowElements..id");
        for (JsonNode id : flowElementsIds) {
            String elId = id.textValue().replaceAll("rx-", "");
            originalJson = originalJson.replaceAll(elId, UUID.randomUUID().toString());
        }
        return originalJson;
    }

    public static DocumentContext updateJsonProperty(String originalJson, String path, Object newValue) {
        DocumentContext jsonContext = JsonPath.using(CONFIGURATION).parse(originalJson);
        jsonContext = jsonContext.set(path, newValue);
        return jsonContext;
    }

    /**
     * Generating process definition name and ID for the copy of the process definition
     *
     * @param jsonContext                 of the original process definition for which the copy will be created
     * @param processDefinitionNameOfCopy name of the copy
     * @return json of the process definition copy with updated name and ID
     */
    public static String generateNewProcessDefinitionIdAndName(DocumentContext jsonContext, String processDefinitionNameOfCopy) {
        JsonNode processDefinitionId = jsonContext.read("$.id");
        String oldProcessDefinitionId = processDefinitionId.textValue();
        JsonNode processDefinitionName = jsonContext.read("$.name");
        String oldProcessDefinitionName = processDefinitionName.textValue();
        String updatedJson = jsonContext.json().toString();
        updatedJson = updatedJson.replaceAll(oldProcessDefinitionName, "task-manager:" + processDefinitionNameOfCopy);
        updatedJson = updatedJson.replaceAll(oldProcessDefinitionId, "rx-" + UUID.randomUUID().toString());
        return updatedJson;
    }

    /**
     * Generating process definition name and ID for the copy of the process definition
     *
     * @param jsonContext                 of the original process definition for which the copy will be created
     * @param processDefinitionNameOfCopy name of the copy
     * @return json of the process definition copy with the updated process definition name, process definition ID
     * and updated flow elements IDs
     */
    public static String createCopyOfProcessDefinitionJson(DocumentContext jsonContext, String processDefinitionNameOfCopy) {
        String updatedJson = generateNewProcessDefinitionIdAndName(jsonContext, processDefinitionNameOfCopy);
        updatedJson = generateNewFlowElementIDs(updatedJson);
        return updatedJson;
    }

    public static DocumentContext getDocumentContext(String originalJson) {
        return JsonPath.using(CONFIGURATION).parse(originalJson);
    }

    /**
     * converts json that is located in {@param fileName} to String
     * {@param fileName} should be placed into "src/com/bmc/gibraltar/automation/dataprovider/jsons/" folder
     *
     * @param fileName name of file with json
     * @return json converted to String
     */
    public static String getJsonFromFile(String fileName) {
        String json;
        try (JsonReader reader = new JsonReader(
                new FileReader("src/com/bmc/gibraltar/automation/dataprovider/jsons/" + fileName))) {
            json = new GsonBuilder().create().toJson(new JsonParser().parse(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
