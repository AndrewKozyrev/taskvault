package org.landsreyk.taskvault.repository;

import org.landsreyk.taskvault.domain.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
}
