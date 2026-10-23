package com.vyshnavi.keytrace.controller;

import com.vyshnavi.keytrace.model.ApiKey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ProtectedController {

    @GetMapping("/secret-data")
    public ResponseEntity<String> getSecretData() {
        return ResponseEntity.ok("🎉 Success! You are seeing this because you provided a valid API Key.");
    }

    @PostMapping("/write-data")
    public ResponseEntity<String> writeData(@RequestAttribute("apiKey") ApiKey apiKey) {
        // Check scopes inside the controller
        if (!apiKey.getScopes().contains("write")) {
            return ResponseEntity.status(403).body("{\"error\": \"Forbidden. You do not have write scope.\"}");
        }

        return ResponseEntity.ok("🎉 Success! You have WRITE access.");
    }
}