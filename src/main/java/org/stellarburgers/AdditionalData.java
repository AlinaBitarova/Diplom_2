package org.stellarburgers;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.datafaker.Faker;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

public class AdditionalData {

    public static final String BASE_URL = "https://qa-stellarburgers.education-services.ru"; // вместо официального стенда https://stellarburgers.education-services.ru"

    public static final String CREATE_USER_PATH = "/api/auth/register";
    public static final String USER_LOGIN_PATH = "/api/auth/login";
    public static final String USER_PATH = "/api/auth/user";
    public static final String ORDER_PATH = "/api/orders";

    public static final String USER_ALREADY_EXISTS_ERROR = "User already exists";
    public static final String LACK_DATA_ERROR = "Email, password and name are required fields";
    public static final String UNAUTHORIZED_ERROR = "email or password are incorrect";
    public static final String WRONG_INGREDIENTS_ERROR = "Ingredient ids must be provided";

    private static final Faker faker = new Faker();

    @Step("Requesting Base Url")
    public RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .build();
    }

    @Step("Sending POST request to create a user")
    public Response createUser(User user) {
        return given()
                .spec(getRequestSpec())
                .body(user)
                .post(CREATE_USER_PATH);
    }

    @Step("Sending POST request to login")
    public Response loginUser(Object credentials) {
        return given()
                .spec(getRequestSpec())
                .body(credentials)
                .post(USER_LOGIN_PATH);
    }

    @Step("Sending POST request to login with User")
    public Response loginUser(User user) {
        return loginUser(Map.of(
                "email", user.getEmail(),
                "password", user.getPassword()
        ));
    }

    @Step("Sending POST request to create an order without logging in")
    public Response createOrderWithoutLoggingIn(Object ingredients) {
        return given()
                .spec(getRequestSpec())
                .body(ingredients)
                .post(ORDER_PATH);
    }

    @Step("Sending POST request to create an order with logging in")
    public Response createOrderWithLoggingIn(Object ingredients, String accessToken) {
        return given()
                .spec(getRequestSpec())
                .header("Authorization", accessToken)
                .body(ingredients)
                .post(ORDER_PATH);
    }

    @Step("Creating a valid user via Faker")
    public User getRandomUser() {
        return new User(
                faker.internet().emailAddress(),
                faker.internet().password(),
                faker.name().name()
        );
    }

    @Step("Creating an invalid user without email")
    public User getUserWithoutEmail() {
        return new User(null, faker.internet().password(), faker.name().name());
    }

    @Step("Creating an invalid user without password")
    public User getUserWithoutPassword() {
        return new User(faker.internet().emailAddress(), null, faker.name().name());
    }

    @Step("Deleting a user (with logging in)")
    public void deleteUser(User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return;
        }

        Response loginResponse = loginUser(user);

        if (loginResponse.getStatusCode() == SC_ACCEPTED) {
            String accessToken = loginResponse.path("accessToken");

            if (accessToken != null) {
                given()
                        .spec(getRequestSpec())
                        .header("Authorization", accessToken)
                        .when()
                        .delete(USER_PATH)
                        .then()
                        .statusCode(SC_OK);
            }
        }
    }
}
