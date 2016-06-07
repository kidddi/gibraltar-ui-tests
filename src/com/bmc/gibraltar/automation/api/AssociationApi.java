package com.bmc.gibraltar.automation.api;

import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;

import java.net.HttpURLConnection;

import static com.jayway.restassured.RestAssured.given;

public class AssociationApi extends RestApiData {

    public AssociationApi() {
        super();
    }

    public AssociationApi(String loginName, String userPassword) {
        super(loginName, userPassword);
    }

    public AssociationApi(RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    public ValidatableResponse createAssociationDefinition(String jsonOfAssociationDefinition) {
        return given()
                .spec(requestSpecification)
                .body(jsonOfAssociationDefinition)
                .when()
                .post("/api/rx/application/association/associationdefinition")
                .then().statusCode(HttpURLConnection.HTTP_CREATED);
    }
}