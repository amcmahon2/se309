package coms309.studyshare.courses;
import coms309.studyshare.users.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@RunWith(SpringRunner.class)
public class CourseControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    CourseUserPermissionsRepository cupRepo;

    @Autowired
    CourseRepository courseRepo;

    @Autowired
    UserRepository userRepo;

    private static String token = "";

    HttpHeaders headers = new HttpHeaders();

    @Before
    public void setUp() {
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://localhost";
        this.token = "";
        this.token += "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbmRyZXdGYWtlQ291cnNlIiwiaWF0IjoxNjgzMjQ1NTUzLCJleHAiOjE2ODMzMzE5NTN9.C133nJOlkwD9iup6JOZDT7h47SzOtS_NMrnmTmsU_fA";
        //for andrewFakeCourse
    }

    @Test
    public void courseTest() throws JSONException {
        //create the request body for auth/register
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("Authorization", "Bearer " + token).
                header("charset","utf-8").with().
                body("").
                when().
                post("/courses/create/A Test Course/aCode/andrewFakeCourse");

        //check the response
        assertEquals("OK", response.getBody().asString());
        assertNotEquals("UNAUTHORIZED", response.getBody().asString());
        assertNotEquals("CONFLICT",response.getBody().asString());
        assertNotEquals("NOT_FOUND",response.getBody().asString());

        //try to join the course with the same user, which shouldn't work
        Response response2 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("Authorization", "Bearer " + token).
                header("charset","utf-8").
                body("").
                when().
                put("/courses/joinCourseTest/aCode/andrewFakeCourse");

        //check the response
        assertEquals("CONFLICT", response2.getBody().asString());
        assertNotEquals("OK",response2.getBody().asString());
        assertNotEquals("NOT_FOUND",response2.getBody().asString());

        //now, try to join a non-real course
        Response response3 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("Authorization", "Bearer " + token).
                header("charset","utf-8").
                body("").
                when().
                put("/courses/joinCourseTest/aNotRealCode/andrewFakeCourse");

        //check the response
        assertNotEquals("CONFLICT", response3.getBody().asString());
        assertNotEquals("OK",response3.getBody().asString());
        assertEquals("NOT_FOUND",response3.getBody().asString());

        //now get the users for said course, which should be only my info
        Response response4 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization", "Bearer " + token).
                when().
                get("/courses/getInfoString/aCode");

        String response4Body = response4.getBody().asString();
        boolean hasUsersFor4 = response4Body.length() > 3 ? true : false;
        assertEquals(true,hasUsersFor4);




        Response response5 = given()
                .header("Authorization", "Bearer " + this.token)
                .header("Content-Type", "text/plain")
                .header("charset", "utf-8")
                .when()
                .get("/courses/getUsersString/aCode");

        String response5Body = response5.getBody().asString();
        boolean hasUsersFor5 = response5Body.length() > 3 ? true : false;
        assertEquals(true,hasUsersFor5);


        Response response6 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization", "Bearer " + token).
                body("").
                when().
                put("/courses/updatePermissions/andrewFakeCourse/aCode/TA/andrewFakeCourse");

        //check the response
        assertNotEquals("FORBIDDEN", response6.getBody().asString());
        assertEquals("OK",response6.getBody().asString());
        UUID user = userRepo.findByNetIDIgnoreCase("andrewFakeCourse").getUserID();
        UUID course = courseRepo.findCourseByJoinCode("aCode").getCourseID();
        String newRole = cupRepo.findCourseUserPermissionsByCourseIDAndUserID(course,user).getRole();

        //after changing role, it should now be "TA"
        assertEquals("TA", newRole);


        Response response8 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization", "Bearer " + token).
                body("").
                when().
                delete("/courses/removeUser/aCode/andrewFakeCourse/andrewFakeCourse");

        //check the response, should be forbidden as i just changed role to TA
        assertEquals("FORBIDDEN", response8.getBody().asString());
        assertNotEquals("OK",response8.getBody().asString());

        cupRepo.deleteAllByCourseID(courseRepo.findCourseByJoinCode("aCode").getCourseID());
        //remove me from the repo so this test can be run again
//        RestTemplate restTemplate = new RestTemplate();
//        JSONObject userInfo = restTemplate.getForObject("http://localhost:8080/users/getInfo/andrewFakeCourse", JSONObject.class);
//        UUID userID = UUID.fromString(userInfo.getString("ID"));
//        restTemplate.delete("http://localhost:8080/users/delete/" + userID);


    }

    @Test
    public void userTest() throws JSONException {
        //for andrew fake user
        String token1 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbmRyZXdGYWtlVXNlciIsImlhdCI6MTY4MzI0NTYwMCwiZXhwIjoxNjgzMzMyMDAwfQ.ZqExyY3m2oJqb0FXy7cLowWdeqzNSeC4ctIEjdrqUQ4";
        //get my courses, should be blank
        Response response2 = given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization", "Bearer " + token1).
                body("").
                when().
                post("/users/getCourses/andrewFakeUser");
        //will be true if in courses, should be false
        JSONObject jsonResponseUser = new JSONObject(response2.getBody().asString());
        String booleanEvaluator = "";
        try{
            booleanEvaluator += jsonResponseUser.getString("courseName");
        }
        catch(JSONException e){
            booleanEvaluator = "";
        }
        boolean inAnyCourses = booleanEvaluator.length() > 2? true : false;
        assertEquals(false, inAnyCourses);

        Response response3 = given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization", "Bearer " + token1).
                body("").
                when().
                put("/courses/joinCourseTest/aCode/andrewFakeUser");

        //check the response
        assertEquals("OK", response3.getBody().asString());
        assertNotEquals("CONFLICT",response3.getBody().asString());
        assertNotEquals("NOT_FOUND",response3.getBody().asString());

        Response response24 = given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", "Bearer " + token1)
                .when()
                .get("/users/getCourses/andrewFakeUser");
        boolean inAnyCourses2 = false;
        try {
            JSONObject jsonResponseUser2 = new JSONObject(response24.getBody().asString());
            String booleanEvaluator2 = "";
            try {
                booleanEvaluator2 += jsonResponseUser2.getString("courseName");
            } catch (JSONException e) {
                booleanEvaluator2 = "";
            }
            inAnyCourses2 = booleanEvaluator2.length() > 2 ? true : false;
        }
        catch(JSONException e){
            inAnyCourses2 = false;
        }
        assertEquals(false, inAnyCourses2);


        Response response34 = given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", "Bearer " + token1)
                .when()
                .get("/users/getInfo/andrewFakeUser");
        String body34 = response34.getBody().asString();
        boolean hasACourse34 = !body34.toString().isEmpty();
        assertNotEquals("", body34);
        assertEquals(true,hasACourse34);

        Response response4 = given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .header("Authorization", "Bearer " + token1)
                .when()
                .get("/users/getInfoString/andrewFakeUser");
        String body4 = response4.getBody().asString();
        boolean hasACourse4 = body4.toString().length() > 2 ? true : false;
        assertNotEquals("", body4);
        assertEquals(true,hasACourse4);

        Response response5 = given()
                .header("Authorization", "Bearer " + token1)
                .when()
                .get("/users/getCoursesString/andrewFakeUser");

        boolean hasCourse = !userRepo.findByNetIDIgnoreCase("andrewFakeUser").getCourses().isEmpty();
        assertEquals(true, hasCourse);

        String adminToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbmRyZXdUZXN0ZXIiLCJpYXQiOjE2ODMyNDU2MzUsImV4cCI6MTY4MzMzMjAzNX0.LARYBomjeCl7eQ3Tz_nsWbjst7a07g2K0n3un82yuug";
        Response resp1 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("Authorization", "Bearer " + adminToken).
                header("charset","utf-8").
                body("").
                when().
                put("/courses/joinCourseTest/aCode/andrewTester");

        //check the response
        assertNotEquals("CONFLICT", resp1.getBody().asString());
        assertEquals("OK",resp1.getBody().asString());
        assertNotEquals("NOT_FOUND",resp1.getBody().asString());

        Response resp2 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("Authorization", "Bearer " + adminToken).
                header("charset","utf-8").
                body("").
                when().
                delete("/courses/removeUser/aCode/andrewFakeUser/andrewTester");
        //assertEquals("OK", resp2.getBody().asString());

        //dont forget to delete the test course from the repo so this test can be run again !!
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Object> entity = new HttpEntity<>("", headers);
        String responseOf22 = restTemplate.exchange("http://localhost:8080/courses/deleteCourse/aCode/andrewTester", HttpMethod.DELETE, entity, String.class).getBody();
    }

    @Test
    public void permissionsTest() throws JSONException{
        String teach = "andrewIsATeacher";
        String teacherToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbmRyZXdJc0FUZWFjaGVyIiwiaWF0IjoxNjgzMjQ1NjY2LCJleHAiOjE2ODMzMzIwNjZ9.8wCwBaq9Z96YiJVC_0PnX_epAfbdUkXDDfHSATrBXs8";
        String s1 = "andyStudent";
        String studentToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbmR5U3R1ZGVudCIsImlhdCI6MTY4MzI0NTY4NSwiZXhwIjoxNjgzMzMyMDg1fQ.Tx_fQpvjLsvF-oeOKylpZdq5rMAb6OoRiazJwfPI4VU";
        String s2 = "aStudent22";
        String student2Token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhU3R1ZGVudDIyIiwiaWF0IjoxNjgzMjQ1NzA0LCJleHAiOjE2ODMzMzIxMDR9.pnEhiH1ljUFb2jLqo6-ck8nMLNlec6dbgbxG8yhQYiI";

        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("Authorization", "Bearer " + studentToken).
                header("charset","utf-8").with().
                body("").
                when().
                post("/courses/create/COM S 227 TESTER/georgiB/" + s1);
        //check the response
        assertEquals("UNAUTHORIZED", response.getBody().asString());
        assertNotEquals("OK", response.getBody().asString());   
        assertNotEquals("CONFLICT",response.getBody().asString());
        assertNotEquals("NOT_FOUND",response.getBody().asString());


        Response response1 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("Authorization", "Bearer " + student2Token).
                header("charset","utf-8").with().
                body("").
                when().
                post("/courses/create/COM S 228 TESTER/bGeorgi/" + s2);

        //check the response
        assertNotEquals("OK", response1.getBody().asString());
        assertEquals("UNAUTHORIZED", response1.getBody().asString());
        assertNotEquals("CONFLICT",response1.getBody().asString());
        assertNotEquals("NOT_FOUND",response1.getBody().asString());

        Response response2 = given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization", "Bearer " + teacherToken).
                body("").
                when().
                post("/users/getCourses/" + teach);
        //will be true if in courses, should be false
        JSONObject jsonResponseUser = new JSONObject(response2.getBody().asString());
        String booleanEvaluator = "";
        try{
            booleanEvaluator += jsonResponseUser.getString("courseName");
        }
        catch(JSONException e){
            booleanEvaluator = "";
        }
        boolean inAnyCourses = booleanEvaluator.length() > 2? true : false;
        assertEquals(false, inAnyCourses);

        Response response3 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("Authorization", "Bearer " + teacherToken).
                header("charset","utf-8").with().
                body("").
                when().
                post("/courses/create/COM S 227 TESTER/georgiB/" + teach);
        //check the response
        assertNotEquals("UNAUTHORIZED", response3.getBody().asString());
        assertEquals("OK", response3.getBody().asString());
        assertNotEquals("CONFLICT",response3.getBody().asString());
        assertNotEquals("NOT_FOUND",response3.getBody().asString());

        Response response4 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("Authorization", "Bearer " + studentToken).
                header("charset","utf-8").
                body("").
                when().
                put("/courses/joinCourseTest/georgiB/" + s1);

        //check the response
        assertNotEquals("CONFLICT", response4.getBody().asString());
        assertEquals("OK",response4.getBody().asString());
        assertNotEquals("NOT_FOUND",response4.getBody().asString());

        Response response5 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("Authorization", "Bearer " + student2Token).
                header("charset","utf-8").
                body("").
                when().
                put("/courses/joinCourseTest/georgiB/" + s2);

        //check the response
        assertNotEquals("CONFLICT", response5.getBody().asString());
        assertEquals("OK",response5.getBody().asString());
        assertNotEquals("NOT_FOUND",response5.getBody().asString());

        Response response6 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization", "Bearer " + teacherToken).
                body("").
                when().
                put("/courses/updatePermissions/"+s1+"/georgiB/TEACHER/" + teach);

        //check the response
        assertNotEquals("FORBIDDEN", response6.getBody().asString());
        assertEquals("OK",response6.getBody().asString());
        UUID user = userRepo.findByNetIDIgnoreCase(s1).getUserID();
        UUID course = courseRepo.findCourseByJoinCode("georgiB").getCourseID();
        String newRole = cupRepo.findCourseUserPermissionsByCourseIDAndUserID(course,user).getRole();

        //after changing role, it should now be "TEACHER"
        assertNotEquals("ADMINISTRATOR", newRole);
        assertEquals("TEACHER", newRole);
        assertNotEquals("TA", newRole);
        assertNotEquals("STUDENT", newRole);

        Response response7 = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization", "Bearer " + teacherToken).
                body("").
                when().
                delete("/courses/removeUser/georgiB/" + s1 + "/" + teach);

        //check the response, should be forbidden as i just changed role to TEACHER
        assertEquals("OK", response7.getBody().asString());
        assertNotEquals("FORBIDDEN",response7.getBody().asString());
        cupRepo.deleteAllByCourseID(courseRepo.findCourseByJoinCode("aCode").getCourseID());
        assertEquals(true, cupRepo.findCourseUserPermissionsByCourseIDAndUserID(courseRepo.findCourseByJoinCode("georgiB").getCourseID(),userRepo.findByNetIDIgnoreCase(s1).getUserID()) == null);


    }
}
