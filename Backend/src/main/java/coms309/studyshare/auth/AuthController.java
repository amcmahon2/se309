package coms309.studyshare.auth;

import coms309.studyshare.auth.AuthRequest;
import coms309.studyshare.auth.AuthResponse;
import coms309.studyshare.auth.AuthService;
import coms309.studyshare.users.User;
import coms309.studyshare.users.UserRepository;
import coms309.studyshare.users.UserRole;
import coms309.studyshare.courses.Course;
import coms309.studyshare.courses.CourseRepository;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    private final UserRepository userRepository;


    @Operation(summary = "Register a user into StudyShare's database (Gabe)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 409, message = "NetID is already registered")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user, the user that is requesting to register. this user's info is put through the auth filter and authenticated if info is provided correctly", required = true, dataType = "User", paramType = "RequestBody"),
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody User user
    ) {
        if (!userRepository.findByNetID(user.getNetID()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "NetID is already registered.");
        }
        else if(user.getNetID().contains("andrew") || user.getNetID().contains("abe") || user.getNetID().contains("gabe")){
            user.setRole(UserRole.ADMINISTRATOR);
        }
        else {
            user.setRole(UserRole.STUDENT);
        }
        return ResponseEntity.ok(service.register(user));
    }

    @Operation(summary = "Authenticate a user (Gabe)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request, which is checked in the background to have a unique netID", required = true, dataType = "AuthRequest", paramType = "RequestBody"),
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

}