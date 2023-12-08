package com.ecommerce.user_service;

import com.ecommerce.user_service.UserSession.UserSession;
import com.ecommerce.user_service.UserSession.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserSessionRepository userSessionRepository) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
    }

    //signing up.
    public List<String> signUp(User user) {
        //creating local variables.
        List<String> result = new ArrayList<>();

        //has the check information
        result = peformUserChecks(user);

        //confirms all other checks passed.
        if (result.isEmpty()) {
            userRepository.save(user);
            result.add("Successful Sign Up");
            return result;
        }
        //confirms not all checks passed.
        else {
            result.add(0, "Unsuccessful Sign Up");
            return result;
        }
    }

    //signing in.
    public boolean signIn(String username, String userpassword) {
        User person = getUserFromUserName(username);
        //user does not exist.
        if (person == null) {
            return false;
        }
        //user does exist.
        else {
            //confirms if user password is valid and matches the password of the user with the specified username.
            if (person.getUserpassword() != null && !person.getUserpassword().isEmpty() &&
                    person.getUserpassword().equals(userpassword)) {
                return true;
            }
            //password does not match.
            else {
                return false;
            }
        }
    }

    //gets all users in user table.
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //gets user with specific id.
    public User getUserFromId(int id) {
        //no user with id - nothing is returned
        if (userRepository.findById(id).isEmpty()) {
            return null;
        }
        //user with id exists - user is returned.
        else {
            return userRepository.findById(id).get();
        }
    }

    //gets user with specific username - keep the casing in mind for JPA.
    //this is taken care of by iterating through entire user table.
    public User getUserFromUserName(String username) {
        //get all users
        List<User> userlist = userRepository.findAll();
        //no users.
        if (userlist.isEmpty()) {
            return null;
        }
        //users exist.
        else {
            //iterate through and find user with specific username - must match casing.
            for (User result : userlist) {
                String name = result.getUsername();
                //the user was found.
                if (name.equals(username)) {
                    return result;
                }
            }
            return null;
        }
    }

    //finding the user from there session
    public User findUserFromSession(int sessionid) {
        if(userSessionRepository.findById(sessionid).isEmpty()){
            return null;
        }
        else{
            UserSession userSession = userSessionRepository.findById(sessionid).get();
            //extracting the username of the user corresponding to this session.
            String username = userSession.getUsername();
            List<User> userList = userRepository.findAll();
            for(User u: userList){
                if(u!=null && u.getUsername()!=null && u.getUsername().equals(username)){
                    return u;
                }
            }
            return null;
        }
    }

    public void setUserInAuction(String username, int auctionid) {
        List<User> userList = userRepository.findAll();
        for(int i=0; i<userList.size(); i++){
            if(userList.get(i)!=null && userList.get(i).getUsername().equals(username)){
                if(userList.get(i).getAuctionid()==0){
                    userList.get(i).setAuctionid(auctionid);
                    userRepository.save(userList.get(i));
                }
            }
        }
    }

    public void setUserOutOfAuction(String username) {
        List<User> userList = userRepository.findAll();
        for(int i=0; i<userList.size(); i++){
            if(userList.get(i)!=null && userList.get(i).getUsername().equals(username)){
                if(userList.get(i).getAuctionid()!=0){
                    userList.get(i).setAuctionid(0);
                    userRepository.save(userList.get(i));
                }
            }
        }
    }

    //helper Method.
    private List<String> peformUserChecks(User user) {
        //local fields
        List<String> result = new ArrayList<>();
        //username checks
        String username = user.getUsername();
        User ref = getUserFromUserName(username);
        if (username == null || username.isEmpty() || ref != null) {
            result.add("Username not specified or already exists.");
        }

        String password = user.getUserpassword();
        if(password == null || password.isEmpty()){
            result.add("Password not specified");
        }

        //firstname check
        String firstname = user.getFirstname();
        if (firstname == null || firstname.isEmpty()) {
            result.add("First name not specified.");
        }

        //lastname check
        String lastname = user.getLastname();
        if (lastname == null || lastname.isEmpty()) {
            result.add("Last name not specified.");
        }

        //streetname check
        String streetname = user.getStreetname();
        if (streetname == null || streetname.isEmpty()) {
            result.add("Street name not specified.");
        }

        //streetnumber check
        int streetnumber = user.getStreetnumber();
        if (streetnumber < 0) {
            result.add("Street number cannot be negative.");
        }

        //city check
        String city = user.getCity();
        if (city == null || city.isEmpty()) {
            result.add("City not specified.");
        }

        //country check
        String country = user.getCountry();
        if (country == null || country.isEmpty()) {
            result.add("Country not specified.");
        }

        //postalcode check
        String postalcode = user.getPostalcode();
        if (postalcode == null || postalcode.isEmpty()) {
            result.add("Postal code not specified.");
        }
        return result;
    }
}