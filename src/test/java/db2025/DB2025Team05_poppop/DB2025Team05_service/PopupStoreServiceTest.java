package db2025.DB2025Team05_poppop.DB2025Team05_service;

import db2025.DB2025Team05_poppop.DB2025Team05_common.ErrorCode;
import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PopupStoreServiceTest {

    @Mock
    private PopupRepository popupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PopupStoreService popupStoreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerPopupStore_ShouldSucceed() throws SQLException {
        // Given
        int userId = 1;
        User producer = new User();
        producer.setId(userId);
        producer.setRole(Role.PRODUCER);

        PopupManagement popup = new PopupManagement();
        popup.setName("테스트 팝업");
        popup.setAddress("서울시 강남구");
        popup.setStartDate(LocalDate.now());
        popup.setEndDate(LocalDate.now().plusDays(5));

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(producer));
        when(popupRepository.insertPopup(any(PopupManagement.class))).thenReturn(popup);

        // When
        PopupManagement result = popupStoreService.registerPopupStore(popup, userId);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).findByUserId(userId);
        verify(popupRepository, times(1)).insertPopup(any(PopupManagement.class));
    }

    @Test
    void updatePopupStore_ShouldSucceed() throws SQLException {
        // Given
        int userId = 1;
        int popupId = 10;
        PopupManagement popup = new PopupManagement();
        popup.setPopupId(popupId);
        popup.setName("수정된 팝업");
        popup.setAddress("서울시 강서구");
        popup.setStartDate(LocalDate.now());
        popup.setEndDate(LocalDate.now().plusDays(3));

        User producer = new User();
        producer.setId(userId);
        producer.setRole(Role.PRODUCER);

        Map<String, Object> existing = new HashMap<>();
        existing.put("userId", userId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(producer));
        when(popupRepository.findPopupById(popupId)).thenReturn(Optional.of(existing));
        when(popupRepository.updatePopup(any())).thenReturn(true);

        // When
        boolean result = popupStoreService.updatePopupStore(popup, userId);

        // Then
        assertTrue(result);
        verify(popupRepository, times(1)).updatePopup(popup);
    }

    @Test
    void deletePopupStore_ShouldSucceed() throws SQLException {
        // Given
        int popupId = 20;
        int userId = 1;

        User producer = new User();
        producer.setId(userId);
        producer.setRole(Role.PRODUCER);

        Map<String, Object> popupData = new HashMap<>();
        popupData.put("userId", userId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(producer));
        when(popupRepository.findPopupById(popupId)).thenReturn(Optional.of(popupData));
        when(popupRepository.deletePopup(popupId)).thenReturn(true);

        // When
        boolean result = popupStoreService.deletePopupStore(popupId, userId);

        // Then
        assertTrue(result);
        verify(popupRepository).deletePopup(popupId);
    }

    @Test
    void findPopupById_ShouldReturnData() throws SQLException {
        // Given
        int popupId = 30;
        Map<String, Object> popupInfo = new HashMap<>();
        popupInfo.put("popupId", popupId);
        popupInfo.put("name", "팝업명");

        when(popupRepository.findPopupById(popupId)).thenReturn(Optional.of(popupInfo));

        // When
        Optional<Map<String, Object>> result = popupStoreService.findPopupById(popupId);

        // Then
        assertTrue(result.isPresent());
        assertEquals("팝업명", result.get().get("name"));
    }
}
