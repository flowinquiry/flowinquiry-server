package io.flexwork.modules.collab.service.mapper;

import io.flexwork.modules.collab.domain.Comment;
import io.flexwork.modules.collab.service.dto.CommentDTO;
import io.flexwork.modules.usermanagement.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(target = "createdByName", expression = "java(mapFullName(comment.getCreatedBy()))")
    CommentDTO toDTO(Comment comment);

    @Mapping(source = "createdById", target = "createdBy.id")
    Comment toEntity(CommentDTO commentDTO);

    default String mapFullName(User user) {
        if (user == null) {
            return null;
        }
        return user.getFirstName() + " " + user.getLastName();
    }
}
