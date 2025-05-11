package io.github.enessaidtatli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author etatli on 9.05.2025 14:52
 */
@SpringBootApplication(scanBasePackages = "io.github.enessaidtatli")
@EnableJpaAuditing(auditorAwareRef = "")
public class SpringChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringChatApplication.class, args);
    }
}