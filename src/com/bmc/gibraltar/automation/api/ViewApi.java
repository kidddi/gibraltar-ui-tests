package com.bmc.gibraltar.automation.api;

import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;

import java.net.HttpURLConnection;

import static com.jayway.restassured.RestAssured.given;

public class ViewApi extends RestApiData {

    public ViewApi() {
        super();
    }

    public ViewApi(String loginName, String userPassword) {
        super(loginName, userPassword);
    }

    public ViewApi(RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    /**
     * Get all existing view definitions
     *
     * @return list of all created view definitions
     */
    public ValidatableResponse getAllViewDefinitions() {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/datapage/?dataPageType=com.bmc.arsys.rx.application.view.datapage" +
                        ".ViewDefinitionDataPageQuery&pageSize=-1&startIndex=0")
                .then().statusCode(HttpURLConnection.HTTP_OK);
    }

    /**
     * Get json of the view definition with the {@param viewDefinitionId}
     *
     * @param viewDefinitionId ID of the view definition
     * @return view definition json
     */
    public ValidatableResponse getViewDefinition(String viewDefinitionId) {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/rx/application/view/viewdefinition/" + viewDefinitionId)
                .then().statusCode(HttpURLConnection.HTTP_OK);
    }
}