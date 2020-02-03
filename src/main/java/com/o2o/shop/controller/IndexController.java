package com.o2o.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/frontend")
public class IndexController {

    @RequestMapping("/index")
    public String index(){

        return "/frontend/index";
    }
}
