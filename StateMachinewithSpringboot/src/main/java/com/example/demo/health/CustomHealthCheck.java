package com.example.demo.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthCheck implements HealthIndicator {

    @Override
    public Health health() {
        // Custom health check logic
        boolean isHealthy = performHealthCheck(); 

        if (isHealthy) {
            return Health.up().withDetail("message", "Application is healthy").build();
        } else {
            return Health.down().withDetail("message", "Application is unhealthy").build();
        }
    }

    private boolean performHealthCheck() {
        return true;
    }
}
