package coms309.studyshare.courses;
import coms309.studyshare.users.User;
import coms309.studyshare.users.UserRepository;
import coms309.studyshare.users.UserRole;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;
@RestController
@Api(value="Course Management System", description="Operations pertaining to courses in the Course Management System")
public class CourseController {
    @Id
    @GeneratedValue
    private UUID courseID;
    private Set<Course> courses = new HashSet<Course>();
    private Set<User> users = new HashSet<User>();
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CourseUserPermissionsRepository permissionsRepository;
    @Operation(summary = "Create a course in StudyShare (Andrew)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "UNAUTHORIZED"),
            @ApiResponse(code = 403, message = "FORBIDDEN"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Course join code, used as a path variable to assign the new course's join code param", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "Course name, used as a path variable to assign the new course's course name param", required = true, dataType = "String", paramType = "path")
    })
    @PostMapping("/courses/create/{courseName}/{joinCode}")
    public String createCourse(@PathVariable String courseName, @PathVariable String joinCode) {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return createCourseTest(u.getNetID(),courseName,joinCode);
    }
    @PostMapping("/courses/create/{courseName}/{joinCode}/{netID}")
    public String createCourseTest(@PathVariable String netID, @PathVariable String courseName, @PathVariable String joinCode){
        User u = userRepository.findByNetIDIgnoreCase(netID);
        String instructorName = u.getFirstName() + " " + u.getLastName();
        if (courseRepository.findCourseByJoinCode(joinCode) != null) {
            //code already exists
            return "FORBIDDEN";
        }
        else if(u.getRole() != UserRole.ADMINISTRATOR && u.getRole() != UserRole.TEACHER){
            return "UNAUTHORIZED";
        }
        else {
            //create the course
            this.courseID = UUID.randomUUID();
            UUID ID = UUID.randomUUID();
            permissionsRepository.save(new CourseUserPermissions(ID, this.courseID, u.getUserID(), u.getRole().toString()));
            courseRepository.save(new Course(courseName, joinCode, instructorName, courseID, users));
            Course c = courseRepository.findCourseByJoinCode(joinCode);
            c.getUsers().add(u);
            u.getCourses().add(c);
            courseRepository.save(c);
            userRepository.save(u);
            return "OK";
        }
    }
    @Operation(summary = "Join an existing course in StudyShare's database (Andrew)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 403, message = "NOT_FOUND"),
            @ApiResponse(code = 409, message = "CONFLICT"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Course join code, to be used with the course repo to add the user to that courses list of users", required = true, dataType = "String", paramType = "path")
    })
    @PutMapping("/courses/joinCourse/{joinCode}")
    public String joinCourse(@PathVariable String joinCode) {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return joinCourseTest(joinCode,u.getNetID());
    }
    @PutMapping("/courses/joinCourseTest/{joinCode}/{netID}")
    public String joinCourseTest(@PathVariable String joinCode, @PathVariable String netID) {
        User u = userRepository.findByNetIDIgnoreCase(netID);
        Course c = courseRepository.findCourseByJoinCode(joinCode);
        if (u == null || c == null) {
            //current user or course provided is null/non-existent
            return "NOT_FOUND";
        }
        else if (c.getUsers().contains(u)) {
            //user is already in course
            return "CONFLICT";
        }  else {
            //good to go!
            c.getUsers().add(u);
            u.getCourses().add(c);
            courseRepository.save(c);
            userRepository.save(u);
            UUID ID = UUID.randomUUID();
            permissionsRepository.save(new CourseUserPermissions(ID, this.courseID, u.getUserID(), u.getRole().toString()));
            return "OK";
        }
    }
    @Operation(summary = "Get all users currently in the given course (Andrew)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = JSONArray.class),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Course join code, to be used to get the current users in the course from StudyShare's repo", required = true, dataType = "String", paramType = "path")
    })
    @GetMapping("/courses/getUsers/{joinCode}")
    public JSONArray getAllInCourse(@PathVariable String joinCode){
        JSONArray userArray = new JSONArray();
        JSONObject userObject = new JSONObject();
        try {
            Set<User> userList = courseRepository.findCourseByJoinCode(joinCode).getUsers();
            Iterator<User> iterator = userList.iterator();
            while (iterator.hasNext()) {
                User user = iterator.next();
                userObject.put("firstName", user.getFirstName());
                userObject.put("lastName", user.getLastName());
                userObject.put("netID", user.getNetID());
                userObject.put("role", (permissionsRepository.findCourseUserPermissionsByCourseIDAndUserID(courseRepository.findCourseByJoinCode(joinCode).getCourseID(),user.getUserID())).getRole());
                userArray.put(userObject);
            }
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }
        return userArray;
    }
    @GetMapping("/courses/getUsersString/{joinCode}")
    public ArrayList<String> getAllInCoursePostman(@PathVariable String joinCode){
        ArrayList<String> returnerList = new ArrayList<String>();
        Set<User> userList = courseRepository.findCourseByJoinCode(joinCode).getUsers();
        Iterator<User> iterator = userList.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            count++;
            User user = iterator.next();
            returnerList.add("User #" + count
                    + "\n" + "firstName: " + user.getFirstName()
                    + "\n" + "lastName: " + user.getLastName()
                    + "\n" + "lastName: " + user.getLastName()
                    + "\n" + "role: " + (permissionsRepository.findCourseUserPermissionsByCourseIDAndUserID(courseRepository.findCourseByJoinCode(joinCode).getCourseID(),user.getUserID())).getRole()
                    + "\n");
        }
        return returnerList;

    }
    @Operation(summary = "Get the information for a course: its name, join code, name of the instructor, and list of users with their permissions for the course (Andrew)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = JSONArray.class),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Course join code, to be used to identify which course's information to return", required = true, dataType = "String", paramType = "path")
    })
    @GetMapping("/courses/getInfo/{joinCode}")
    public JSONObject getInfo(@PathVariable String joinCode){
        JSONObject courseObj = new JSONObject();
        if(courseRepository.findCourseByJoinCode(joinCode) != null) {
            Course course = courseRepository.findCourseByJoinCode(joinCode);
            try {
                courseObj.put("courseName", course.getCourseName());
                courseObj.put("joinCode", course.getJoinCode());
                courseObj.put("instructorName", course.getInstructorName());
                courseObj.put("users", getAllInCourse(joinCode));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return courseObj;
    }
    @GetMapping("/courses/getInfoString/{joinCode}")
    public String getInfoString(@PathVariable String joinCode){
        return getInfo(joinCode).toString();
    }
    @DeleteMapping("/courses/removeUser/{joinCode}/{netIDToRemove}")
    public String removeUser(@PathVariable String joinCode, @PathVariable String netIDToRemove){
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String myID = u.getNetID();
        return removeUserTest(joinCode,netIDToRemove, myID);
    }
    @Operation(summary = "Delete a user from an existing course (Andrew)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 403, message = "FORBIDDEN", response = String.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Course join code, the join code of the course, used by the findBy in courses repo", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "User netID, the netID of the user that will be removed from the course", required = true, dataType = "String", paramType = "path")
    })
    @DeleteMapping("/courses/removeUser/{joinCode}/{netIDToRemove}/{myID}")
    public String removeUserTest(@PathVariable String joinCode, @PathVariable String netIDToRemove, @PathVariable String myID) {
        // Retrieve the course, remover, and user from the repository
        Course course = courseRepository.findCourseByJoinCode(joinCode);
        User remover = userRepository.findByNetIDIgnoreCase(myID);
        User userToRemove = userRepository.findByNetIDIgnoreCase(netIDToRemove);

        // Check if any of the retrieved objects are null
        if (course == null || remover == null || userToRemove == null) {
            return "NOT_FOUND";
        }
        // Check if the remover has the required permissions to remove the user
        CourseUserPermissions removerPermissions = permissionsRepository.findCourseUserPermissionsByCourseIDAndUserID(course.getCourseID(), remover.getUserID());
        if (!"TEACHER".equals(removerPermissions.getRole()) && !"ADMINISTRATOR".equals(removerPermissions.getRole())) {
            return "FORBIDDEN";
        }
        // Remove the user from the course and the course from the user
        course.getUsers().remove(userToRemove);
        userToRemove.getCourses().remove(course);
        // Delete the user's permissions for the course
        permissionsRepository.deleteByCourseIDAndUserID(course.getCourseID(), userToRemove.getUserID());
        // Save the changes to the repository
        courseRepository.save(course);
        userRepository.save(userToRemove);
        return "OK";
    }
    @Operation(summary = "Delete a course from StudyShare's database (Andrew)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 403, message = "FORBIDDEN", response = String.class),
            @ApiResponse(code = 404, message = "NOT_FOUND", response = String.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Course join code, used to find the course to delete, and remove it from StudyShare's repo", required = true, dataType = "String", paramType = "path"),
    })
    @DeleteMapping("/courses/deleteCourse/{joinCode}")
    public String deleteCourse(@PathVariable String joinCode) {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return deleteCourseTest(joinCode,u.getNetID());
    }
    @DeleteMapping("/courses/deleteCourse/{joinCode}/{netID}")
    public String deleteCourseTest(@PathVariable String joinCode, @PathVariable String netID) {
        User u = userRepository.findByNetIDIgnoreCase(netID);
        Course course = courseRepository.findCourseByJoinCode(joinCode);
        String currentRole = permissionsRepository.findCourseUserPermissionsByCourseIDAndUserID(course.getCourseID(),u.getUserID()).getRole();
        if(!(currentRole.equals("TEACHER")) && !(currentRole.equals("ADMINISTRATOR"))){
            return "FORBIDDEN";
        }
        else if(course != null){
            Set<User> userList = course.getUsers();
            Iterator<User> iterator = userList.iterator();
            List<String> netIDList = new ArrayList<String>();
            String returner = "OK";
            while(iterator.hasNext()) {
                //remove all users from course
                User user = iterator.next();
                String message = removeUserTest(joinCode,user.getNetID(),u.getNetID());
                if(!message.equals("OK")){
                    //forbidden
                    returner = message;
                }
            }
            //remove course from both repos
            permissionsRepository.deleteAllByCourseID(course.getCourseID());
            courseRepository.delete(course);
            //makes sure code ran smoothly, tells frontend if it hasnt (forbidden)
            return returner.equals("OK") ? "OK" : "FORBIDDEN";
        }
        return "NOT_FOUND";
    }
    @Operation(summary = "Update local permissions for a given user in a given course (Andrew)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 403, message = "FORBIDDEN", response = String.class),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Course join code, used to find the course in the repo that needs its permissions to be updated", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "User netID, used to find the users permission in the course and update it at the teacher/admin's discretion", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "New role for user in course, used to update the users role in the course", required = true, dataType = "UserRole", paramType = "path")
    })
    @PutMapping("/courses/updatePermissions/{netID}/{joinCode}/{newRole}")
    public String updatePermissions(@PathVariable String netID, @PathVariable String joinCode, @PathVariable UserRole newRole){
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return updatePermissionsTest(netID,joinCode,newRole,currentUser.getNetID());
    }
    @PutMapping("/courses/updatePermissions/{netID}/{joinCode}/{newRole}/{userNetID}")
    public String updatePermissionsTest(@PathVariable String netID, @PathVariable String joinCode, @PathVariable UserRole newRole, @PathVariable String userNetID) {
        User currentUser = userRepository.findByNetIDIgnoreCase(userNetID);
        String currentRole = permissionsRepository.findCourseUserPermissionsByCourseIDAndUserID(courseRepository.findCourseByJoinCode(joinCode).getCourseID(), currentUser.getUserID()).getRole();
        if((!currentRole.equals("TEACHER")) && (!(currentRole.equals("ADMINISTRATOR")))){
            return "FORBIDDEN";
        }
        else{
            User u = userRepository.findByNetIDIgnoreCase(netID);
            Course c = courseRepository.findCourseByJoinCode(joinCode);
            UUID key = permissionsRepository.findCourseUserPermissionsByCourseIDAndUserID(c.getCourseID(),u.getUserID()).getID();
            permissionsRepository.deleteByCourseIDAndUserID(c.getCourseID(), u.getUserID());
            permissionsRepository.save(new CourseUserPermissions(key, c.getCourseID(),u.getUserID(), newRole.toString()));
            return "OK";
        }
    }

    @Operation(summary = "Get local permissions for a given user in a given course (Andrew)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "user_role_for_course"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Course join code, used to find the course the user is in", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "User netID, used to find the users permission in the course", required = true, dataType = "String", paramType = "path"),
    })
    @GetMapping("/courses/updatePermissions/{netID}/{joinCode}")
    public String getPermissions(@PathVariable String netID, @PathVariable String joinCode){
        UUID cID = courseRepository.findCourseByJoinCode(joinCode).getCourseID();
        UUID uID = userRepository.findByNetIDIgnoreCase(netID).getUserID();
        return permissionsRepository.findCourseUserPermissionsByCourseIDAndUserID(cID,uID).getRole();
    }
        @Operation(summary = "Delete all courses from the database - ADMIN ONLY (Andrew)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 403, message = "FORBIDDEN", response = String.class),
    })
    @DeleteMapping("/courses/clearAllFromDatabase")
    public String deleteAll(){
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //here we check for global role (admin)
        if(!u.getRole().equals("ADMINISTRATOR")){
            return "FORBIDDEN";
        }
        //clear both repos
        courseRepository.deleteAll();
        permissionsRepository.deleteAll();
        return "OK";
    }
}