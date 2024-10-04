package io.flexwork.modules.crm.web.rest;

import static io.flexwork.query.QueryUtils.parseFiltersFromParams;

import io.flexwork.modules.crm.domain.Account;
import io.flexwork.modules.crm.service.AccountService;
import io.flexwork.modules.crm.service.dto.AccountDTO;
import io.flexwork.modules.crm.service.mapper.AccountMapper;
import io.flexwork.query.QueryFilter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        Optional<Account> account = accountService.findAccountById(id);
        if (account.isPresent()) {
            accountService.deleteAccountById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Page<AccountDTO>> getAllAccounts(
            @RequestParam Map<String, String> params, Pageable pageable) {
        List<QueryFilter> filters = parseFiltersFromParams(params);
        Page<Account> accounts = accountService.findAllAccounts(filters, pageable);
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
