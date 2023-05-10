package coms309.studyshare.courses;
import jakarta.persistence.*;
import coms309.studyshare.users.User;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.*;
import java.util.UUID;


@Entity
@Table(name = "course")
public class Course {

    /*
     * this is the course identifier for the CourseUser table
     */
    @Id
    @Column(name = "courseID")
    private UUID courseID;

    private String joinCode;

    @ManyToMany(mappedBy = "courses", cascade = CascadeType.REMOVE)
    private Set<User> users = new HashSet<User>();

    private String instructorName;

    private String courseName;


    /*
     * Blank constructor for Course
     */
    public Course(){}

    public Course(String courseName, String joinCode, String instructorName, UUID courseID, Set<User> userList) {
        this.courseName = courseName;
        this.joinCode = joinCode;
        this.instructorName = instructorName;
        this.courseID = courseID;
        this.setUsers(userList);
    }

    public String getInstructorName() {
        return this.instructorName;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public String getJoinCode() {
        return this.joinCode;
    }

    public Set<User> getUsers(){
        return this.users;
    }

    public UUID getCourseID() {
        return courseID;
    }

    public void setCourseID(UUID courseID) {
        this.courseID = courseID;
    }

    public void setInstructorName(String name) {
        this.instructorName = name;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public void setUsers(Set<User> users){
        this.users = users;
    }

    public void wipeUsers(Set<User> users){this.users.clear();}

    @Override
    public String toString() {
        return String.format("joinCode: %s, Name: %s, Instructor: %s", this.getJoinCode(), this.getCourseName(), this.getInstructorName());
    }


}