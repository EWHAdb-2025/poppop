package db2025.DB2025Team05_poppop.DB2025Team05_service;

import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.DisposalRecord;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.DispRecRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DisposalServiceTest {

    @Mock
    private DispRecRepository dispRecRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PopupRepository popupRepository;

    @InjectMocks
    private DisposalService disposalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerDisposalRecord() throws Exception {
        int managerId = 1;
        int processorId = 2;
        int popupId = 3;

        DisposalRecord record = DisposalRecord.builder()
                .disposalId(100)
                .userId(processorId)
                .popupId(popupId)
                .disposalDate(LocalDateTime.now())
                .wasteId(10)
                .status("COMPLETED")
                .build();

        when(userRepository.findByUserId(managerId)).thenReturn(Optional.of(User.builder().id(managerId).role(Role.MANAGER).build()));
        when(userRepository.findByUserId(processorId)).thenReturn(Optional.of(new User()));
        when(popupRepository.findPopupById(popupId)).thenReturn(Optional.of(Map.of("popupId", popupId)));
        when(dispRecRepository.insertDisposalRecord(record)).thenReturn(true);

        DisposalRecord result = disposalService.registerDisposalRecord(record, managerId);
        assertNotNull(result);
        verify(dispRecRepository, times(1)).insertDisposalRecord(record);
    }

    @Test
    void updateDisposalRecord() throws Exception {
        int managerId = 1;
        int processorId = 2;
        int popupId = 3;

        DisposalRecord record = DisposalRecord.builder()
                .disposalId(200)
                .userId(processorId)
                .popupId(popupId)
                .disposalDate(LocalDateTime.now())
                .wasteId(11)
                .status("UPDATED")
                .build();

        when(userRepository.findByUserId(managerId)).thenReturn(Optional.of(User.builder().id(managerId).role(Role.MANAGER).build()));
        when(userRepository.findByUserId(processorId)).thenReturn(Optional.of(new User()));
        when(popupRepository.findPopupById(popupId)).thenReturn(Optional.of(Map.of("popupId", popupId)));
        when(dispRecRepository.updateDisposalRecord(record)).thenReturn(true);

        DisposalRecord result = disposalService.updateDisposalRecord(record, managerId);
        assertNotNull(result);
        verify(dispRecRepository, times(1)).updateDisposalRecord(record);
    }

    @Test
    void deleteDisposalRecord() throws Exception {
        int managerId = 1;
        int disposalId = 300;

        Map<String, Object> recordData = new HashMap<>();
        recordData.put("disposalId", disposalId);
        recordData.put("userId", 2);
        recordData.put("popupId", 3);
        recordData.put("status", "PENDING");
        recordData.put("disposalDate", LocalDateTime.now());

        when(userRepository.findByUserId(managerId)).thenReturn(Optional.of(User.builder().id(managerId).role(Role.MANAGER).build()));
        when(dispRecRepository.findDisRecByDisRecId(disposalId)).thenReturn(Optional.of(recordData));
        when(dispRecRepository.deleteDisRec(disposalId)).thenReturn(true);

        DisposalRecord result = disposalService.deleteDisposalRecord(disposalId, managerId);
        assertNotNull(result);
        assertEquals(disposalId, result.getDisposalId());
        verify(dispRecRepository, times(1)).deleteDisRec(disposalId);
    }

    @Test
    void getDisposalStatisticsByCompany() throws Exception {
        int managerId = 1;
        when(userRepository.findByUserId(managerId)).thenReturn(Optional.of(User.builder().id(managerId).role(Role.MANAGER).build()));
        when(dispRecRepository.getDisposalStatisticsByCompany()).thenReturn(List.of(Map.of("company", "A", "amount", 100)));

        List<Map<String, Object>> result = disposalService.getDisposalStatisticsByCompany(managerId);
        assertEquals(1, result.size());
    }

    @Test
    void getDisposalStatisticsByPopup() throws Exception {
        int managerId = 1;
        when(userRepository.findByUserId(managerId)).thenReturn(Optional.of(User.builder().id(managerId).role(Role.MANAGER).build()));
        when(dispRecRepository.getDisposalStatisticsByPopup()).thenReturn(List.of(Map.of("popup", "B", "amount", 200)));

        List<Map<String, Object>> result = disposalService.getDisposalStatisticsByPopup(managerId);
        assertEquals(1, result.size());
    }

    @Test
    void getDisposalStatisticsByMonth() throws Exception {
        int managerId = 1;
        when(userRepository.findByUserId(managerId)).thenReturn(Optional.of(User.builder().id(managerId).role(Role.MANAGER).build()));
        when(dispRecRepository.getDisposalStatisticsByMonth()).thenReturn(List.of(Map.of("month", "2025-05", "amount", 300)));

        List<Map<String, Object>> result = disposalService.getDisposalStatisticsByMonth(managerId);
        assertEquals(1, result.size());
    }
}
