package com.jang.gitminiprojectthejoeun.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/","/main","/index"})
    public String index(Model model){
        return "index/index";
    }
}
