package com.bmc.gibraltar.automation.api;

import com.bmc.gibraltar.automation.framework.utils.PropertiesUtils;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.LogConfig.logConfig;
import static com.jayway.restassured.config.RestAssuredConfig.config;

public class RestApiData {
    public static final String TASK_MANAGER_HEADER = "task-manager";
    public static final String FOUNDATION_HEADER = "foundation";
    public static final String APPROVAL_HEADER = "approval";
    public static final String USER_MESSAGE_HEADER = "usermessage";
    public static final String RXN_HEADER = "rxn:/";
    public static final String TASK_RECORD_DEFINITION = "task-manager:Task";
    public static final String BUNDLE_SCOPE_HEADER = "default-bundle-scope";
    private static String appServerUrl = PropertiesUtils.getAppServerUrl();
    protected RequestSpecification requestSpecification;
    protected String username;
    protected String password;

    public RestApiData() {
        this(PropertiesUtils.getUsername(), PropertiesUtils.getPassword());
    }

    public RestApiData(String loginName, String userPassword) {
        setUpBaseSettings();
        username = loginName;
        password = userPassword;
        requestSpecification = buildRequestSpecification(loginName, userPassword);
    }

    public RestApiData(RequestSpecification requestSpecification) {
        setUpBaseSettings();
        this.requestSpecification = requestSpecification;
        // TODO: extract username and password from getCurrentUser request
    }

    private void setUpBaseSettings() {
        RestAssured.baseURI = appServerUrl;
        RestAssured.config = config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }

    public RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    protected String buildRequestBodyForLogin(String loginName, String password) {
        JsonObject loginInfo = new JsonObject();
        loginInfo.addProperty("resourceType", "com.bmc.arsys.rx.application.user.command.LoginCommand");
        loginInfo.addProperty("username", loginName);
        loginInfo.addProperty("password", password);
        return new GsonBuilder().create().toJson(loginInfo);
    }

    protected RequestSpecification buildRequestSpecification(String loginName, String password) {
        Map<String, String> cookies = login(loginName, password).and().extract().cookies();
        return new RequestSpecBuilder()
                .addCookies(cookies)
                .and()
                .setContentType(ContentType.JSON)
                .and()
                .addHeader(BUNDLE_SCOPE_HEADER, TASK_MANAGER_HEADER)
                .and().build()
                .and().log().ifValidationFails();
    }

    public ValidatableResponse login(String loginName, String password) {
        return given().log().ifValidationFails()
                .contentType(ContentType.JSON)
                .body(buildRequestBodyForLogin(loginName, password))
                .when()
                .post("/api/rx/application/command")
                .then().log().ifValidationFails()
                .statusCode(HttpURLConnection.HTTP_OK);
    }

    public ValidatableResponse login() {
        return login(username, password);
    }

    public List<String> getBundleScopeHeaders() {
        List<String> allHeaders = new ArrayList<>();
        allHeaders.add(TASK_MANAGER_HEADER);
        allHeaders.add(APPROVAL_HEADER);
        allHeaders.add(USER_MESSAGE_HEADER);
        allHeaders.add(FOUNDATION_HEADER);
        allHeaders.add(RXN_HEADER);
        return allHeaders;
    }

    public String generateAbsoluteDefinitionNameBasedOnHeader(String header, String recordDefinitionName) {
        if (!header.equals(RXN_HEADER)) {
            return header + ":" + recordDefinitionName;
        } else if (header.equals(RXN_HEADER)) {
            String recordDefName = recordDefinitionName.substring(1);
            return "rxn%3A%2F" + recordDefName;
        }
        return "";
    }

    public ValidatableResponse getCustomFieldDefinition() {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/datapage/?dataPageType=com.bmc.arsys.rx.application" +
                        ".record.datapage.FieldTypeDataPageQuery&pageSize=-1&startIndex=0")
                .then().log().ifValidationFails()
                .statusCode(HttpURLConnection.HTTP_OK);
    }

    public ValidatableResponse getAllPermittedGroups() {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/datapage?dataPageType=com.bmc.arsys.rx" +
                        ".application.group.datapage.GroupDataPageQuery&pageSize=-1&startIndex=0")
                .then().log().ifValidationFails()
                .statusCode(HttpURLConnection.HTTP_OK);
    }

    public ValidatableResponse logout() {
        return given()
                .spec(requestSpecification)
                .body("{\n" +
                        "  \"resourceType\": \"com.bmc.arsys.rx.application.user.command.LogoutCommand\"\n" +
                        "}\n")
                .when()
                .post("/api/rx/application/command")
                .then().log().ifValidationFails()
                .statusCode(HttpURLConnection.HTTP_OK);
    }
}