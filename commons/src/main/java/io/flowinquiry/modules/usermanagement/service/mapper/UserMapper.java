package io.flowinquiry.modules.usermanagement.service.mapper;

import io.flowinquiry.modules.usermanagement.domain.Authority;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    public UserDTO toDto(User user) {
        return toDto(user, new Context(true));
    }

    @Mapping(target = "managerId", ignore = true)
    @Mapping(target = "managerName", ignore = true)
    @Mapping(target = "managerImageUrl", ignore = true)
    @Mapping(
            target = "authorities",
            source = "authorities",
            qualifiedByName = "authorityToStringSet")
    public abstract UserDTO toDto(User user, @org.mapstruct.Context Context context);

    @AfterMapping
    protected void includeManagerFields(
            @MappingTarget UserDTO dto, User user, @org.mapstruct.Context Context context) {
        if (context.isIncludeManager && user.getManager() != null) {
            dto.setManagerId(user.getManager().getId());
            dto.setManagerName(mapManagerName(user.getManager()));
            dto.setManagerImageUrl(user.getManager().getImageUrl());
        }
    }

    @Mapping(target = "manager", source = "managerId", qualifiedByName = "mapManagerIdToUser")
    @Mapping(
            target = "authorities",
            source = "authorities",
            qualifiedByName = "stringToAuthoritySet")
    //    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntity(UserDTO userDTO, @MappingTarget User user);

    @Mapping(target = "manager", source = "managerId", qualifiedByName = "mapManagerIdToUser")
    @Mapping(
            target = "authorities",
            source = "authorities",
            qualifiedByName = "stringToAuthoritySet")
    public abstract User toEntity(UserDTO userDTO);

    public abstract List<UserDTO> toDtos(List<User> users);

    @Named("mapManagerName")
    protected String mapManagerName(User manager) {
        if (manager == null) {
            return null;
        }
        try {
            String firstName = manager.getFirstName() != null ? manager.getFirstName() : "";
            String lastName = manager.getLastName() != null ? manager.getLastName() : "";
            return (firstName + " " + lastName).trim();
        } catch (Exception e) {
            return null;
        }
    }

    @Named("authorityToStringSet")
    protected Set<String> authorityToStringSet(Set<Authority> authorities) {
        try {
            return authorities != null
                    ? authorities.stream().map(Authority::getName).collect(Collectors.toSet())
                    : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Named("stringToAuthoritySet")
    protected Set<Authority> stringToAuthoritySet(Set<String> authorityNames) {
        return authorityNames != null
                ? authorityNames.stream()
                        .map(
                                name -> {
                                    Authority authority = new Authority();
                                    authority.setName(name);
                                    return authority;
                                })
                        .collect(Collectors.toSet())
                : null;
    }

    @Named("mapManagerIdToUser")
    protected User mapManagerIdToUser(Long managerId) {
        if (managerId == null) {
            return null;
        }
        User manager = new User();
        manager.setId(managerId);
        return manager;
    }

    public static class Context {
        private final boolean isIncludeManager;

        public Context(Boolean isIncludeManager) {
            this.isIncludeManager = isIncludeManager;
        }
    }
}
