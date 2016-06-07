package com.bmc.gibraltar.automation.api;

import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;

import java.net.HttpURLConnection;

import static com.jayway.restassured.RestAssured.given;

public class RuleApi extends RestApiData {

    public RuleApi() {
        super();
    }

    public RuleApi(String loginName, String userPassword) {
        super(loginName, userPassword);
    }

    public RuleApi(RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    public ValidatableResponse createRuleDefinition(String jsonOfRuleDefinition) {
        return given()
                .spec(requestSpecification)
                .body(jsonOfRuleDefinition)
                .when()
                .post("/api/rx/application/rule/ruledefinition")
                .then().statusCode(HttpURLConnection.HTTP_CREATED);
    }
}