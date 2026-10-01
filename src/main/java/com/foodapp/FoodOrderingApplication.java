package com.foodapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Food Ordering System.
 * Run this class (or `mvn spring-boot:run`) to start the server on http://localhost:8080
 */
@SpringBootApplication
public class FoodOrderingApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodOrderingApplication.class, args);
    }
}
