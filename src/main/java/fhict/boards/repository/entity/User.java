package fhict.boards.repository.entity;

import fhict.boards.domain.enums.AccessRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "[user]")
@DynamicUpdate
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String company;
    private String location;

    @Column(nullable = false)
    private String password;

    @Lob
    @Column
    private byte[] picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessRole accessRole;
}
