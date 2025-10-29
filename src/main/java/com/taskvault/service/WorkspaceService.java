package com.taskvault.service;

import lombok.RequiredArgsConstructor;
import com.taskvault.domain.Workspace;
import com.taskvault.dto.CreateWorkspaceRequest;
import com.taskvault.dto.WorkspaceResponse;
import com.taskvault.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request) {
        Workspace workspace = Workspace.builder()
                .createdAt(Instant.now())
                .name(request.getName())
                .ownerId(request.getOwnerId())
                .build();
        workspace = workspaceRepository.save(workspace);
        return WorkspaceResponse.builder()
                .name(workspace.getName())
                .createdAt(workspace.getCreatedAt())
                .id(workspace.getId())
                .build();
    }
}
