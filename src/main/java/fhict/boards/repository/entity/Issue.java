package fhict.boards.repository.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import fhict.boards.domain.enums.IssuePriority;
import fhict.boards.domain.enums.IssueStatus;
import fhict.boards.domain.enums.IssueType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "issue")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "[key]", nullable = false)
    private String key;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "assignee_member_id")
    private Member assignee;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IssueType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IssuePriority priority;

    @Column(nullable = false)
    private LocalDateTime createdOn;
    private LocalDate dueOn;

    @ManyToOne
    @JoinColumn(name = "created_by_member_id")
    private Member createdBy;

    private LocalDateTime updatedOn;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdOn DESC")
    private List<Comment> comments;
}
