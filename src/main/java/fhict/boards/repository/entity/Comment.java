package fhict.boards.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@DynamicUpdate
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "created_by_member_id", nullable = false)
    private Member createdBy;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column
    private LocalDateTime lastUpdatedOn;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String body;
}
