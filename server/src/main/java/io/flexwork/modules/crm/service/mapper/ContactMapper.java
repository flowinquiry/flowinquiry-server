package io.flexwork.modules.crm.service.mapper;

import io.flexwork.modules.crm.domain.Account;
import io.flexwork.modules.crm.domain.Contact;
import io.flexwork.modules.crm.service.dto.ContactDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "account.name", target = "accountName")
    ContactDTO toDto(Contact contact);

    @Mapping(target = "account", expression = "java(ofAccount(contactDTO.getAccountId()))")
    Contact toEntity(ContactDTO contactDTO);

    @Mapping(target = "account", expression = "java(ofAccount(contactDTO.getAccountId()))")
    void updateFromDto(ContactDTO contactDTO, @MappingTarget Contact contact);

    default Account ofAccount(Long accountId) {
        Account account = new Account();
        account.setId(accountId);
        return account;
    }
}
