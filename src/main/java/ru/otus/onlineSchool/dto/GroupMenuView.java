package ru.otus.onlineSchool.dto;


import java.util.List;

public interface GroupMenuView {
     Long getId();
     String getTitle();
    List<UserMenuView> getUsers();

  default Integer getUsersCount() {
       return getUsers().size();
     }

}
