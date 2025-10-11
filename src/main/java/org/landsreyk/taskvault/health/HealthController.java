package org.landsreyk.taskvault.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple liveness check endpoint.
 * TODO: In later tasks, wire this with security to be publicly accessible.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * GET /api/health
     * @return 200 OK with body "OK"
     */
    @GetMapping
    public ResponseEntity<String> getHealth() {
        return ResponseEntity.ok("OK");
    }
}
