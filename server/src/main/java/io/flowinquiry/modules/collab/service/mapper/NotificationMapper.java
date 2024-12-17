package io.flowinquiry.modules.collab.service.mapper;

import io.flowinquiry.modules.collab.domain.Notification;
import io.flowinquiry.modules.collab.service.dto.NotificationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "user.id", target = "userId")
    NotificationDTO toDTO(Notification notification);

    @Mapping(source = "userId", target = "user.id")
    Notification toEntity(NotificationDTO notificationDTO);
}
