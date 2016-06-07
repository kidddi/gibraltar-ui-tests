package com.bmc.gibraltar.automation.api;

import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;

import java.net.HttpURLConnection;

import static com.jayway.restassured.RestAssured.given;

public class ProcessApi extends RestApiData {

    public ProcessApi() {
        super();
    }

    public ProcessApi(String loginName, String userPassword) {
        super(loginName, userPassword);
    }

    public ProcessApi(RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    public ValidatableResponse createProcessDefinition(String jsonOfProcessDefinition) {
        return given()
                .spec(requestSpecification)
                .body(jsonOfProcessDefinition)
                .when()
                .post("/api/rx/application/process/processdefinition")
                .then().statusCode(HttpURLConnection.HTTP_CREATED);
    }

    public ValidatableResponse getProcessDefinitionJson(String processDefinitionName) {
        return getProcessDefinition(processDefinitionName).statusCode(HttpURLConnection.HTTP_OK);
    }

    public ValidatableResponse getProcessDefinition(String processDefinitionName) {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/process/processdefinition/{processDefinitionName}", processDefinitionName)
                .then();
    }

    public ValidatableResponse getAllProcessDefinitionsInfo() {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/datapage/?dataPageType=com.bmc.arsys.rx.application.process.datapage" +
                        ".ProcessDefinitionDataPageQuery&pageSize=-1&startIndex=0")
                .then().statusCode(HttpURLConnection.HTTP_OK);
    }

    public ValidatableResponse startProcessInstance(String bodyOfRequest) {
        return given()
                .spec(requestSpecification)
                .body(bodyOfRequest)
                .when()
                .post("/api/rx/application/command")
                .then().statusCode(HttpURLConnection.HTTP_OK);
    }

    public ValidatableResponse getInputsOfProcessDefinition(String processDefinitionName) {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/process/processdefinition/{processDefinitionName}/inputParams",
                        processDefinitionName)
                .then().statusCode(HttpURLConnection.HTTP_OK);
    }
}