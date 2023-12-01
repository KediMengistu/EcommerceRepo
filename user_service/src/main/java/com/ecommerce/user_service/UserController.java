package com.ecommerce.user_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "ecommerce/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/signup")
    public boolean signUp(@RequestBody User user) {
        return userService.signUp(user);
    }

    @GetMapping("/signin")
    public boolean signIn(@RequestParam String username, @RequestParam String password){
        return userService.signIn(username, password);
    }

    @GetMapping("/getallusers")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

@GetMapping("/getallusers")
public String getAllUsers(Model model){
    List<User> users = userService.getAllUsers();
    model.addAttribute("users", users);
    return "userList"; 
    // Thymeleaf template name (userList.html)
}

// ...


    @GetMapping("/id_getuser")
    public User getUserFromId(@RequestParam int id){
        return userService.getUserFromId(id);
    }

    @GetMapping("/username_getuser")
    public User getUserFromUsername(@RequestParam String username){
        return userService.getUserFromUserName(username);
    }

}
