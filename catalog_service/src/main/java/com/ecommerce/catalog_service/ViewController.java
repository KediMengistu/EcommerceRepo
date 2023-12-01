package com.ecommerce.catalog_service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ViewController {
    @GetMapping("/catalogg")
    public String catalog() {
        return "catalog";
    }
    }

