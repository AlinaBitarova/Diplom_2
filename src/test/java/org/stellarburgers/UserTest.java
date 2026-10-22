package org.stellarburgers;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserTest {

    private AdditionalData additionalData;
    private User user;

    @Before
    public void setUp() {
        additionalData = new AdditionalData();
        user = additionalData.getRandomUser();
    }

    @Test
    @Description("Check status code of valid user creation")
    public void testUserCanBeCreatedWithValidData() {
        Response response = additionalData.createUser(user);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @Description("Check status code of two identical users creation")
    public void testCantCreateTwoIdenticalUsers() {
        additionalData.createUser(user);
        Response response = additionalData.createUser(user);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("message", containsString(AdditionalData.USER_ALREADY_EXISTS_ERROR));
    }

    @Test
    @Description("Check status code of invalid user creation without email")
    public void testUserCantBeCreatedWithoutLogin() {
        User userWithoutEmail = additionalData.getUserWithoutEmail();
        Response response = additionalData.createUser(userWithoutEmail);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("message", containsString(AdditionalData.LACK_DATA_ERROR));
    }

    @Test
    @Description("Check status code of invalid user creation without password")
    public void testUserCantBeCreatedWithoutPassword() {
        User userWithoutPassword = additionalData.getUserWithoutPassword();
        Response response = additionalData.createUser(userWithoutPassword);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("message", containsString(AdditionalData.LACK_DATA_ERROR));
    }

    @After
    public void tearDown() {
        additionalData.deleteUser(user);
    }
}