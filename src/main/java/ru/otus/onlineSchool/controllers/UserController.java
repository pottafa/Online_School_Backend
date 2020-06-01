package ru.otus.onlineSchool.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import ru.otus.onlineSchool.dataBase.entity.User;
import ru.otus.onlineSchool.dataBase.service.UserService;

@Controller
public class UserController {
    private static final String ADMIN_PANEL_TEMPLATE = "admin-panel";
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin-panel")
    protected String allUsersView(Model model) {
        return ADMIN_PANEL_TEMPLATE;
    }

}
