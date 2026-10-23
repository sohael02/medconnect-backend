package com.medconnect.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Returns the health status of the application")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "medconnect-backend",
                "timestamp", java.time.Instant.now().toString()
        );
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness check", description = "Returns whether the application is ready to serve requests")
    public Map<String, Object> ready() {
        return Map.of(
                "status", "READY",
                "service", "medconnect-backend",
                "timestamp", java.time.Instant.now().toString()
        );
    }
}