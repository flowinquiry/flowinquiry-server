package io.flexwork.modules.crm.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "fw_crm_call")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer duration; // Duration in minutes

    @Enumerated(EnumType.STRING)
    private CallResult callResult;

    private String phoneNumber;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum CallResult {
        SUCCESSFUL,
        UNSUCCESSFUL,
        VOICEMAIL
    }
}
