package coms309.studyshare.users;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import coms309.studyshare.courses.Course;


import java.util.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {
    /**
     * this is the primary key for the CourseUser table, as well
     * as the unique identifier for a user
     */
    @Id
    @Column(name = "userID")
    @GeneratedValue
    private UUID userID;

    private String netID;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "course_user",
            joinColumns = @JoinColumn(name = "userID"),
            inverseJoinColumns = @JoinColumn(name = "courseID"))
    private Set<Course> courses = new HashSet<Course>();

    private String firstName;
    private String lastName;

    private String password;

    /*
     * this is a GLOBAL role, but can be changed in each course as desired
     */
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public static JSONArray usersToJSON(Set<User> users) {
        JSONArray jsonArray = new JSONArray();
        for (User user : users) {
            JSONObject userObject = new JSONObject();
            try {
                userObject.put("ID", user.getID());
                userObject.put("firstName", user.getFirstName());
                userObject.put("lastName", user.getLastName());
                userObject.put("netID", user.getNetID());
                userObject.put("password", user.getPassword());
                userObject.put("role", user.getRole());
                jsonArray.put(userObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    public String getFirstName() {return this.firstName;}

    public void setFirstName(String name) {this.firstName = name;}

    public String getLastName() {return this.lastName;}

    public void setLastName(String name) {this.lastName = name;}

    public String getNetID() {return this.netID;}

    public void setNetID(String netID) {this.netID = netID;}

    public UserRole getRole() {return this.role;}

    public Set<Course> getCourses() {
        return this.courses;
    }

    public void setCourses(Set<Course> courses){
        this.courses = courses;
    }

    public UUID getID() {
        return userID;
    }

    public void setID(UUID userID) {
        this.userID = userID;
    }

    public void setRole(UserRole role) {this.role = role;}

    public void setPassword(String password) {this.password = password;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return  List.of(new SimpleGrantedAuthority(getRole().name()));
    }

    @Override
    public String getPassword() {
        return  password;
    }

    @Override
    public String getUsername() {
        return  netID;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
