package coms309.studyshare.auth;


import coms309.studyshare.auth.token.TokenRepository;
import coms309.studyshare.users.User;
import coms309.studyshare.users.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.UUID;

import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

@RunWith(SpringRunner.class)
public class AuthControllerTest {


    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepo;

    @Autowired
    AuthService authService;

    @Autowired
    TokenRepository tokenRepo;


    private static UUID userID;
    private static String userPass = "____";

    @Before
    public void setUp() {
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://localhost";
    }


    @Test
    public void registerUser_returnToken() {
        userID = UUID.randomUUID();
        JSONObject user =  new JSONObject();
        user.put("firstName", "test");
        user.put("lastName", "test");
        user.put("netID", userID.toString());
        user.put("password", userPass);

        Response response = RestAssured.given()
                            .contentType("application/json")
                            .body(user.toString())
                            .when()
                            .post("/auth/register");


        assertEquals(response.getStatusCode(), 200);


        JSONObject responseBody = new JSONObject(response.getBody().asString());
        assertEquals(responseBody.getString("token").isEmpty(), false);
    }


    @Test
    public void authenticateUser_returnToken() {
        JSONObject user =  new JSONObject();
        user.put("password", userPass);
        user.put("netID", userID.toString());

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(user.toString())
                .when()
                .post("/auth/authenticate");


        assertEquals(response.getStatusCode(), 200);

        JSONObject responseBody = new JSONObject(response.getBody().asString());
        assertEquals(responseBody.getString("token").isEmpty(), false);

    }


    @Test
    public void authenticateUserInvalidPassword() {
        JSONObject user =  new JSONObject();
        user.put("password", "wrong");
        user.put("netID", userID.toString());
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(user.toString())
                .when()
                .post("/auth/authenticate");

        assertEquals(response.getStatusCode(), 403);

    }


    @Test
    public void authenticateUserInvalidUser() {
        JSONObject user =  new JSONObject();
        user.put("password", userPass);
        user.put("netID", UUID.randomUUID());
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(user.toString())
                .when()
                .post("/auth/authenticate");

        assertEquals(response.getStatusCode(), 403);

    }




}
