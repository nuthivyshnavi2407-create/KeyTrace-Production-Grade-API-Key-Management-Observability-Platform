package com.vyshnavi.keytrace.config;

import com.vyshnavi.keytrace.model.User;
import com.vyshnavi.keytrace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Profile("!prod") // This tells Spring to ONLY run this locally, not on Render
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            UUID testId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            if (userRepository.findById(testId).isEmpty()) {
                User user = User.builder()
                        .id(testId)
                        .email("test@example.com")
                        .name("Test User")
                        .build();
                userRepository.save(user);
                System.out.println("✅ Test user created automatically via JPA!");
            } else {
                System.out.println("✅ Test user already exists!");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not create test user: " + e.getMessage());
        }
    }
}