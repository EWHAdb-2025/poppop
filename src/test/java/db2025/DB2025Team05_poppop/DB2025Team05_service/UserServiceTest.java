package db2025.DB2025Team05_poppop.DB2025Team05_service;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.CompanyInfo;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.CompanyRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ValidUser_ShouldSucceed() throws SQLException {
        // Given
        User user = User.builder()
                .id(1)
                .name("테스트 사용자")
                .role(Role.MANAGER)
                .email("test@example.com")
                .build();

        CompanyInfo companyInfo = CompanyInfo.builder()
                .id(1)
                .companyName("테스트 회사")
                .build();

        when(userRepository.insertUser(any(User.class))).thenReturn(user);

        // When
        User registeredUser = userService.registerUser(user, companyInfo);

        // Then
        assertNotNull(registeredUser);
        assertEquals(user.getId(), registeredUser.getId());
        verify(userRepository, times(1)).insertUser(any(User.class));
    }

    @Test
    void findUserById_ExistingUser_ShouldReturnUser() throws SQLException {
        // Given
        int userId = 1;
        User expectedUser = User.builder()
                .id(userId)
                .name("테스트 사용자")
                .role(Role.MANAGER)
                .email("test@example.com")
                .build();
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(expectedUser));

        // When
        Optional<User> actualUser = userService.findUserById(userId);

        // Then
        assertTrue(actualUser.isPresent());
        assertEquals(userId, actualUser.get().getId());
        verify(userRepository, times(1)).findByUserId(userId);
    }

    @Test
    void updateUser_ValidUser_ShouldUpdateSuccessfully() {
        // Given
        int userId = 1;
        User updatedUserInfo = User.builder()
                .id(userId)
                .name("수정된 사용자")
                .role(Role.MANAGER)
                .email("updated@example.com")
                .build();

        User existingUser = User.builder()
                .id(userId)
                .name("테스트 사용자")
                .role(Role.MANAGER)
                .email("test@example.com")
                .build();

        // 기존 정보와 수정하려는 정보는 달라야 테스트 의미 있음
        when(userRepository.findByUserId(updatedUserInfo.getId()))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.updateUser(any(User.class)))
                .thenReturn(true);

        // When
        User result = userService.updateUser(updatedUserInfo);

        // Then
        assertNotNull(result);
        assertEquals(updatedUserInfo.getEmail(), result.getEmail());
        verify(userRepository, times(1)).updateUser(any(User.class));
    }



    @Test
    void deleteUser_ProducerUser_ShouldDeleteSuccessfully() throws SQLException {
        // Given
        int userId = 1;
        User producer = User.builder()
                .id(userId)
                .name("생산자")
                .email("producer@example.com")
                .role(Role.PRODUCER)
                .build();

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(producer));
        when(userRepository.deleteUser(userId)).thenReturn(true);

        // When
        boolean result = userService.deleteUser(userId);

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).deleteUser(userId);
    }

} 