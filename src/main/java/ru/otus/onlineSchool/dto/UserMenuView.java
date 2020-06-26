package ru.otus.onlineSchool.dto;

import java.util.Set;

public interface UserMenuView {
     Long getId();
     String getLogin();
    String getEmail();
    Set<RoleMenuView> getRoles();

}
