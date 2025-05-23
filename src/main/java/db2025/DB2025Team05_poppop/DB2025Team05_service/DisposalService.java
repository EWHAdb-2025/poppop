package db2025.DB2025Team05_poppop.DB2025Team05_service;

import db2025.DB2025Team05_poppop.DB2025Team05_common.ErrorCode;
import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_DISPOSAL_RECORD;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_POPUP_MANAGEMENT;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_USER;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.DispRecRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 폐기물 처리 기록 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 
 * 주요 기능:
 * 1. 폐기물 처리 기록 등록 (매니저 전용)
 * 2. 폐기물 처리 통계 조회 (매니저 전용)
 *    - 회사별 통계
 *    - 팝업스토어별 통계
 *    - 월별 통계
 * 
 * 권한 관리:
 * - 매니저(MANAGER): 모든 기능 접근 가능
 * - 다른 사용자: 접근 불가
 */
@Service
public class DisposalService {
    private final DispRecRepository dispRecRepository;
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;

    /**
     * 시스템에서 사용되는 상수값들을 관리하는 내부 클래스
     * 
     * 상수 종류:
     * - MIN_WASTE_AMOUNT: 최소 폐기물량 (1kg)
     * - MAX_WASTE_AMOUNT: 최대 폐기물량 (1000kg)
     */
    private static final class Constants {
        private static final int MIN_WASTE_AMOUNT = 1;
        private static final int MAX_WASTE_AMOUNT = 1000;
    }

    /**
     * DisposalService 생성자
     * 
     * @param dispRecRepository 폐기물 처리 기록을 관리하는 저장소
     * @param userRepository 사용자 정보를 관리하는 저장소
     * @param popupRepository 팝업스토어 정보를 관리하는 저장소
     */
    public DisposalService(DispRecRepository dispRecRepository, UserRepository userRepository, PopupRepository popupRepository) {
        this.dispRecRepository = dispRecRepository;
        this.userRepository = userRepository;
        this.popupRepository = popupRepository;
    }

    /**
     * 폐기물 처리 기록 등록
     * 
     * 처리 과정:
     * 1. 매니저 권한 확인
     * 2. 처리업체 존재 확인
     * 3. 팝업스토어 존재 확인
     * 4. 입력값 유효성 검증
     * 5. 처리 기록 저장
     * 
     * @param record 등록할 처리 기록
     * @param managerId 등록을 시도하는 매니저 ID
     * @return 등록된 처리 기록
     * @throws BusinessException 권한 없음, 입력값 검증 실패, 저장 실패 시 발생
     */
    @Transactional
    public DB2025_DISPOSAL_RECORD registerDisposalRecord(DB2025_DISPOSAL_RECORD record, int managerId) {
        try {
            validateManagerPermission(managerId);
            validateProcessorExists(record.getUserId());
            validatePopupExists(record.getPopupId());
            validateDisposalInput(record);

            boolean success = dispRecRepository.insertDisposalRecord(record);
            if (!success) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "폐기물 처리 기록 등록에 실패했습니다.");
            }

            return record;
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 회사별 폐기물 처리 통계 조회
     * 
     * @param managerId 조회를 시도하는 매니저 ID
     * @return 회사별 처리 통계 목록
     * @throws BusinessException 권한 없음, 조회 실패 시 발생
     */
    public List<Map<String, Object>> getDisposalStatisticsByCompany(int managerId) {
        try {
            validateManagerPermission(managerId);
            return dispRecRepository.getDisposalStatisticsByCompany();
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 팝업스토어별 폐기물 처리 통계 조회
     * 
     * @param managerId 조회를 시도하는 매니저 ID
     * @return 팝업스토어별 처리 통계 목록
     * @throws BusinessException 권한 없음, 조회 실패 시 발생
     */
    public List<Map<String, Object>> getDisposalStatisticsByPopup(int managerId) {
        try {
            validateManagerPermission(managerId);
            return dispRecRepository.getDisposalStatisticsByPopup();
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 월별 폐기물 처리 통계 조회
     * 
     * @param managerId 조회를 시도하는 매니저 ID
     * @return 월별 처리 통계 목록
     * @throws BusinessException 권한 없음, 조회 실패 시 발생
     */
    public List<Map<String, Object>> getDisposalStatisticsByMonth(int managerId) {
        try {
            validateManagerPermission(managerId);
            return dispRecRepository.getDisposalStatisticsByMonth();
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // Private helper methods

    /**
     * 매니저 권한 확인
     * 
     * @param managerId 확인할 사용자 ID
     * @throws BusinessException 사용자가 없거나 매니저가 아닌 경우
     */
    private void validateManagerPermission(int managerId) throws SQLException {
        Optional<DB2025_USER> userOpt = userRepository.findById(managerId);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (userOpt.get().getRole() != Role.MANAGER) {
            throw new BusinessException(ErrorCode.INVALID_ROLE, "매니저만 접근 가능한 기능입니다.");
        }
    }

    /**
     * 처리업체 존재 확인
     * 
     * @param processorId 확인할 처리업체 ID
     * @throws BusinessException 처리업체가 존재하지 않는 경우
     */
    private void validateProcessorExists(int processorId) throws SQLException {
        Optional<DB2025_USER> userOpt = userRepository.findById(processorId);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 처리업체입니다.");
        }
    }

    /**
     * 팝업스토어 존재 확인
     * 
     * @param popupId 확인할 팝업스토어 ID
     * @throws BusinessException 팝업스토어가 존재하지 않는 경우
     */
    private void validatePopupExists(int popupId) throws SQLException {
        Optional<DB2025_POPUP_MANAGEMENT> popupOpt = popupRepository.findById(popupId);
        if (popupOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.POPUP_NOT_FOUND);
        }
    }

    /**
     * 폐기물 처리 기록 입력값 검증
     * 
     * 검증 항목:
     * 1. null 체크
     * 2. 폐기물량 범위 (1kg ~ 1000kg)
     * 3. 처리일자 유효성
     * 
     * @param record 검증할 처리 기록
     * @throws BusinessException 검증 실패 시
     */
    private void validateDisposalInput(DB2025_DISPOSAL_RECORD record) {
        if (record == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "처리 기록 정보가 없습니다.");
        }
        if (record.getWasteId() == 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "폐기물 정보가 없습니다.");
        }
        if (record.getDisposalDate() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "처리일자를 입력해주세요.");
        }
    }
} 