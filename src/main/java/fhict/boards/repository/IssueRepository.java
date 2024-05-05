package fhict.boards.repository;

import fhict.boards.repository.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    @Query(value = "SELECT MAX(CAST(REGEXP_SUBSTR(i.key, '(\\\\d+)$', 1) AS signed)) " +
            "FROM issue i " +
            "WHERE i.project_id = :projectId", nativeQuery = true)
    Optional<Long> findLatestIssueNumberByProjectId(@Param("projectId") Long projectId);

    @Transactional
    @Modifying
    @Query("UPDATE Issue i SET i.assignee.id = NULL, i.createdBy.id = NULL " +
            "WHERE i.assignee.id = :memberId OR i.createdBy.id = :memberId")
    void setAssigneeAndCreatedByToNull(@Param("memberId") Long memberId);
}
