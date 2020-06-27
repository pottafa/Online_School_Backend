package ru.otus.onlineSchool.dto;

public class CourseStatisticsDTO {
    private String title;
    private Long usersCount;

    public CourseStatisticsDTO(String title, Long usersCount) {
        this.title = title;
        this.usersCount = usersCount;
    }

    public String getName() {
        return title;
    }

    public void setName(String title) {
        this.title = title;
    }

    public Long getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(Long usersCount) {
        this.usersCount = usersCount;
    }
}
