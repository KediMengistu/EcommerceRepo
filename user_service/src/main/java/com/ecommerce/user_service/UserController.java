package com.ecommerce.user_service;

import com.ecommerce.user_service.UserSession.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = "ecommerce/user")
public class UserController {
    private final UserService userService;
    private final UserSessionRepository userSessionRepository;

    @Autowired
    public UserController(UserService userService, UserSessionRepository userSessionRepository) {
        this.userService = userService;
        this.userSessionRepository = userSessionRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<List<String>> signUp(@RequestBody User user) {
        return ResponseEntity.ok(userService.signUp(user));
    }

    @GetMapping("/signin")
    public ResponseEntity<Boolean> signIn(@RequestParam String username, @RequestParam String password) {
        return ResponseEntity.ok(userService.signIn(username, password));
    }

    @GetMapping("/getallusers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/id_getuser")
    public User getUserFromId(@RequestParam int id) {
        return userService.getUserFromId(id);
    }

    @GetMapping("/username_getuser")
    public User getUserFromUsername(@RequestParam String username) {
        return userService.getUserFromUserName(username);
    }
}

//    @PostMapping("/signup")
//    public ResponseEntity<List<String>> signUp(@RequestBody User user,
//                                               HttpServletResponse response,
//                                               @CookieValue(value = "session_id", required = false)
//                                               String existingSessionId) {
//        //local fields.
//        List<String> result = new ArrayList<>();
//
//        //the user trying to sign up is currently in an active session, return response stating that.
//        if (existingSessionId != null && isSessionValid(existingSessionId)) {
//            result.add("Cannot sign up while in an active session");
//        }
//        //the user trying to sign up is not in a current active session.
//        else{
//            result = userService.signUp(user);
//
//            //if result list indicates user inputs are valid, they have been
//            //stored in the user table at this point. Need to create, store, and return
//            //session for that user.
//            if(result.size()==1){
//                UserSession newSession = new UserSession();
//                newSession.setUsername(user.getUsername());
//                newSession.setCreationDate(LocalDate.now());
//                newSession.setCreationTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
//                newSession.setMaxSessionTime(LocalTime.of(2,0,0));
//
//                //End date and end time for session creation.
//                LocalDateTime creationDateTime = LocalDateTime.of(newSession.getCreationDate(), newSession.getCreationTime());
//                LocalDateTime endDateTime = creationDateTime.plusHours(2);
//
//                //Set the end date and time
//                newSession.setEndDate(endDateTime.toLocalDate());
//                newSession.setEndTime(endDateTime.toLocalTime());
//                newSession.setExpired(false);
//
//                //session is saved to the session table.
//                userSessionRepository.save(newSession);
//
//                // Create cookie with new session ID
//                Cookie sessionCookie = new Cookie("session_id", String.valueOf(newSession.getSessionId()));
//                sessionCookie.setHttpOnly(true);
//                //sessionCookie.setSecure(true); // Use with HTTPS
//                response.addCookie(sessionCookie);
//            }
//        }
//        return ResponseEntity.ok(result);
//    }
////    //helper to signup and signin mapped controller methods.
////    private boolean isSessionValid(String existingSessionId) {
////
////    }
