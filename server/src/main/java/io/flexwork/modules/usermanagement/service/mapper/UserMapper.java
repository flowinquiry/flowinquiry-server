package io.flexwork.modules.usermanagement.service.mapper;

import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);

    List<UserDTO> toDtos(List<User> users);
}
