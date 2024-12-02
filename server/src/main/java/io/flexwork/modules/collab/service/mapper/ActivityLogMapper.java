package io.flexwork.modules.collab.service.mapper;

import io.flexwork.modules.collab.domain.ActivityLog;
import io.flexwork.modules.collab.service.dto.ActivityLogDTO;
import io.flexwork.modules.usermanagement.service.mapper.UserMapperUtils;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapperUtils.class)
public interface ActivityLogMapper {

    ActivityLogDTO toDTO(ActivityLog activityLog);
}
