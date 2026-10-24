package org.stellarburgers;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        User userWithIncorrectCredentials = new User("incorrectemail@qa.com", user.getPassword()
        );
        Response response = additionalData.loginUser(userWithIncorrectCredentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString(AdditionalData.UNAUTHORIZED_ERROR));
    }

    @Test
    @Description("Check status code of invalid log in with incorrect password")
    public void testUserCantLogInWithInvalidPassword() {
        User userWithIncorrectCredentials = new User(user.getEmail(),"wrong_password1"
        );
        Response response = additionalData.loginUser(userWithIncorrectCredentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString(AdditionalData.UNAUTHORIZED_ERROR));
    }

    @Test
    @Description("Check status code of invalid log in without email")
    public void testUserCantLogInWithoutEmail() {
        User userWithoutLogin = new User(null, user.getPassword()
        );
        Response response = additionalData.loginUser(userWithoutLogin);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString(AdditionalData.UNAUTHORIZED_ERROR));
    }

    @Test
    @Description("Check status code of invalid log in without password")
    public void testUserCantLogInWithoutPassword() {
        User userWithoutPassword = new User(user.getEmail(), null
        );
        Response response = additionalData.loginUser(userWithoutPassword);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString(AdditionalData.UNAUTHORIZED_ERROR));
    }

    @Test
    @Description("Check status code of invalid log in with unregistered credentials")
    public void testLoggingInAsUnregisteredUserReturnsError() {
        User userWithUnregisteredCredentials = new User("unregistered_email@qa.com", "unregistered_password"
        );
        Response response = additionalData.loginUser(userWithUnregisteredCredentials);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", containsString(AdditionalData.UNAUTHORIZED_ERROR));
    }

    @After
    public void tearDown() {
        if (user != null) {
            additionalData.deleteUser(user);
        }
    }
}
