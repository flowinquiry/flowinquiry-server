package io.flexwork.modules.crm.web.rest;

import io.flexwork.modules.crm.domain.Contact;
import io.flexwork.modules.crm.service.ContactService;
import io.flexwork.modules.crm.service.dto.ContactDTO;
import io.flexwork.modules.crm.service.mapper.ContactMapper;
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

    private ContactMapper contactMapper;

    private ContactService contactService;

    public ContactController(ContactService contactService, ContactMapper contactMapper) {
        this.contactService = contactService;
        this.contactMapper = contactMapper;
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ContactDTO>> findContacts(
            @Valid @RequestBody Optional<QueryDTO> queryDTO, Pageable pageable) {
        Page<ContactDTO> contacts = contactService.findContacts(queryDTO, pageable);
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @GetMapping("/account/{accountId}")
    public Page<ContactDTO> getContacts(@PathVariable Long accountId, Pageable pageable) {
        return contactService
                .findByAccountId(accountId, pageable)
                .map(value -> contactMapper.contactToContactDTO(value));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long id) {
        Optional<Contact> contact = contactService.getContactById(id);
        return contact.map(value -> ResponseEntity.ok(contactMapper.contactToContactDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ContactDTO createContact(@RequestBody ContactDTO contactDTO) {
        return contactMapper.contactToContactDTO(
                contactService.createContact(contactMapper.contactDTOToContact(contactDTO)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDTO> updateContact(
            @PathVariable Long id, @RequestBody ContactDTO contactDTO) {
        Contact updatedContact =
                contactService.updateContact(id, contactMapper.contactDTOToContact(contactDTO));
        return ResponseEntity.ok(contactMapper.contactToContactDTO(updatedContact));
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
}
