package io.flowinquiry.modules.usermanagement.service.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class UserHierarchyDTO {
    private Long id;

    private String name;

    private String imageUrl;

    private Long managerId;

    private String managerName;

    private String managerImageUrl;

    @ToString.Exclude private List<UserHierarchyDTO> subordinates;

    public UserHierarchyDTO(
            Long id,
            String name,
            String imageUrl,
            Long managerId,
            String managerName,
            String managerImageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.managerId = managerId;
        this.managerName = managerName;
        this.managerImageUrl = managerImageUrl;
    }
}
