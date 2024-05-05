package fhict.boards.repository;

import fhict.boards.repository.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUserId(Long userId);
    Optional<Member> findByIdAndProjectId(Long id, Long projectId);
    Optional<Member> findByUserIdAndProjectId(Long userId, Long projectId);
    Long countByProjectId(Long projectId);
    @Transactional
    @Modifying
    @Query("DELETE FROM Member m WHERE m.user.id = :id")
    void deleteAllByUserId(@Param("id") Long id);
}
