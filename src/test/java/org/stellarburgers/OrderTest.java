package org.stellarburgers;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class OrderTest {

    private AdditionalData additionalData;
    private User user;
    private List<String> ingredients;

    @Before
    public void setUp() {
        additionalData = new AdditionalData();
        user = additionalData.getRandomUser();
        ingredients = List.of("6acef97852135b001b21a32e", "6acef97852135b001b21a332");
    }

    @Test
    @Description("Check status code of ordering after logging in")
    public void testShouldCreateOrderAfterLoggingIn() {
        additionalData.createUser(user);
        Response loginResponse = additionalData.loginUser(user);
        String accessToken = loginResponse.path("accessToken");
        Map<String, List<String>> order = Map.of("ingredients", ingredients);
        Response response = additionalData.createOrderWithLoggingIn(order, accessToken);
        response.then()
                .statusCode(SC_OK)
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Check status code of ordering without logging in")
    public void testShouldCreateOrderWithoutLoggingIn() {
        Map<String, List<String>> order = Map.of("ingredients", ingredients);
        Response response = additionalData.createOrderWithoutLoggingIn(order);
        response.then()
                .statusCode(SC_OK)
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Check status code of ordering without ingredients")
    public void testCantCreateOrderWithoutIngredients() {
        Map<String, List<String>> emptyOrder = Map.of("ingredients", List.of());
        Response response = additionalData.createOrderWithoutLoggingIn(emptyOrder);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString(AdditionalData.WRONG_INGREDIENTS_ERROR));
    }

    @Test
    @Description("Check status code of ordering non-existing ingredients")
    public void testCantCreateOrderWithInvalidIngredients() {
        List<String> invalidIngredients = List.of("invalid_ingredient1", "invalid_ingredient2");
        Map<String, List<String>> invalidOrder = Map.of("ingredients", invalidIngredients);
        Response response = additionalData.createOrderWithoutLoggingIn(invalidOrder);
        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void tearDown() {
        additionalData.deleteUser(user);
    }
}
