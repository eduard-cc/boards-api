package fhict.boards.repository.entity;

import fhict.boards.domain.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@DynamicUpdate
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private NotificationType type;

    @ManyToOne
    @JoinColumn(name = "sender_member_id", nullable = false)
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "receiver_member_id", nullable = false)
    private Member receiver;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "`read`",nullable = false)
    private boolean read;
}
