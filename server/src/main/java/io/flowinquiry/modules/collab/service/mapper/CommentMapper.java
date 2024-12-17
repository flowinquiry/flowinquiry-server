package io.flowinquiry.modules.collab.service.mapper;

import io.flowinquiry.modules.collab.domain.Comment;
import io.flowinquiry.modules.collab.service.dto.CommentDTO;
import io.flowinquiry.modules.usermanagement.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "createdByUser.id", target = "createdById")
    @Mapping(target = "createdByName", expression = "java(mapFullName(comment.getCreatedByUser()))")
    @Mapping(source = "createdByUser.imageUrl", target = "createdByImageUrl")
    CommentDTO toDTO(Comment comment);

    @Mapping(source = "createdById", target = "createdByUser.id")
    Comment toEntity(CommentDTO commentDTO);

    default String mapFullName(User user) {
        if (user == null) {
            return null;
        }
        return user.getFirstName() + " " + user.getLastName();
    }
}
