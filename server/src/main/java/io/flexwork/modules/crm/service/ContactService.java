package io.flexwork.modules.crm.service;

import static io.flexwork.query.QueryUtils.createSpecification;

import io.flexwork.modules.crm.domain.Contact;
import io.flexwork.modules.crm.repository.ContactRepository;
import io.flexwork.modules.crm.service.dto.ContactDTO;
import io.flexwork.modules.crm.service.mapper.ContactMapper;
import io.flexwork.query.QueryDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactService {

    private ContactRepository contactRepository;

    private ContactMapper contactMapper;

    public ContactService(ContactRepository contactRepository, ContactMapper contactMapper) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
    }

    public Page<ContactDTO> findByAccountId(Long accountId, Pageable pageable) {
        return contactRepository.findByAccountId(accountId, pageable).map(contactMapper::toDto);
    }

    public Optional<ContactDTO> getContactById(Long id) {
        return contactRepository.findById(id).map(contactMapper::toDto);
    }

    public ContactDTO createContact(ContactDTO contact) {
        return contactMapper.toDto(contactRepository.save(contactMapper.toEntity(contact)));
    }

    public ContactDTO updateContact(ContactDTO contactDetails) {
        Contact contact =
                contactRepository
                        .findById(contactDetails.getId())
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Contact not found with id "
                                                        + contactDetails.getId()));

        contact.setFirstName(contactDetails.getFirstName());
        contact.setLastName(contactDetails.getLastName());
        contact.setEmail(contactDetails.getEmail());
        contact.setPhone(contactDetails.getPhone());
        contact.setPosition(contactDetails.getPosition());
        contact.setNotes(contactDetails.getNotes());

        return contactMapper.toDto(contactRepository.save(contact));
    }

    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }

    @Transactional
    public void deleteContacts(List<Long> ids) {
        contactRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional(readOnly = true)
    public Page<ContactDTO> findContacts(Optional<QueryDTO> queryDTO, Pageable pageable) {
        Specification<Contact> spec = createSpecification(queryDTO);
        return contactRepository.findAll(spec, pageable).map(contactMapper::toDto);
    }

    public Optional<ContactDTO> getNextEntity(Long currentId) {
        return contactRepository.findNextEntity(currentId).map(contactMapper::toDto);
    }

    public Optional<ContactDTO> getPreviousEntity(Long currentId) {
        return contactRepository.findPreviousEntity(currentId).map(contactMapper::toDto);
    }
}
