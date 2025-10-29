package org.landsreyk.taskvault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateWorkspaceRequest {

    @NotBlank
    private String name;

    @NotNull
    private Long ownerId;
}
