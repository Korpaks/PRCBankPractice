package com.PNCBank.Test;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;


public class Practice {

    @BeforeEach
    public  void setUp() {
        baseURI = "https://restcountries.com/v3.1/";
    }

    @AfterEach
    public  void tearDown() {
        reset();
    }

    // method for getting the capital city
    public static String getCapitalCity(String nameOrCode) {
        JsonPath jp;
        try {
            if (nameOrCode.isEmpty()) {
                return "Must pass some values as input";
            } else if (nameOrCode.length() == 3 || nameOrCode.length() == 2) { //since there are no countries with 2 or 3 letters, it can be only abbreviation
                basePath = "/alpha";
                jp = given().accept(ContentType.JSON)
                        .and().pathParam("code", nameOrCode)
                        .when().get("/{code}")
                        .then().extract().jsonPath();

            } else {
                basePath = "/name";
                jp = given().accept(ContentType.JSON)
                        .and().pathParam("name", nameOrCode)
                        .when().get("/{name}")
                        .then().extract().jsonPath();
            }
            List<List<String>> str = jp.get("capital");
            return str.get(0).get(0);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "Page not found";
        }
    }
    // checking if the method works
    @Test
    public void tryOutMethodTest(){
        String capital = getCapitalCity("narnia");
        System.out.println(capital);
    }

    @DisplayName("GET/ Capital City with valid parameters")
    @Test
    public void getCapitalByName(){
        basePath = "/name";
        String country = "Serbia";
        JsonPath jp = given().log().all()
                .accept(ContentType.JSON)
                .and().pathParam("name", country)
                .when().get("/{name}")
                .then().statusCode(200)
                .and().contentType(ContentType.JSON)
                .and().extract().jsonPath();

        jp.prettyPrint();

       List<List<String>> str = jp.get("capital");
        System.out.println(str.get(0).get(0));
    }

    @DisplayName("GET/ Capital City with invalid parameters")
    @Test
    public void getCapitalByNameNegativePath(){
        basePath = "/name";
        String country = "Narnia"; // we could create a List of all countries that we have on this API and check if the input value is not on this list that it is country that doesn't exist, return error
        JsonPath jp = given().log().all()
                .accept(ContentType.JSON)
                .and().pathParam("name", country)
                .when().get("/{name}")
                .then().statusCode(404)
                .and().contentType(ContentType.JSON)
                .and().extract().jsonPath();

        jp.prettyPrint();
        String message = jp.getString("message");
        assertEquals("Not Found", message);

    }

    @DisplayName("GET/ Capital City with null or empty parameters")
    @Test
    public void getCapitalByNameDestructivePath(){
        basePath = "/name";
        String country = "";
        JsonPath jp = given().log().all()
                .accept(ContentType.JSON)
                .and().pathParam("name", country)
                .when().get("/{name}")
                .then().statusCode(404)
                .and().contentType(ContentType.JSON)
                .and().extract().jsonPath();

        jp.prettyPrint();
        String message = jp.getString("message");
        assertEquals("Page Not Found", message);
    }

    @DisplayName("GET/ Capital City with Code")
    @Test
    public void getCapitalByCodePositivePath(){
        basePath = "/alpha";
        String code = "uSa"; // the API is case-insensitive so passing this values will work
        JsonPath jp = given().log().all()
                .accept(ContentType.JSON)
                .and().pathParam("code", code)
                .when().get("/{code}")
                .then().statusCode(200)
                .and().contentType(ContentType.JSON)
                .and().extract().jsonPath();

        jp.prettyPrint();
        String capital = jp.getString("capital");
        System.out.println(capital);
    }

    @DisplayName("GET/ Capital City with not existing Code")
    @Test
    public void getCapitalByCodeNegativePath(){
        basePath = "/alpha";
        String code = "44";
        JsonPath jp = given().log().all()
                .accept(ContentType.JSON)
                .and().pathParam("code", code)
                .when().get("/{code}")
                .then().statusCode(404)
                .and().contentType(ContentType.JSON)
                .and().extract().jsonPath();

        jp.prettyPrint();
        String message = jp.getString("message");
        assertEquals("Not Found", message);
    }

    @DisplayName("GET/ Capital City with an empty input code")
    @Test
    public void getCapitalByCodeDestructivePath(){
        basePath = "/alpha";
        String code = "";
        JsonPath jp = given().log().all()
                .accept(ContentType.JSON)
                .and().pathParam("code", code)
                .when().get("/{code}")
                .then().statusCode(400)
                .and().contentType(ContentType.JSON)
                .and().extract().jsonPath();

        jp.prettyPrint();
        String message = jp.getString("message");
        assertEquals("Required argument [String codes] not specified", message);
    }

}
