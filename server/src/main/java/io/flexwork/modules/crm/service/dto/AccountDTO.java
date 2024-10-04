package io.flexwork.modules.crm.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDTO {

    private Long id; // Unique identifier for the account

    private String name; // Name of the account

    private String type; // Type of the account (e.g., customer, vendor)

    private String industry; // Industry in which the account operates

    private String website; // Website URL for the account

    private String phoneNumber; // Contact phone number

    private String email; // Email address

    private String addressLine1; // First line of address

    private String addressLine2; // Second line of address (optional)

    private String city; // City where the account is located

    private String state; // State or region of the account

    private String postalCode; // Postal code

    private String country; // Country of the account

    private String annualRevenue; // Annual revenue of the account (if applicable)

    private Long parentAccountId; // ID of the parent account (if any)

    private LocalDateTime createdAt; // Creation timestamp

    private LocalDateTime updatedAt; // Last update timestamp

    private String status; // Current status of the account

    private Long assignedToUserId; // ID of the user the account is assigned to

    private String notes; // Additional notes or details about the account
}
