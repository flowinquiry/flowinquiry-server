package io.flowinquiry.modules.fss.service.mapper;

import io.flowinquiry.modules.collab.domain.EntityWatcher;
import io.flowinquiry.modules.fss.service.dto.EntityWatcherDTO;
import io.flowinquiry.modules.usermanagement.service.mapper.UserMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface EntityWatcherMapper {

    @Mapping(source = "watchUser.id", target = "watchUserId")
    @Mapping(target = "watchUserName", source = "watchUser", qualifiedByName = "userFullName")
    @Mapping(target = "watcherImageUrl", source = "watchUser.imageUrl")
    EntityWatcherDTO toDTO(EntityWatcher entityWatcher);

    List<EntityWatcherDTO> toDTOList(List<EntityWatcher> entityWatchers);

    EntityWatcher toEntity(EntityWatcherDTO entityWatcherDTO);
}
