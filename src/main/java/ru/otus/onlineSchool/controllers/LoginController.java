package ru.otus.onlineSchool.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

  private static final String LOGIN_PAGE_TEMPLATE = "login";

  @GetMapping("/login")
  protected String authorizationView(Model model) {
    return LOGIN_PAGE_TEMPLATE;
  }

}
