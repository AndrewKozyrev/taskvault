package org.landsreyk.taskvault.controller;

import lombok.RequiredArgsConstructor;
import org.landsreyk.taskvault.dto.CreateWorkspaceRequest;
import org.landsreyk.taskvault.dto.WorkspaceResponse;
import org.landsreyk.taskvault.service.WorkspaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping("/api/v1/workspaces")
    public WorkspaceResponse createWorkspace(@RequestBody CreateWorkspaceRequest request) {
        return workspaceService.createWorkspace(request);
    }

    @GetMapping("/api/v1/health")
    public Map<String, String> healthCheck() {
        return Map.of("status", "UP");
    }
}
