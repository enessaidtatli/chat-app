package io.github.enessaidtatli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author etatli on 9.05.2025 14:52
 */
@SpringBootApplication(scanBasePackages = "io.github.enessaidtatli")
public class SpringChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringChatApplication.class, args);
    }
}