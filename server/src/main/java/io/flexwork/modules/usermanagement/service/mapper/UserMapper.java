package io.flexwork.modules.usermanagement.service.mapper;

import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(source = "manager", target = "managerName", qualifiedByName = "mapManagerName")
    UserDTO toDto(User user);

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
}
