package com.ecommerce.user_service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "ecommerce/user/userUI")
public class UserControllerUI {

    @GetMapping("/userStartPage")
    public String userStartPage() {
        return "userStartPage";
    }

    @GetMapping("/userSignUpPage")
    public String userSignUpPage() {
        return "userSignUpPage";
    }

    @GetMapping("/userSignInPage")
    public String userSignInPage() {
        return "userSignInPage";
    }
}
