package io.flexwork.modules.crm.service.mapper;

import io.flexwork.modules.crm.domain.Account;
import io.flexwork.modules.crm.domain.Action;
import io.flexwork.modules.crm.domain.ActivityLog;
import io.flexwork.modules.crm.service.dto.AccountDTO;
import io.flexwork.modules.usermanagement.domain.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    // Mapping from entity to DTO
    @Mapping(target = "parentAccountId", source = "parentAccount.id")
    @Mapping(target = "assignedToUserId", source = "assignedToUser.id")
    AccountDTO toDto(Account account);

    // Mapping from DTO to entity
    @Mapping(
            target = "assignedToUser",
            expression = "java(ofUser(accountDTO.getAssignedToUserId()))")
    @Mapping(
            target = "parentAccount",
            expression = "java(ofParentAccount(accountDTO.getParentAccountId()))")
    Account toEntity(AccountDTO accountDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(
            target = "assignedToUser",
            expression = "java(ofUser(accountDTO.getAssignedToUserId()))")
    @Mapping(
            target = "parentAccount",
            expression = "java(ofParentAccount(accountDTO.getParentAccountId()))")
    void updateFromDto(AccountDTO accountDTO, @MappingTarget Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "entityType", constant = "ACCOUNT")
    @Mapping(target = "entityId", source = "account.id")
    @Mapping(target = "user", expression = "java(ofUser(updatedUserId))")
    ActivityLog accountEntityToActivityLog(Account account, Action action, Long updatedUserId);

    default User ofUser(Long userId) {
        return (userId == null) ? null : User.builder().id(userId).build();
    }

    default Account ofParentAccount(Long parentAccountId) {
        return (parentAccountId == null) ? null : Account.builder().id(parentAccountId).build();
    }
}
