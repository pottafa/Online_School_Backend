package ru.otus.onlineSchool.controllers.rest.message;

import java.io.Serializable;

public class ApiError implements Serializable {
    private int status = 500;
    private String message;

    public ApiError(String message) {
    this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
