package com.taskvault.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.taskvault.dto.CreateWorkspaceRequest;
import com.taskvault.dto.WorkspaceResponse;
import com.taskvault.service.WorkspaceService;
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
    public WorkspaceResponse createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
        return workspaceService.createWorkspace(request);
    }

    @GetMapping("/api/v1/health")
    public Map<String, String> healthCheck() {
        return Map.of("status", "UP");
    }
}
