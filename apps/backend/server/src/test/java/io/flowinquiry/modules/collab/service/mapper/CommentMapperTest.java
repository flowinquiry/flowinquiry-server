package io.flowinquiry.modules.collab.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.flowinquiry.modules.collab.domain.Comment;
import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.service.dto.CommentDTO;
import io.flowinquiry.modules.usermanagement.domain.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class CommentMapperTest {

    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    public void testToDTO() {
        // Given
        Instant now = Instant.now();
        User user =
                User.builder()
                        .id(1L)
                        .firstName("John")
                        .lastName("Doe")
                        .imageUrl("http://example.com/image.jpg")
                        .build();

        Comment comment =
                Comment.builder()
                        .id(1L)
                        .content("Test comment")
                        .entityType(EntityType.Ticket)
                        .entityId(2L)
                        .createdAt(now)
                        .createdByUser(user)
                        .build();

        // When
        CommentDTO commentDTO = commentMapper.toDTO(comment);

        // Then
        assertAll(
                () -> assertEquals(comment.getId(), commentDTO.getId()),
                () -> assertEquals(comment.getContent(), commentDTO.getContent()),
                () -> assertEquals(comment.getEntityType(), commentDTO.getEntityType()),
                () -> assertEquals(comment.getEntityId(), commentDTO.getEntityId()),
                () -> assertEquals(comment.getCreatedAt(), commentDTO.getCreatedAt()),
                () -> assertEquals(comment.getCreatedByUser().getId(), commentDTO.getCreatedById()),
                () -> assertEquals("John Doe", commentDTO.getCreatedByName()),
                () ->
                        assertEquals(
                                comment.getCreatedByUser().getImageUrl(),
                                commentDTO.getCreatedByImageUrl()));
    }

    @Test
    public void testToDTOWithNullCreatedBy() {
        // Given
        Instant now = Instant.now();
        Comment comment =
                Comment.builder()
                        .id(1L)
                        .content("Test comment")
                        .entityType(EntityType.Ticket)
                        .entityId(2L)
                        .createdAt(now)
                        .createdByUser(null)
                        .build();

        // When
        CommentDTO commentDTO = commentMapper.toDTO(comment);

        // Then
        assertAll(
                () -> assertEquals(comment.getId(), commentDTO.getId()),
                () -> assertEquals(comment.getContent(), commentDTO.getContent()),
                () -> assertEquals(comment.getEntityType(), commentDTO.getEntityType()),
                () -> assertEquals(comment.getEntityId(), commentDTO.getEntityId()),
                () -> assertEquals(comment.getCreatedAt(), commentDTO.getCreatedAt()),
                () -> assertNull(commentDTO.getCreatedById()),
                () -> assertNull(commentDTO.getCreatedByName()),
                () -> assertNull(commentDTO.getCreatedByImageUrl()));
    }

    @Test
    public void testToEntity() {
        // Given
        Instant now = Instant.now();
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setContent("Test comment");
        commentDTO.setEntityType(EntityType.Ticket);
        commentDTO.setEntityId(2L);
        commentDTO.setCreatedAt(now);
        commentDTO.setCreatedById(1L);
        commentDTO.setCreatedByName("John Doe");
        commentDTO.setCreatedByImageUrl("http://example.com/image.jpg");

        // When
        Comment comment = commentMapper.toEntity(commentDTO);

        // Then
        assertAll(
                () -> assertEquals(commentDTO.getId(), comment.getId()),
                () -> assertEquals(commentDTO.getContent(), comment.getContent()),
                () -> assertEquals(commentDTO.getEntityType(), comment.getEntityType()),
                () -> assertEquals(commentDTO.getEntityId(), comment.getEntityId()),
                () ->
                        assertEquals(
                                commentDTO.getCreatedById(), comment.getCreatedByUser().getId()));
    }
}
