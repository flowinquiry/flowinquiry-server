package io.flexwork.modules.crm.web.rest;

import io.flexwork.modules.crm.domain.Account;
import io.flexwork.modules.crm.service.AccountService;
import io.flexwork.modules.crm.service.dto.AccountDTO;
import io.flexwork.modules.crm.service.mapper.AccountMapper;
import io.flexwork.query.QueryDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crm/accounts")
public class AccountController {

    private final AccountService accountService;

    private AccountMapper accountMapper;

    public AccountController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    // Get an account by ID
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.findAccountById(id);
        return account.map(value -> ResponseEntity.ok(accountMapper.accountToAccountDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new account
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO account) {
        Account savedAccount =
                accountService.saveAccount(accountMapper.accountDTOToAccount(account));
        return new ResponseEntity<>(
                accountMapper.accountToAccountDTO(savedAccount), HttpStatus.CREATED);
    }

    // Update an existing account
    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable Long id, @RequestBody AccountDTO accountDTO) {
        Account updatedAccount =
                accountService.updateAccount(id, accountMapper.accountDTOToAccount(accountDTO));
        return ResponseEntity.ok(accountMapper.accountToAccountDTO(updatedAccount));
    }

    // Delete an account by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccountById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccounts(@RequestBody List<Long> ids) {
        accountService.deleteAccounts(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<AccountDTO>> findAccounts(
            @Valid @RequestBody Optional<QueryDTO> queryDTO, Pageable pageable) {
        Page<Account> accounts = accountService.findAccounts(queryDTO, pageable);
        return new ResponseEntity<>(
                accounts.map(accountMapper::accountToAccountDTO), HttpStatus.OK);
    }

    @GetMapping("/next/{currentId}")
    public ResponseEntity<AccountDTO> getNextEntity(@PathVariable Long currentId) {
        return accountService
                .getNextEntity(currentId)
                .map(value -> ResponseEntity.ok(accountMapper.accountToAccountDTO(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/previous/{currentId}")
    public ResponseEntity<AccountDTO> getPreviousEntity(@PathVariable Long currentId) {
        return accountService
                .getPreviousEntity(currentId)
                .map(value -> ResponseEntity.ok(accountMapper.accountToAccountDTO(value)))
                .orElse(ResponseEntity.notFound().build());
    }
}
