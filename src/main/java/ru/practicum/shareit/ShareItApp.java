package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class ShareItApp {


    public static void main(String[] args) throws SQLException {
        SpringApplication.run(ShareItApp.class, args);
    }

}
