package io.flowinquiry.modules.teams.service.mapper;

import io.flowinquiry.modules.teams.service.dto.WatcherDTO;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.service.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WatcherMapper {
    WatcherDTO toDto(User user);

    UserDTO toUserDto(WatcherDTO watcherDTO);
}
