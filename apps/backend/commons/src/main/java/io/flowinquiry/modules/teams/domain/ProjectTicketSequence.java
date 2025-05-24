package io.flowinquiry.modules.teams.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "fw_project_ticket_seq")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTicketSequence {

    @Id private Long projectId;

    @Column(name = "last_ticket_number", nullable = false)
    private Long lastTicketNumber;

    @Version private Long version;
}
