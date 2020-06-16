package ru.otus.onlineSchool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class OnlineSchoolApplication {

	public static void main(String[] args) {

		SpringApplication.run(OnlineSchoolApplication.class, args);

	}

}
