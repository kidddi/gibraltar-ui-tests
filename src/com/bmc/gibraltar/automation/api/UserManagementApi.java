package com.bmc.gibraltar.automation.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;

import java.net.HttpURLConnection;

import static com.jayway.restassured.RestAssured.given;

public class UserManagementApi extends RestApiData {

    public UserManagementApi() {
        super();
    }

    public UserManagementApi(String loginName, String userPassword) {
        super(loginName, userPassword);
    }

    public UserManagementApi(RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    public static JsonObject generateJsonForUserCreation(String fullName, String loginName, String password
            , String emailAddress, String[] permissionGroups) {
        JsonObject request = new JsonObject();
        request.addProperty("fullName", fullName);
        request.addProperty("loginName", loginName);
        request.addProperty("password", password);
        request.addProperty("emailAddress", emailAddress);
        request.add("groups", new Gson().toJsonTree(permissionGroups));
        return request;
    }

    public ValidatableResponse getUserInfo(String userName) {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/user/{userLoginName}", userName)
                .then();
    }

    /**
     * Will create user with the details specified in {@param jsonString}
     *
     * @param jsonString request body
     */
    public ValidatableResponse createUserRequest(String jsonString) {
        return given().spec(requestSpecification).body(jsonString).post("/api/rx/application/user")
                .then().statusCode(HttpURLConnection.HTTP_CREATED);
    }
}