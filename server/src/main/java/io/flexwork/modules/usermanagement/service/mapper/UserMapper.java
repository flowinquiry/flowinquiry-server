package io.flexwork.modules.usermanagement.service.mapper;

import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(source = "manager", target = "managerName", qualifiedByName = "mapManagerName")
    @Mapping(target = "managerImageUrl", source = "manager.imageUrl")
    UserDTO toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UserDTO userDTO, @MappingTarget User user);

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
