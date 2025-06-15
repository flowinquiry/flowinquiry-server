package io.flowinquiry.modules.collab.service.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.flowinquiry.modules.collab.domain.EntityType;
import io.flowinquiry.modules.collab.domain.EntityWatcher;
import io.flowinquiry.modules.fss.service.dto.EntityWatcherDTO;
import io.flowinquiry.modules.usermanagement.domain.User;
import io.flowinquiry.modules.usermanagement.service.mapper.UserMapper;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class EntityWatcherMapperTest {

    private EntityWatcherMapper entityWatcherMapper;

    @Mock(lenient = true)
    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        entityWatcherMapper = new EntityWatcherMapperImpl();
        ReflectionTestUtils.setField(entityWatcherMapper, "userMapper", userMapper);

        // Set up mock behavior
        when(userMapper.getFullName(any(User.class)))
                .thenAnswer(
                        invocation -> {
                            User user = invocation.getArgument(0);
                            if (user == null) {
                                return null;
                            }
                            return user.getFirstName() + " " + user.getLastName();
                        });
    }

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

        EntityWatcher entityWatcher =
                EntityWatcher.builder()
                        .id(1L)
                        .entityType(EntityType.Ticket)
                        .entityId(2L)
                        .watchUser(user)
                        .createdAt(now)
                        .createdBy(1L)
                        .build();

        // When
        EntityWatcherDTO entityWatcherDTO = entityWatcherMapper.toDTO(entityWatcher);

        // Then
        assertAll(
                () -> assertEquals(entityWatcher.getId(), entityWatcherDTO.getId()),
                () ->
                        assertEquals(
                                entityWatcher.getEntityType().toString(),
                                entityWatcherDTO.getEntityType()),
                () -> assertEquals(entityWatcher.getEntityId(), entityWatcherDTO.getEntityId()),
                () ->
                        assertEquals(
                                entityWatcher.getWatchUser().getId(),
                                entityWatcherDTO.getWatchUserId()),
                () -> assertEquals("John Doe", entityWatcherDTO.getWatchUserName()),
                () ->
                        assertEquals(
                                entityWatcher.getWatchUser().getImageUrl(),
                                entityWatcherDTO.getWatcherImageUrl()),
                () -> assertEquals(entityWatcher.getCreatedAt(), entityWatcherDTO.getCreatedAt()),
                () -> assertEquals(entityWatcher.getCreatedBy(), entityWatcherDTO.getCreatedBy()));
    }

    @Test
    public void testToDTOList() {
        // Given
        Instant now = Instant.now();
        User user1 =
                User.builder()
                        .id(1L)
                        .firstName("John")
                        .lastName("Doe")
                        .imageUrl("http://example.com/image1.jpg")
                        .build();

        User user2 =
                User.builder()
                        .id(2L)
                        .firstName("Jane")
                        .lastName("Smith")
                        .imageUrl("http://example.com/image2.jpg")
                        .build();

        EntityWatcher entityWatcher1 =
                EntityWatcher.builder()
                        .id(1L)
                        .entityType(EntityType.Ticket)
                        .entityId(2L)
                        .watchUser(user1)
                        .createdAt(now)
                        .createdBy(1L)
                        .build();

        EntityWatcher entityWatcher2 =
                EntityWatcher.builder()
                        .id(2L)
                        .entityType(EntityType.Ticket)
                        .entityId(3L)
                        .watchUser(user2)
                        .createdAt(now)
                        .createdBy(2L)
                        .build();

        List<EntityWatcher> entityWatchers = Arrays.asList(entityWatcher1, entityWatcher2);

        // When
        List<EntityWatcherDTO> entityWatcherDTOs = entityWatcherMapper.toDTOList(entityWatchers);

        // Then
        assertEquals(2, entityWatcherDTOs.size());

        // Verify first DTO
        EntityWatcherDTO dto1 = entityWatcherDTOs.get(0);
        assertAll(
                () -> assertEquals(entityWatcher1.getId(), dto1.getId()),
                () -> assertEquals(entityWatcher1.getEntityType().toString(), dto1.getEntityType()),
                () -> assertEquals(entityWatcher1.getEntityId(), dto1.getEntityId()),
                () -> assertEquals(entityWatcher1.getWatchUser().getId(), dto1.getWatchUserId()),
                () -> assertEquals("John Doe", dto1.getWatchUserName()),
                () ->
                        assertEquals(
                                entityWatcher1.getWatchUser().getImageUrl(),
                                dto1.getWatcherImageUrl()));

        // Verify second DTO
        EntityWatcherDTO dto2 = entityWatcherDTOs.get(1);
        assertAll(
                () -> assertEquals(entityWatcher2.getId(), dto2.getId()),
                () -> assertEquals(entityWatcher2.getEntityType().toString(), dto2.getEntityType()),
                () -> assertEquals(entityWatcher2.getEntityId(), dto2.getEntityId()),
                () -> assertEquals(entityWatcher2.getWatchUser().getId(), dto2.getWatchUserId()),
                () -> assertEquals("Jane Smith", dto2.getWatchUserName()),
                () ->
                        assertEquals(
                                entityWatcher2.getWatchUser().getImageUrl(),
                                dto2.getWatcherImageUrl()));
    }

    @Test
    public void testToEntity() {
        // Given
        Instant now = Instant.now();
        EntityWatcherDTO entityWatcherDTO = new EntityWatcherDTO();
        entityWatcherDTO.setId(1L);
        entityWatcherDTO.setEntityType("Ticket");
        entityWatcherDTO.setEntityId(2L);
        entityWatcherDTO.setWatchUserId(1L);
        entityWatcherDTO.setWatchUserName("John Doe");
        entityWatcherDTO.setWatcherImageUrl("http://example.com/image.jpg");
        entityWatcherDTO.setCreatedAt(now);
        entityWatcherDTO.setCreatedBy(1L);

        // Note: We can't directly test the watchUser mapping because mapManagerIdToUser is
        // protected

        // When
        EntityWatcher entityWatcher = entityWatcherMapper.toEntity(entityWatcherDTO);

        // Then
        assertAll(
                () -> assertEquals(entityWatcherDTO.getId(), entityWatcher.getId()),
                () ->
                        assertEquals(
                                EntityType.valueOf(entityWatcherDTO.getEntityType()),
                                entityWatcher.getEntityType()),
                () -> assertEquals(entityWatcherDTO.getEntityId(), entityWatcher.getEntityId()));
    }
}
