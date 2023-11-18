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
        //gets optional user based on username - may or may not be empty.
        Optional<User> opUser = userRepository.findByusername(username);
        User person;

        //user with specified username is not in user table.
        if (opUser.isEmpty()) {
            return false;
        }
        //user with specified username is in user table.
        else {
            //sets user corresponding to username.
            person = opUser.get();

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

    //gets user with specific username.
    public User getUserFromUserName(String username){
        //no user with username - nothing is returned.
        if(userRepository.findByusername(username).isEmpty()){
            return null;
        }
        //user with username exists - user is returned.
        else{
            return userRepository.findByusername(username).get();
        }
    }

    //helper Method.
    private boolean peformUserChecks(User user) {
        //username checks
        String username = user.getUsername();
        Optional<User> ref = userRepository.findByusername(username);
        if (username == null || username.isEmpty() || !ref.isEmpty()) {
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