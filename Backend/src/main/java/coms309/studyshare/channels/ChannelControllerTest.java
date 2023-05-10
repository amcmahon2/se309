package coms309.studyshare.channels;



import coms309.studyshare.auth.token.TokenRepository;
import coms309.studyshare.users.User;
import coms309.studyshare.users.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.UUID;

import static coms309.studyshare.utils.TestUtil.AUTH_TOKEN;
import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

@RunWith(SpringRunner.class)
public class ChannelControllerTest {




    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ChannelRepository channelRepo;


    @Before
    public void setUp() {
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://localhost";
    }


    @Test
    public void createChannel() {
        JSONObject channel =  new JSONObject();
        channel.put("name", "testChannel");
        channel.put("hidden", false);

        Response response = RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .body(channel.toString())
                .when()
                .post("/channel/create");


        assertEquals(response.getStatusCode(), 201);
    }

    @Test
    public void createChannelInvalidToken() {
        JSONObject channel =  new JSONObject();
        channel.put("name", "testChannel");
        channel.put("hidden", false);

        Response response = RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + "token")
                .body(channel.toString())
                .when()
                .post("/channel/create");


        assertEquals(response.getStatusCode(), 403);
    }


    @Test
    public void listChannels() {


        Response response = RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .when()
                .get("/channel/listAll");


        assertEquals(response.getStatusCode(), 200);

        JSONArray array = new JSONArray(response.getBody().asString());
        assertEquals(array.length(), channelRepo.findAll().size());

    }







}
