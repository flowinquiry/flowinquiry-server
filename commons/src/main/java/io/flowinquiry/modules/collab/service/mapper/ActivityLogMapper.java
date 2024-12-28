package io.flowinquiry.modules.collab.service.mapper;

import io.flowinquiry.modules.collab.domain.ActivityLog;
import io.flowinquiry.modules.collab.service.dto.ActivityLogDTO;
import io.flowinquiry.modules.usermanagement.service.mapper.UserMapperUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapperUtils.class)
public interface ActivityLogMapper {

    @Mapping(source = "createdBy", target = "createdByName", qualifiedByName = "mapUserToFullName")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdBy.imageUrl", target = "createdByImageUrl")
    ActivityLogDTO toDTO(ActivityLog activityLog);
}
