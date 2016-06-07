package com.bmc.gibraltar.automation.api;

import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;
import ru.yandex.qatools.allure.annotations.Step;

import java.net.HttpURLConnection;

import static com.jayway.restassured.RestAssured.given;

public class RecordApi extends RestApiData {

    public RecordApi() {
        super();
    }

    public RecordApi(String loginName, String userPassword) {
        super(loginName, userPassword);
    }

    public RecordApi(RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    public ValidatableResponse createRecordDefinition(String jsonOfRecordDefinition) {
        return given()
                .spec(requestSpecification)
                .body(jsonOfRecordDefinition)
                .when()
                .post("/api/rx/application/record/recorddefinition")
                .then().statusCode(HttpURLConnection.HTTP_CREATED);
    }

    public ValidatableResponse getAbsoluteRecordDefinitionNames() {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/datapage/?dataPageType=com.bmc.arsys.rx.application.record.datapage" +
                        ".RecordDefinitionNamesDataPageQuery&pageSize=-1&startIndex=0")
                .then().statusCode(HttpURLConnection.HTTP_OK);
    }

    public ValidatableResponse getRecordDefinitionJson(String absoluteRecordDefinitionName) {
        return getRecordDefinition(absoluteRecordDefinitionName).statusCode(HttpURLConnection.HTTP_OK);
    }

    public ValidatableResponse getRecordDefinition(String absoluteRecordDefinitionName) {
        return given()
                .spec(requestSpecification)
                .when()
                .urlEncodingEnabled(false)
                .get("/api/rx/application/record/recorddefinition/" + absoluteRecordDefinitionName)
                .then();
    }

    public ValidatableResponse getRecordInstance(String recordDefinitionName, String guid) {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/record/recordinstance/{recordDefinitionName}/{guid}", recordDefinitionName, guid)
                .then().statusCode(HttpURLConnection.HTTP_OK);
    }

    public ValidatableResponse getRecordInstances(String recordDefinitionName) {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/datapage/?dataPageType=com.bmc.arsys.rx.application." +
                        "record.datapage.RecordInstanceDataPageQuery&startIndex=0&pageSize=10" +
                        "&recorddefinition={recordDefinitionName}", recordDefinitionName)
                .then().statusCode(HttpURLConnection.HTTP_OK);
    }

    public ValidatableResponse createRecordInstance(String bodyRequest) {
        return given()
                .spec(requestSpecification)
                .body(bodyRequest)
                .when()
                .post("/api/rx/application/record/recordinstance")
                .then().statusCode(HttpURLConnection.HTTP_CREATED);
    }

    public ValidatableResponse updateRecordInstance(String recordDefinitionName, String guid, String jsonToUpdateRecordInstance) {
        return given()
                .spec(requestSpecification)
                .body(jsonToUpdateRecordInstance)
                .when()
                .put("/api/rx/application/record/recordinstance/{recordDefinitionName}/{guid}", recordDefinitionName, guid)
                .then().statusCode(HttpURLConnection.HTTP_NO_CONTENT);
    }

    public ValidatableResponse getRecordInstanceContent(String recordDefinitionName, String guid, String attachmentName) {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/content/recordinstance/{recordDefinitionName}/{guid}/{attachmentName}",
                        recordDefinitionName, guid, attachmentName)
                .then().statusCode(HttpURLConnection.HTTP_OK);
    }

    @Step
    public ValidatableResponse getRecordInstancesByFieldValue(String recordDefinitionName, String fieldId, String fieldValue) {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/datapage?dataPageType=com.bmc.arsys.rx.application.record" +
                                ".datapage.RecordInstanceDataPageQuery&pageSize=25" +
                                "&{fieldId}={fieldValue}&recorddefinition={recordDefinitionName}&sortBy=-1&startIndex=0",
                        fieldId, fieldValue, recordDefinitionName)
                .then().statusCode(HttpURLConnection.HTTP_OK);
    }
}