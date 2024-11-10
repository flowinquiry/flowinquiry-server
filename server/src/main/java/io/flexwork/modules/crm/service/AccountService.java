package io.flexwork.modules.crm.service;

import static io.flexwork.query.QueryUtils.createSpecification;

import io.flexwork.modules.crm.domain.Account;
import io.flexwork.modules.crm.domain.Action;
import io.flexwork.modules.crm.event.ActivityLogEvent;
import io.flexwork.modules.crm.repository.AccountRepository;
import io.flexwork.modules.crm.service.dto.AccountDTO;
import io.flexwork.modules.crm.service.mapper.AccountMapper;
import io.flexwork.modules.usermanagement.service.dto.UserKey;
import io.flexwork.query.QueryDTO;
import io.flexwork.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final AuditServiceWrapperService<Account> activityServiceWrapper;

    private AccountMapper accountMapper;

    public AccountService(
            AccountRepository accountRepository,
            AuditServiceWrapperService<Account> activityServiceWrapper,
            AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.activityServiceWrapper = activityServiceWrapper;
        this.accountMapper = accountMapper;
    }

    public Optional<AccountDTO> findAccountById(Long accountId) {
        return accountRepository.findById(accountId).map(accountMapper::toDto);
    }

    // Save a new account or update an existing one
    public AccountDTO saveAccount(AccountDTO account) {
        Account accountEntity =
                activityServiceWrapper.saveEntity(
                        accountMapper.toEntity(account),
                        accountRepository,
                        (savedAccount) ->
                                new ActivityLogEvent(
                                        this,
                                        accountMapper.accountEntityToActivityLog(
                                                savedAccount,
                                                Action.CREATE,
                                                SecurityUtils.getCurrentUserLogin()
                                                        .map(UserKey::getId)
                                                        .orElse(null))));
        return accountMapper.toDto(accountEntity);
    }

    public AccountDTO updateAccount(Long accountId, AccountDTO accountDetails) {
        Account accountEntityDetails = accountMapper.toEntity(accountDetails);

        Account existingAccount =
                accountRepository
                        .findById(accountId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Account not found with id: " + accountId));

        existingAccount.setName(accountEntityDetails.getName());
        existingAccount.setType(accountEntityDetails.getType());
        existingAccount.setIndustry(accountEntityDetails.getIndustry());
        existingAccount.setWebsite(accountEntityDetails.getWebsite());
        existingAccount.setPhoneNumber(accountEntityDetails.getPhoneNumber());
        existingAccount.setEmail(accountEntityDetails.getEmail());
        existingAccount.setAddressLine1(accountEntityDetails.getAddressLine1());
        existingAccount.setAddressLine2(accountEntityDetails.getAddressLine2());
        existingAccount.setCity(accountEntityDetails.getCity());
        existingAccount.setState(accountEntityDetails.getState());
        existingAccount.setPostalCode(accountEntityDetails.getPostalCode());
        existingAccount.setCountry(accountEntityDetails.getCountry());
        existingAccount.setAnnualRevenue(accountEntityDetails.getAnnualRevenue());
        existingAccount.setParentAccount(accountEntityDetails.getParentAccount());
        existingAccount.setStatus(accountEntityDetails.getStatus());
        existingAccount.setAssignedToUser(accountEntityDetails.getAssignedToUser());
        existingAccount.setNotes(accountEntityDetails.getNotes());

        // Step 3: Save the updated account
        return accountMapper.toDto(accountRepository.save(existingAccount));
    }

    // Delete an account by its ID
    public void deleteAccountById(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    @Transactional
    public void deleteAccounts(List<Long> ids) {
        accountRepository.deleteAllByIdInBatch(ids);
    }

    public Page<AccountDTO> findAccounts(Optional<QueryDTO> queryDTO, Pageable pageable) {
        Specification<Account> spec = createSpecification(queryDTO);
        return accountRepository.findAll(spec, pageable).map(accountMapper::toDto);
    }

    public Optional<AccountDTO> getNextEntity(Long currentId) {
        return accountRepository.findNextEntity(currentId).map(accountMapper::toDto);
    }

    public Optional<AccountDTO> getPreviousEntity(Long currentId) {
        return accountRepository.findPreviousEntity(currentId).map(accountMapper::toDto);
    }
}
