package com.vyshnavi.keytrace.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);

            if (count != null && count == 0) {
                jdbcTemplate.update(
                        "INSERT INTO users (id, email, name) VALUES (?::uuid, ?, ?)",
                        "11111111-1111-1111-1111-111111111111",
                        "test@example.com",
                        "Test User"
                );
                System.out.println("✅ Test user created automatically!");
            } else {
                System.out.println("✅ Test user already exists!");
            }
        } catch (Exception e) {
            // Catch the exception so the app doesn't crash on startup
            System.out.println("⚠️ Could not create test user: " + e.getMessage());
        }
    }
}