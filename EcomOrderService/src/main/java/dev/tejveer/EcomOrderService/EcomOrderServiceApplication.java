package dev.tejveer.EcomOrderService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class EcomOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomOrderServiceApplication.class, args);
    }

}
