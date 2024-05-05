package fhict.boards.repository;

import fhict.boards.repository.entity.Notification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>  {
    List<Notification> findAllByReceiverUserId(Long id, Sort sort);

    @Transactional
    void deleteAllByReceiverUserId(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Notification n " +
            "WHERE n.receiver.id = :memberId OR n.sender.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Notification n " +
            "WHERE n.project.id = :projectId")
    void deleteByProjectId(@Param("projectId") Long projectId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Notification n " +
            "WHERE n.issue.id = :issueId")
    void deleteByIssueId(@Param("issueId") Long issueId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Notification n " +
            "WHERE n.receiver.id " +
            "IN (SELECT m.id FROM Member m WHERE m.user.id = :userId) " +
            "OR n.sender.id IN (SELECT m.id FROM Member m WHERE m.user.id = :userId)")
    void deleteByUserId(@Param("userId") Long userId);
}
