package io.flexwork.modules.usermanagement.service;

import io.flexwork.modules.usermanagement.repository.AuthorityResourcePermissionRepository;
import io.flexwork.modules.usermanagement.service.dto.AuthorityResourcePermissionDTO;
import io.flexwork.modules.usermanagement.service.mapper.AuthorityResourcePermissionMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuthorityResourcePermissionService {
    private final AuthorityResourcePermissionMapper authorityResourcePermissionMapper;
    private final AuthorityResourcePermissionRepository authorityResourcePermissionRepository;

    public AuthorityResourcePermissionService(
            AuthorityResourcePermissionRepository authorityResourcePermissionRepository,
            AuthorityResourcePermissionMapper authorityResourcePermissionMapper) {
        this.authorityResourcePermissionRepository = authorityResourcePermissionRepository;
        this.authorityResourcePermissionMapper = authorityResourcePermissionMapper;
    }

    /**
     * Retrieves all resource permissions associated with a specific authority.
     *
     * @param authorityName The name of the authority.
     * @return A list of AuthorityResourcePermission objects associated with the authority.
     */
    public List<AuthorityResourcePermissionDTO> getResourcePermissionsByAuthority(
            String authorityName) {
        return authorityResourcePermissionRepository.findAllByAuthorityName(authorityName).stream()
                .map(authorityResourcePermissionMapper::toDto)
                .toList();
    }

    public List<AuthorityResourcePermissionDTO> saveAllPermissions(
            List<AuthorityResourcePermissionDTO> authorityResourcePermissionDtos) {
        return authorityResourcePermissionMapper.toDtoList(
                authorityResourcePermissionRepository.saveAll(
                        authorityResourcePermissionMapper.toEntityList(
                                authorityResourcePermissionDtos)));
    }
}
