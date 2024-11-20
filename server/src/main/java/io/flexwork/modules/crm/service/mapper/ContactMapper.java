package io.flexwork.modules.crm.service.mapper;

import io.flexwork.modules.crm.domain.Account;
import io.flexwork.modules.crm.domain.Contact;
import io.flexwork.modules.crm.service.dto.ContactDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "account.name", target = "accountName")
    ContactDTO toDto(Contact contact);

    @Mapping(target = "account", source = "accountId", qualifiedByName = "toAccount")
    Contact toEntity(ContactDTO contactDTO);

    @Mapping(target = "account", source = "accountId", qualifiedByName = "toAccount")
    void updateFromDto(ContactDTO contactDTO, @MappingTarget Contact contact);

    @Named("toAccount")
    default Account toAccount(Long accountId) {
        if (accountId == null) {
            return null;
        }
        Account account = new Account();
        account.setId(accountId);
        return account;
    }
}
