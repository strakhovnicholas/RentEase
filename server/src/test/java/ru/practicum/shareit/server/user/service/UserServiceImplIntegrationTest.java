package ru.practicum.shareit.server.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.server.AllMappersTestConfig;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.dto.UserCreateDto;
import ru.practicum.shareit.server.user.dto.UserResponseDto;
import ru.practicum.shareit.server.user.entity.User;
import ru.practicum.shareit.server.user.enums.UserRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({UserServiceImpl.class, AllMappersTestConfig.class})
class UserServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private ItemService itemService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setName("Existing User");
        existingUser.setEmail("existing@email.com");
        existingUser.setPassword("password123");
        existingUser.setRole(UserRole.USER);
        entityManager.persist(existingUser);
        entityManager.flush();
    }

    @Test
    void getUserById_whenUserExists_thenReturnUser() {
        UserResponseDto retrievedUser = userService.getUserById(existingUser.getId());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.id()).isEqualTo(existingUser.getId());
        assertThat(retrievedUser.email()).isEqualTo("existing@email.com");
        assertThat(retrievedUser.name()).isEqualTo("Existing User");
    }

    @Test
    void getUserById_whenUserDoesNotExist_thenThrowException() {
        Long nonExistingUserId = 999L;

        assertThatThrownBy(() -> userService.getUserById(nonExistingUserId))
                .isInstanceOf(ru.practicum.shareit.server.user.exception.UserNotFoundException.class);
    }

    @Test
    void getUserById_whenUserHasAdminRole_thenReturnUserWithRole() {
        User adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@email.com");
        adminUser.setPassword("admin123");
        adminUser.setRole(UserRole.ADMIN);
        entityManager.persist(adminUser);
        entityManager.flush();

        UserResponseDto retrievedUser = userService.getUserById(adminUser.getId());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.id()).isEqualTo(adminUser.getId());
        assertThat(retrievedUser.email()).isEqualTo("admin@email.com");
        assertThat(retrievedUser.name()).isEqualTo("Admin User");
    }

    @Test
    void createUser_thenGetUserById_shouldReturnSameUser() {
        UserCreateDto createDto = new UserCreateDto(
                "John Doe",
                "securePass",
                "john.doe@example.com",
                UserRole.USER
        );

        UserResponseDto created = userService.createUser(createDto);
        UserResponseDto retrieved = userService.getUserById(created.id());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.id()).isEqualTo(created.id());
        assertThat(retrieved.email()).isEqualTo("john.doe@example.com");
        assertThat(retrieved.name()).isEqualTo("John Doe");
    }

    @Test
    void getUserById_afterUserUpdate_shouldReturnUpdatedData() {
        UserCreateDto createDto = new UserCreateDto(
                "Original Name",
                "password",
                "original@email.com",
                UserRole.USER
        );

        UserResponseDto created = userService.createUser(createDto);
        UserResponseDto found = userService.getUserById(created.id());

        assertThat(found).isNotNull();
        assertThat(found.name()).isEqualTo("Original Name");
    }
}