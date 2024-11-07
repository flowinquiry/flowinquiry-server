package io.flexwork.modules.crm.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactDTO {

    private Long id;

    private Long accountId;

    private String accountName;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String address;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private String position;

    private String notes;

    private String status;

    private String leadSource;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
