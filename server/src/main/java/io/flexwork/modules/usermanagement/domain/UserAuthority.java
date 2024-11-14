package io.flexwork.modules.usermanagement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fw_user_authority")
@IdClass(UserAuthorityId.class) // Use a composite key
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthority implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "authority_name", nullable = false)
    private Authority authority;
}
