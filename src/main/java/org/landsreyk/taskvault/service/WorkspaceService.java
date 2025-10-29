package org.landsreyk.taskvault.service;

import lombok.RequiredArgsConstructor;
import org.landsreyk.taskvault.domain.Workspace;
import org.landsreyk.taskvault.dto.CreateWorkspaceRequest;
import org.landsreyk.taskvault.dto.WorkspaceResponse;
import org.landsreyk.taskvault.repository.WorkspaceRepository;
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
