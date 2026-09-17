package com.bootforge.hbp.bookingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookingServiceApplication {
    static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}
