package io.flowinquiry.modules.fss.service.mapper;

import io.flowinquiry.modules.fss.domain.EntityAttachment;
import io.flowinquiry.modules.fss.service.dto.EntityAttachmentDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EntityAttachmentMapper {

    EntityAttachmentDTO toDto(EntityAttachment entity);

    List<EntityAttachmentDTO> toDtoList(List<EntityAttachment> entities);
}
