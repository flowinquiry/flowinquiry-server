package io.flexwork.modules.crm.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.flexwork.modules.crm.domain.Account;
import io.flexwork.modules.crm.domain.Contact;
import io.flexwork.modules.crm.service.dto.ContactDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class ContactMapperTest {
    private ContactMapper contactMapper = Mappers.getMapper(ContactMapper.class);

    @Test
    public void testToEntity() {
        ContactDTO contactDTO =
                ContactDTO.builder().id(1L).accountId(1L).firstName("firstName").build();
        Contact contact = contactMapper.toEntity(contactDTO);
        assertAll(
                () -> assertEquals(contactDTO.getId(), contact.getId()),
                () -> assertEquals(contactDTO.getFirstName(), contact.getFirstName()),
                () -> assertEquals(contactDTO.getLastName(), contact.getLastName()),
                () -> assertEquals(contactDTO.getAccountId(), contact.getAccount().getId()));
    }

    @Test
    public void testToDto() {
        Contact contact =
                Contact.builder()
                        .id(1L)
                        .account(Account.builder().id(1L).build())
                        .firstName("firstName")
                        .build();
        ContactDTO contactDTO = contactMapper.toDto(contact);
        assertAll(
                () -> assertEquals(contact.getFirstName(), contactDTO.getFirstName()),
                () -> assertEquals(contact.getId(), contactDTO.getId()),
                () -> assertEquals(contact.getAccount().getId(), contactDTO.getAccountId()));
    }
}
