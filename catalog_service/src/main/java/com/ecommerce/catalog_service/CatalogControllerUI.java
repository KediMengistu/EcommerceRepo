package com.ecommerce.catalog_service;

import com.ecommerce.catalog_service.Client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "ecommerce/catalog/catalogUI")
public class CatalogControllerUI {

    private final UserClient userClient;

    @Autowired
    public CatalogControllerUI(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/catalogStartPage")
    public String catalogStartPage(@CookieValue(value = "session_id", required = false) String existingSessionId) {
        boolean isValidSession = userClient.sessionChecker(existingSessionId);
        if(isValidSession){
            return "catalogStartPage";
        }
        else{
            return "catalogSignOutPage";
        }
    }

    @GetMapping("/catalogSellPage")
    public String catalogSellPage(@CookieValue(value = "session_id", required = false) String existingSessionId) {
        boolean isValidSession = userClient.sessionChecker(existingSessionId);
        if(isValidSession){
            return "catalogSellPage";
        }
        else{
            return "catalogSignOutPage";
        }
    }

    @GetMapping("/catalogSearchPage")
    public String catalogSearchPage(@CookieValue(value = "session_id", required = false) String existingSessionId) {
        boolean isValidSession = userClient.sessionChecker(existingSessionId);
        if(isValidSession){
            return "catalogSearchPage";
        }
        else{
            return "catalogSignOutPage";
        }
    }

    @GetMapping("/catalogSignOutPage")
    public String catalogSignOutPage(@CookieValue(value = "session_id", required = false) String existingSessionId) {
        boolean isValidSession = userClient.sessionChecker(existingSessionId);
        if(isValidSession){
            return "catalogSignOutPage";
        }
        else{
            return "catalogSignOutPage";
        }
    }
}
