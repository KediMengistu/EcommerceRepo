package com.ecommerce.user_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //signing up.
    public boolean signUp(User user) {
        //creating local variables.
        boolean isValidUser;

        //opUser may or may not contain user with the id of the user object we are trying to store.
        Optional<User> opUser = userRepository.findById(user.getUserid());

        //no such user with id - sign up can be performed.
        if (opUser.isEmpty()) {
            isValidUser = peformUserChecks(user);
            //confirms all other checks passed.
            if (isValidUser == true) {
                userRepository.save(user);
                return true;
            }
            //confirms not all checks passed.
            else {
                return false;
            }
        }
        //pre-existing user with id - sign up cannot be performed.
        else {
            return false;
        }
    }

    //signing in.
    public boolean signIn(String username, String userpassword) {
        User person = getUserFromUserName(username);
        //user does not exist.
        if(person==null){
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
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    //gets user with specific id.
    public User getUserFromId(int id) {
        //no user with id - nothing is returned
        if(userRepository.findById(id).isEmpty()){
            return null;
        }
        //user with id exists - user is returned.
        else{
            return userRepository.findById(id).get();
        }
    }

    //gets user with specific username - keep the casing in mind for JPA.
    //this is taken care of by iterating through entire user table.
    public User getUserFromUserName(String username){
        //get all users
        List<User> userlist = userRepository.findAll();
        //no users.
        if(userlist.isEmpty()){
            return null;
        }
        //users exist.
        else{
            //iterate through and find user with specific username - must match casing.
            for(User result: userlist){
                String name = result.getUsername();
                //the user was found.
                if(name.equals(username)){
                    return result;
                }
            }
            return null;
        }
    }

    //helper Method.
    private boolean peformUserChecks(User user) {
        //username checks
        String username = user.getUsername();
        User ref = getUserFromUserName(username);
        if (username == null || username.isEmpty() || ref!=null) {
            return false;
        }

        //firstname check
        String firstname = user.getFirstname();
        if (firstname == null || firstname.isEmpty()) {
            return false;
        }

        //lastname check
        String lastname = user.getLastname();
        if (lastname == null || lastname.isEmpty()) {
            return false;
        }

        //streetname check
        String streetname = user.getStreetname();
        if (streetname == null || streetname.isEmpty()) {
            return false;
        }

        //streetnumber check
        int streetnumber = user.getStreetnumber();
        if (streetnumber < 0) {
            return false;
        }

        //city check
        String city = user.getCity();
        if (city == null || city.isEmpty()) {
            return false;
        }

        //country check
        String country = user.getCountry();
        if (country == null || country.isEmpty()) {
            return false;
        }

        //postalcode check
        String postalcode = user.getPostalcode();
        if (postalcode == null || postalcode.isEmpty()) {
            return false;
        }

        return true;
    }
}