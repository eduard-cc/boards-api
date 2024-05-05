package fhict.boards.repository;

import fhict.boards.repository.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Transactional
    @Modifying
    void deleteByCreatedBy_Id(Long memberId);
}
