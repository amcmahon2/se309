package coms309.studyshare.files;



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


import java.io.File;
import java.util.UUID;

import static coms309.studyshare.utils.TestUtil.AUTH_TOKEN;
import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

@RunWith(SpringRunner.class)
public class FileControllerTests {


    private static String fileLink;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    FileDataRepository fileRepo;


    @Before
    public void setUp() {
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://localhost";
    }



    @Test
    public void listFiles() {


        Response response = RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .when()
                .get("/files");


        assertEquals(response.getStatusCode(), 200);

        JSONArray array = new JSONArray(response.getBody().asString());
        assertEquals(array.length(), fileRepo.findAll().size());

    }


    @Test
    public void uploadFile() {
        File file = new File("src/main/resources/test.txt");

        int numFiles = fileRepo.findAll().size();

        Response response = RestAssured.given()
                .multiPart("file", file)
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .when()
                .post("/upload");

        fileLink = response.getBody().asString();
        assertEquals(response.getStatusCode(), 200);
        assertTrue(numFiles < fileRepo.findAll().size());

    }


    @Test
    public void downloadFile() {

        int numFiles = fileRepo.findAll().size();

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .when()
                .get(fileLink).andReturn();

        assertEquals(response.getStatusCode(), 200);

    }









}
