package io.flexwork.modules.crm.service.mapper;

import static org.junit.jupiter.api.Assertions.*;

import io.flexwork.modules.crm.domain.Account;
import io.flexwork.modules.crm.service.dto.AccountDTO;
import io.flexwork.modules.usermanagement.domain.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class AccountMapperTest {

    private AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Test
    public void testAccountDTOToAccount() {
        AccountDTO accountDTO =
                AccountDTO.builder()
                        .id(1L)
                        .parentAccountId(1L)
                        .assignedToUserId(1L)
                        .industry("industry")
                        .build();
        Account account = accountMapper.accountDTOToAccount(accountDTO);
        assertAll(
                () -> assertEquals(accountDTO.getIndustry(), account.getIndustry()),
                () -> assertEquals(accountDTO.getType(), account.getType()),
                () -> assertEquals(accountDTO.getAddressLine2(), account.getAddressLine2()),
                () -> assertEquals(accountDTO.getId(), account.getId()),
                () ->
                        assertEquals(
                                accountDTO.getAssignedToUserId(),
                                account.getAssignedToUser().getId()),
                () ->
                        assertEquals(
                                accountDTO.getParentAccountId(),
                                account.getParentAccount().getId()));
    }

    @Test
    public void testAccountToAccountDTO() {
        Account account = Account.builder().id(1L).name("accountName").build();
        AccountDTO accountDTO = accountMapper.accountToAccountDTO(account);
        assertAll(
                () -> assertEquals(account.getName(), accountDTO.getName()),
                () -> assertNull(accountDTO.getAssignedToUserId()),
                () -> assertNull(accountDTO.getParentAccountId()));

        Account account2 =
                Account.builder()
                        .id(2L)
                        .name("accountName")
                        .parentAccount(account)
                        .assignedToUser(User.builder().id(1L).build())
                        .build();
        AccountDTO accountDTO2 = accountMapper.accountToAccountDTO(account2);
        assertAll(
                () -> assertEquals(account2.getName(), accountDTO.getName()),
                () ->
                        assertEquals(
                                account2.getParentAccount().getId(),
                                accountDTO2.getParentAccountId()),
                () ->
                        assertEquals(
                                account2.getAssignedToUser().getId(),
                                accountDTO2.getAssignedToUserId()));
    }

    @Test
    public void testUpdateAccount() {
        AccountDTO accountDTO =
                AccountDTO.builder()
                        .id(1L)
                        .parentAccountId(1L)
                        .assignedToUserId(1L)
                        .industry("industry")
                        .build();

        Account account = Account.builder().id(1L).name("accountName").build();
        accountMapper.updateAccountFromDTO(accountDTO, account);
        assertAll(
                () -> assertEquals("accountName", account.getName()),
                () -> assertEquals("industry", account.getIndustry()),
                () -> assertEquals(1L, account.getParentAccount().getId()),
                () -> assertEquals(1, account.getAssignedToUser().getId()));
    }
}
