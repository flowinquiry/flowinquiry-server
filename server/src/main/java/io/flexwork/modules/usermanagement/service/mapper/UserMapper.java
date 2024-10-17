package io.flexwork.modules.usermanagement.service.mapper;

import io.flexwork.modules.usermanagement.domain.User;
import io.flexwork.modules.usermanagement.service.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);
}
