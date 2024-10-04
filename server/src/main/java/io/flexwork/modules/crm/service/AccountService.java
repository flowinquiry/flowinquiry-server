package io.flexwork.modules.crm.service;

import static io.flexwork.query.QueryUtils.buildSpecification;

import io.flexwork.modules.crm.domain.Account;
import io.flexwork.modules.crm.event.ActivityLogEvent;
import io.flexwork.modules.crm.repository.AccountRepository;
import io.flexwork.query.QueryFilter;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final ApplicationEventPublisher eventPublisher;

    public AccountService(
            AccountRepository accountRepository, ApplicationEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.eventPublisher = eventPublisher;
    }

    // Find an account by its ID
    public Optional<Account> findAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    // Save a new account or update an existing one
    public Account saveAccount(Account account) {
        Account savedAccount = accountRepository.save(account);

        ActivityLogEvent activityLogEvent = new ActivityLogEvent(this);
        eventPublisher.publishEvent(activityLogEvent);
        return savedAccount;
    }

    public Account updateAccount(Long accountId, Account accountDetails) {

        Account existingAccount =
                accountRepository
                        .findById(accountId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Account not found with id: " + accountId));

        // Step 2: Update the fields of the existing account with the new details
        existingAccount.setName(accountDetails.getName());
        existingAccount.setType(accountDetails.getType());
        existingAccount.setIndustry(accountDetails.getIndustry());
        existingAccount.setWebsite(accountDetails.getWebsite());
        existingAccount.setPhoneNumber(accountDetails.getPhoneNumber());
        existingAccount.setEmail(accountDetails.getEmail());
        existingAccount.setAddressLine1(accountDetails.getAddressLine1());
        existingAccount.setAddressLine2(accountDetails.getAddressLine2());
        existingAccount.setCity(accountDetails.getCity());
        existingAccount.setState(accountDetails.getState());
        existingAccount.setPostalCode(accountDetails.getPostalCode());
        existingAccount.setCountry(accountDetails.getCountry());
        existingAccount.setAnnualRevenue(accountDetails.getAnnualRevenue());
        existingAccount.setParentAccount(accountDetails.getParentAccount());
        existingAccount.setStatus(accountDetails.getStatus());
        existingAccount.setAssignedToUser(accountDetails.getAssignedToUser());
        existingAccount.setNotes(accountDetails.getNotes());

        // Step 3: Save the updated account
        return accountRepository.save(existingAccount);
    }

    // Delete an account by its ID
    public void deleteAccountById(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    public Page<Account> findAllAccounts(List<QueryFilter> filters, Pageable pageable) {
        Specification<Account> spec = buildSpecification(filters);
        return accountRepository.findAll(spec, pageable);
    }

    public Optional<Account> getNextEntity(Long currentId) {
        return accountRepository.findNextEntity(currentId);
    }

    public Optional<Account> getPreviousEntity(Long currentId) {
        return accountRepository.findPreviousEntity(currentId);
    }
}
