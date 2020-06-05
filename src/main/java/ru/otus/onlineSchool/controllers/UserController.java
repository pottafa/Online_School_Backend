package ru.otus.onlineSchool.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.onlineSchool.dataBase.service.UserService;

@Controller
public class UserController {
    private static final String USER_PANEL_PAGE = "admin-panel";
    private static final String REGISTRATION_PAGE = "registration";
    @Autowired
    private UserService userService;

    @GetMapping("/admin-panel")
    protected String userPanelView(Model model) {
        return USER_PANEL_PAGE;
    }

    @GetMapping("/registration")
    protected String registrationPageView(Model model) {
        return REGISTRATION_PAGE;
    }

}
