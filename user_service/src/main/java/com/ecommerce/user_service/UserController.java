package com.ecommerce.user_service;

import com.ecommerce.user_service.UserSession.UserSession;
import com.ecommerce.user_service.UserSession.UserSessionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<List<String>> signUp(@RequestBody User user, HttpServletResponse response,
                                               @CookieValue(value = "session_id", required = false) String existingSessionId) {
        //local fields.
        List<String> result = new ArrayList<>();
        Optional<UserSession> opUserSession;
        UserSession session;
        Integer sessionId;
        LocalDateTime sessionEndDateTime;
        LocalDateTime now;

        //need to check if the person trying to sign up, doesnt have a session; they were never signed up as a user.
        if(existingSessionId==null){
            //need to check if sign up is successful.
            result = userService.signUp(user);

            //successful sign up - requires session and cookie to be made for the new user.
            if(result.size()==1){
                //need to create a session and cookie for the new user.
                createAndStoreUserSession(user.getUsername(), response);
            }
            //unsuccessful sign up - no session and notice is presented.
        }
        //there is a session stored by the cookie; regardless if session expires, user is associated with session so they were already
        //signed up.
        else{
            //check to see if the session stored in the cookie is still valid or not - valid = non-expired, invalid = expired session.
            sessionId = Integer.valueOf(existingSessionId);
            opUserSession = userSessionRepository.findById(sessionId);

            //pre-check = checking to see if the session corresponding to the sessionId doesn't exist.
            //this pre-check should never evaluate to true in the case that a cookie has a session id.
            //always delete a session when the cookie expires; never before so a session should always be existing and mapped to
            //a cookie even in the case that it has expired.
            if(opUserSession.isEmpty()){
                //delete cookie corresponding to the cookie.
                Cookie cookie = new Cookie("session_id", existingSessionId); // Same name as the original cookie
                cookie.setPath("/"); // Set path to root to increase the chance of matching
                cookie.setMaxAge(0); // Expire immediately
                response.addCookie(cookie);
                result.add("Unsuccessful Sign Up - Session Error");
                result.add("Active user browser session expired - Sign Out");
            }
            //session exists corresponding to the session id stored by the cookie.
            else{
                session = opUserSession.get();
                //check to see if the session is expired; if it is then delete.
                sessionEndDateTime = LocalDateTime.of(session.getEndDate(), session.getEndTime());
                now = LocalDateTime.now();
                if (now.isEqual(sessionEndDateTime) || now.isAfter(sessionEndDateTime)) {
                    //delete the session.
                    userSessionRepository.delete(session);

                    //create deletion cookie.
                    Cookie cookie = new Cookie("session_id", existingSessionId); // Same name as the original cookie
                    cookie.setPath("/"); // Set path to root to increase the chance of matching
                    cookie.setMaxAge(0); // Expire immediately
                    response.addCookie(cookie);
                    result.add("Unsuccessful Sign Up - Session Error");
                    result.add("Active user browser session expired - Sign Out");
                }
                //the session is still active - do not delete the session or the cookie corresponding to the session id stored in the cookie.
                else{
                    result.add("Unsuccessful Sign Up - Session Error");
                    result.add("Active user browser session - Sign Out");
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/signin")
    public ResponseEntity<List<Boolean>> signIn(@RequestParam String username, @RequestParam String password,
                                          HttpServletResponse response, @CookieValue(value = "session_id", required = false)
                                          String existingSessionId) {
        //local fields.
        List<Boolean> result = new ArrayList<>();
        Boolean output;
        Optional<UserSession> opUserSession;
        UserSession session;
        Integer sessionId;
        LocalDateTime sessionEndDateTime;
        LocalDateTime now;

        //session doesnt exist when trying to sign in; possibly a signed out user trying to get back into system.
        if(existingSessionId==null){
            //sign in the user.
            output = userService.signIn(username, password);

            //saving the output to the list of boolean; list is of size 1 and is true, so can successfully sign in.
            result.add(output);

            //user already exists and doesnt have a session in the browser - can sign them in and create session and cookie.
            if(output==true){
                createAndStoreUserSession(username, response);
            }
            //unsuccessful sign in for user - this means the user credentials supplied are not correct.
            //simply return the list containing the false value - size 1 and false means try again.
        }
        //session exists; this means that the user is already signed in.
        else {
            //getting the session corresponding to the session id in the cookie.
            sessionId = Integer.valueOf(existingSessionId);
            opUserSession = userSessionRepository.findById(sessionId);

            //check to see if the session mapped to the session id in the cookie actually exists.
            if (opUserSession.isEmpty()) {
                //delete cookie corresponding to the cookie.
                Cookie cookie = new Cookie("session_id", existingSessionId); // Same name as the original cookie
                cookie.setPath("/"); // Set path to root to increase the chance of matching
                cookie.setMaxAge(0); // Expire immediately
                response.addCookie(cookie);
                result.add(false);
                result.add(false);
            }
            //session does exist - need to check for expired or not.
            else {
                session = opUserSession.get();
                //check to see if the session is expired; if it is then delete.
                sessionEndDateTime = LocalDateTime.of(session.getEndDate(), session.getEndTime());
                now = LocalDateTime.now();
                if (now.isEqual(sessionEndDateTime) || now.isAfter(sessionEndDateTime)) {
                    //delete the session.
                    userSessionRepository.delete(session);

                    //create deletion cookie.
                    Cookie cookie = new Cookie("session_id", existingSessionId); // Same name as the original cookie
                    cookie.setPath("/"); // Set path to root to increase the chance of matching
                    cookie.setMaxAge(0); // Expire immediately
                    response.addCookie(cookie);
                    result.add(false);
                    result.add(false);
                }
                //the session is still active - do not delete the session or the cookie corresponding to the session id stored in the cookie.
                else {
                    result.add(false);
                    result.add(false);
                    result.add(false);
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/signout")
    public ResponseEntity<Void> signOut(HttpServletResponse response, @CookieValue(value = "session_id", required = false) String existingSessionId) {
        //checking to see if the cookie has a session.
        //if it does then delete the session and the cookie, regardless of the session is inactive or active.
        if (existingSessionId != null) {
            //find the session that corresponds to the session id stored in the cookie.
            Integer sessionId = Integer.valueOf(existingSessionId);
            Optional<UserSession> opUserSession = userSessionRepository.findById(sessionId);

            //there is no session stored in the db that corresponds to the session_id stored in the cookie.
            //just delete the cookie.
            if(opUserSession.isEmpty()){
                //deleting the cookie.
                Cookie cookie = new Cookie("session_id", existingSessionId); // Same name as the original cookie
                cookie.setPath("/"); // Set path to root to increase the chance of matching
                cookie.setMaxAge(0); // Expire immediately
                response.addCookie(cookie);
            }
            //session_id corresponds to session in session table.
            //delete the cookie and the session.
            else{
                //session placeholder.
                UserSession session = opUserSession.get();

                //deleting the cookie.
                Cookie cookie = new Cookie("session_id", existingSessionId); // Same name as the original cookie
                cookie.setPath("/"); // Set path to root to increase the chance of matching
                cookie.setMaxAge(0); // Expire immediately
                response.addCookie(cookie);

                //delete the session.
                userSessionRepository.delete(session);
            }
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/setInAuction")
    public void setInAuction(@RequestParam String username, @RequestParam int auctionid){
       userService.setUserInAuction(username, auctionid);
    }

    @PutMapping("/setOutAuction")
    public void setOutOfAuction(@RequestParam String username){
        userService.setUserOutOfAuction(username);
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

    @GetMapping("/sessionid_getuser")
    public User getUserFromUsername(@RequestParam int sessionid) {
       return userService.findUserFromSession(sessionid);
    }
    @GetMapping("/userfromsession")
    public ResponseEntity<User> usernfromsession(HttpServletResponse response,
                                                 @CookieValue(value = "session_id", required = false) String existingSessionId){
        if(existingSessionId!=null){
            Integer sessionid = Integer.valueOf(existingSessionId);
            Optional<UserSession> usersession = userSessionRepository.findById(sessionid);
            if(usersession.isEmpty()==false){
                User user = userService.getUserFromUserName(usersession.get().getUsername());
                if(user!=null){
                    return ResponseEntity.ok(user);
                }
            }
        }
        return ResponseEntity.ok(null);
    }

    //helper method to create session and store in session db and create cookie.
    private void createAndStoreUserSession(String username, HttpServletResponse response) {
        UserSession newSession = new UserSession();
        newSession.setUsername(username);
        newSession.setCreationDate(LocalDate.now());
        newSession.setCreationTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        newSession.setMaxSessionTime(LocalTime.of(0, 12, 0));

        LocalDateTime creationDateTime = LocalDateTime.of(newSession.getCreationDate(), newSession.getCreationTime());
        LocalDateTime endDateTime = creationDateTime.plusHours(0).plusMinutes(12).plusSeconds(0).truncatedTo(ChronoUnit.SECONDS);

        newSession.setEndDate(endDateTime.toLocalDate());
        newSession.setEndTime(endDateTime.toLocalTime().truncatedTo(ChronoUnit.SECONDS));

        userSessionRepository.save(newSession);

        Cookie sessionCookie = new Cookie("session_id", String.valueOf(newSession.getSessionId()));
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");
        // sessionCookie.setSecure(true); // Use with HTTPS
        response.addCookie(sessionCookie);
    }

    @GetMapping("/sessionChecker")
    boolean sessionChecker(@RequestParam String existingSessionId){
        //local fields.
        Optional<UserSession> opUserSession;
        UserSession session;
        Integer sessionId;
        LocalDateTime sessionEndDateTime;
        LocalDateTime now;
        //cookie with empty session id - so invalid and needs to be deleted.
        if(existingSessionId==null){
            return false;
        }
        //cookie contains session id - need to perform more checks for validity.
        else{
            //extracting session.
            sessionId = Integer.valueOf(existingSessionId);
            opUserSession = userSessionRepository.findById(sessionId);
            //session does not exist corresponding to the session id stored in cookie - should never be the case, but still check.
            if(opUserSession.isEmpty()){
                return false;
            }
            //session exists for the session id stored in the cookie.
            else{
                //setting the session placeholder.
                session = opUserSession.get();
                //need to check to see if the session is not expired.
                sessionEndDateTime = LocalDateTime.of(session.getEndDate(), session.getEndTime());
                now = LocalDateTime.now();
                if (now.isEqual(sessionEndDateTime) || now.isAfter(sessionEndDateTime)) {
                    return false;
                }
                //session has not expired.
                else{
                    return true;
                }
            }
        }
    }

    @GetMapping("/cookieName")
    public String getCookieName(HttpServletResponse response,
                                @CookieValue(value = "session_id", required = false) String existingSessionId){
        return existingSessionId;
    }
}