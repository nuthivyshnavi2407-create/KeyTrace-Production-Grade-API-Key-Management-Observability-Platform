package com.vyshnavi.keytrace.controller;

import com.vyshnavi.keytrace.dto.CreateApiKeyRequest;
import com.vyshnavi.keytrace.dto.CreateApiKeyResponse;
import com.vyshnavi.keytrace.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<CreateApiKeyResponse> createKey(@RequestBody CreateApiKeyRequest request) {
        CreateApiKeyResponse response = apiKeyService.createApiKey(request);
        return ResponseEntity.ok(response);
    }

    // THIS IS THE MISSING METHOD THAT CAUSES THE 500 ERROR
    @DeleteMapping("/{id}")
    public ResponseEntity<String> revokeKey(@PathVariable UUID id) {
        apiKeyService.revokeKey(id);
        return ResponseEntity.ok("{\"message\": \"API Key revoked successfully\"}");
    }
}