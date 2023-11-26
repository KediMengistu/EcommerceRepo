package com.ecommerce.user_service;

import com.ecommerce.user_service.User;
import com.ecommerce.user_service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserControllerUI {

    private final UserService userService;

    @Autowired
    public UserControllerUI(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "userList"; 
        // Thymeleaf template name (userList.html)
    }

}
