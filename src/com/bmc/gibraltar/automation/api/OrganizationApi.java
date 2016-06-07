package com.bmc.gibraltar.automation.api;

import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;

import java.net.HttpURLConnection;

import static com.jayway.restassured.RestAssured.given;

public class OrganizationApi extends RestApiData {

    public OrganizationApi(String loginName, String userPassword) {
        super(loginName, userPassword);
    }

    public OrganizationApi() {
        super();
    }

    public OrganizationApi(RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    public ValidatableResponse createOrganization(String jsonOfOrganizationRecordInstance) {
        return given()
                .spec(requestSpecification)
                .when()
                .body(jsonOfOrganizationRecordInstance)
                .post("/api/rx/foundation/organization/application/organizationrecordinstance")
                .then().statusCode(HttpURLConnection.HTTP_CREATED);
    }
}