package fhict.boards.repository;

import fhict.boards.repository.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p JOIN p.members m WHERE (p.name = :name OR p.key = :key) AND m.user.id = :userId AND p.id <> :projectId")
    Optional<Project> findFirstByNameOrKeyAndMembers_User_IdAndIdNot(@Param("name") String projectName, @Param("key") String projectKey, @Param("userId") Long userId, @Param("projectId") Long projectId);

    @Query("SELECT p FROM Project p JOIN p.members m WHERE (p.name = :name OR p.key = :key) AND m.user.id = :userId")
    Optional<Project> findFirstByNameOrKeyAndMembers_User_Id(@Param("name") String projectName, @Param("key") String projectKey, @Param("userId") Long userId);
}