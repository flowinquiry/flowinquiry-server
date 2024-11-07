package io.flexwork.modules.crm.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDTO {

    private Long id;

    private String name;

    private String type;

    private String industry;

    private String website;

    private String phoneNumber;

    private String email;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private String annualRevenue;

    private Long parentAccountId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String status;

    private Long assignedToUserId;

    private String notes;
}
