package com.taskvault.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class WorkspaceResponse {

    private Long id;
    private String name;
    private Long ownerId;
    private Instant createdAt;
}
