package io.flexwork.modules.usermanagement.service.mapper;

import io.flexwork.modules.usermanagement.domain.Authority;
import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(source = "manager", target = "managerName", qualifiedByName = "mapManagerName")
    @Mapping(target = "managerImageUrl", source = "manager.imageUrl")
    @Mapping(
            target = "authorities",
            source = "authorities",
            qualifiedByName = "authorityToStringSet")
    UserDTO toDto(User user);

    @Mapping(
            target = "authorities",
            source = "authorities",
            qualifiedByName = "stringToAuthoritySet")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UserDTO userDTO, @MappingTarget User user);

    @Mapping(
            target = "authorities",
            source = "authorities",
            qualifiedByName = "stringToAuthoritySet")
    User toEntity(UserDTO userDTO);

    List<UserDTO> toDtos(List<User> users);

    @Named("mapManagerName")
    static String mapManagerName(User manager) {
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
    default Set<String> authorityToStringSet(Set<Authority> authorities) {
        return authorities != null
                ? authorities.stream().map(Authority::getName).collect(Collectors.toSet())
                : null;
    }

    @Named("stringToAuthoritySet")
    default Set<Authority> stringToAuthoritySet(Set<String> authorityNames) {
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
}
