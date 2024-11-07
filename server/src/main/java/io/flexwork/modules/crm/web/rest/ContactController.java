package io.flexwork.modules.crm.web.rest;

import io.flexwork.modules.crm.service.ContactService;
import io.flexwork.modules.crm.service.dto.ContactDTO;
import io.flexwork.query.QueryDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crm/contacts")
public class ContactController {

    private ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ContactDTO>> findContacts(
            @Valid @RequestBody Optional<QueryDTO> queryDTO, Pageable pageable) {
        Page<ContactDTO> contacts = contactService.findContacts(queryDTO, pageable);
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @GetMapping("/account/{accountId}")
    public Page<ContactDTO> getContacts(@PathVariable Long accountId, Pageable pageable) {
        return contactService.findByAccountId(accountId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long id) {
        Optional<ContactDTO> contact = contactService.getContactById(id);
        return contact.map(value -> ResponseEntity.ok(value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ContactDTO createContact(@RequestBody ContactDTO contactDTO) {
        return contactService.createContact(contactDTO);
    }

    @PutMapping()
    public ResponseEntity<ContactDTO> updateContact(@RequestBody ContactDTO contactDTO) {
        ContactDTO updatedContact = contactService.updateContact(contactDTO);
        return ResponseEntity.ok(updatedContact);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/contacts")
    public ResponseEntity<Void> deleteContacts(@RequestBody List<Long> ids) {
        contactService.deleteContacts(ids);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/next/{currentId}")
    public ResponseEntity<ContactDTO> getNextEntity(@PathVariable Long currentId) {
        return contactService
                .getNextEntity(currentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/previous/{currentId}")
    public ResponseEntity<ContactDTO> getPreviousEntity(@PathVariable Long currentId) {
        return contactService
                .getPreviousEntity(currentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
