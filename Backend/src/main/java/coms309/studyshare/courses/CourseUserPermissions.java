package coms309.studyshare.courses;

import coms309.studyshare.users.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(name = "permissions")
public class CourseUserPermissions {
    @Id
    @Column(name = "ID")
    @Getter
    @Setter
    private UUID ID;

    @Column(name = "courseID")
    @Getter
    @Setter
    private UUID courseID;

    @Column(name = "userID")
    @Getter
    @Setter
    private UUID userID;

    @Column(name = "role")
    @Getter
    @Setter
    private String role;

    public CourseUserPermissions(UUID ID, UUID courseID, UUID userID, String role) {
        this.userID = userID;
        this.courseID = courseID;
        this.ID = ID;
        this.role = role;
    }
    public CourseUserPermissions() {}

    @Override
    public String toString() {
        return String.format("courseID: %s, userID: %s, role: %s", this.getCourseID(), this.getUserID(), this.getRole());
    }
}