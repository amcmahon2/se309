package coms309.studyshare.users;
import coms309.studyshare.courses.Course;
import coms309.studyshare.courses.CourseUserPermissionsRepository;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@RestController
public class UsersController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CourseUserPermissionsRepository permissionsRepository;

    @Operation(summary = "Get all information for a user: ID, netID, first & last name, global role (Andrew)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = JSONObject.class),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "User netID, used to find the specific user in StudyShare's user repo and display their info", required = true, dataType = "String", paramType = "path"),
    })
    @GetMapping("/users/getInfo/{netID}")
    public JSONObject getUserInfo(@PathVariable String netID) throws JSONException{
        JSONObject userObject = new JSONObject();
        User user = userRepository.findByNetIDIgnoreCase(netID);
        userObject.put("ID", user.getID());
        userObject.put("firstName", user.getFirstName());
        userObject.put("lastName", user.getLastName());
        userObject.put("netID", user.getNetID());
        userObject.put("password", user.getPassword());
        //this is the global role
        userObject.put("role", user.getRole());
        return userObject;
    }
    @GetMapping("/users/getInfoString/{netID}")
    public String getUserInfoString(@PathVariable String netID){
        return getUserInfo(netID).toString();
    }
    @Operation(summary = "Get all courses a given user is in, where each is a JSON object with the course's ID, join code, instructor name, and the given user's role in the course (Andrew)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = JSONArray.class),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "User netID, used to find the current user in the users repo, which then allows for access to all courses they're enrolled in", required = true, dataType = "String", paramType = "path"),
    })
    @GetMapping("/users/getCourses/{netID}")
    public JSONArray getCourses(@PathVariable String netID) throws JSONException{
        JSONArray jsonArray = new JSONArray();
        for (Course course : userRepository.findByNetIDIgnoreCase(netID).getCourses()) {
            JSONObject courseObject = new JSONObject();
            try {
                courseObject.put("courseID", course.getCourseID());
                courseObject.put("joinCode", course.getJoinCode());
                courseObject.put("instructorName", course.getInstructorName());
                courseObject.put("courseName", course.getCourseName());
                courseObject.put("role", permissionsRepository.findCourseUserPermissionsByCourseIDAndUserID(course.getCourseID(),userRepository.findByNetIDIgnoreCase(netID).getUserID()).getRole());
                jsonArray.put(courseObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
    @GetMapping("/courses/getCoursesString/{netID}")
    public ArrayList<String> getCoursesPostman(@PathVariable String netID){
        ArrayList<String> returnerList = new ArrayList<String>();
        Set<Course> courseList = userRepository.findByNetIDIgnoreCase(netID).getCourses();
        Iterator<Course> iterator = courseList.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            Course course = iterator.next();
            returnerList.add("Course #" + count
                    + "\n" + "courseID: " + course.getCourseID()
                    + "\n" + "joinCode: " + course.getJoinCode()
                    + "\n" + "instructorName: " + course.getInstructorName()
                    + "\n" + "courseName: " + course.getCourseName()
                    + "\n" + "my role: " + (permissionsRepository.findCourseUserPermissionsByCourseIDAndUserID(course.getCourseID(),userRepository.findByNetIDIgnoreCase(netID).getUserID()).getRole())
                    + "\n");
        }
        return returnerList;
    }
    @Operation(summary = "Delete a user from StudyShare (Gabe)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "User id, used to delete a user from both the user and permissions repo", required = true, dataType = "UUID", paramType = "path"),
    })
    @GetMapping("/users/delete/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userRepository.deleteByID(id);
        permissionsRepository.deleteAllByUserID(id);
    }
    @Operation(summary = "Get the current user (Gabe)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = User.class),
    })
    @GetMapping("/me")
    public User getMe() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
