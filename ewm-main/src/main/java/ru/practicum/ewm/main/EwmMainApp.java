package ru.practicum.ewm.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.ewm"})
public class EwmMainApp {

    public static void main(String[] args) {
        SpringApplication.run(EwmMainApp.class, args);
    }
}