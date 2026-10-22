package org.stellarburgers;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class LoginTest {

    private AdditionalData additionalData;
    private User user;

    @Before
    public void setUp() {
        additionalData = new AdditionalData();
        user = additionalData.getRandomUser();
        additionalData.createUser(user);
    }

    @Test
    @Description("Check status code of valid log in")
    public void testUserCanLogInWithValidData() {
        Response response = additionalData.loginUser(user);
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @Description("Check status code of invalid log in with incorrect email")
    public void testUserCantLogInWithInvalidEmail() {
        Map<String, String> incorrectCredentials = Map.of(
                "email", "incorrect_email123@qa.com",
                "password", user.getPassword()
        );
        Response response = additionalData.loginUser(incorrectCredentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString(AdditionalData.UNAUTHORIZED_ERROR));
    }

    @Test
    @Description("Check status code of invalid log in with incorrect password")
    public void testUserCantLogInWithInvalidPassword() {
        Map<String, String> incorrectCredentials = Map.of(
                "email", user.getEmail(),
                "password", "wrong_password1"
        );
        Response response = additionalData.loginUser(incorrectCredentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString(AdditionalData.UNAUTHORIZED_ERROR));
    }

    @Test
    @Description("Check status code of invalid log in without email")
    public void testUserCantLogInWithoutEmail() {
        Map<String, String> credentials = Map.of(
                "password", user.getPassword()
        );
        Response response = additionalData.loginUser(credentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString(AdditionalData.UNAUTHORIZED_ERROR));
    }

    @Test
    @Description("Check status code of invalid log in without password")
    public void testUserCantLogInWithoutPassword() {
        Map<String, String> credentials = Map.of(
                "email", user.getEmail()
        );
        Response response = additionalData.loginUser(credentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString(AdditionalData.UNAUTHORIZED_ERROR));
    }

    @Test
    @Description("Check status code of invalid log in with unregistered credentials")
    public void testLoggingInAsUnregisteredUserReturnsError() {
        Map<String, String> unregisteredCredentials = Map.of(
                "email", "unregistered_email@qa.com",
                "password", "unregistered_password"
        );
        Response response = additionalData.loginUser(unregisteredCredentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString(AdditionalData.UNAUTHORIZED_ERROR));
    }

    @After
    public void tearDown() {
        additionalData.deleteUser(user);
    }
}
