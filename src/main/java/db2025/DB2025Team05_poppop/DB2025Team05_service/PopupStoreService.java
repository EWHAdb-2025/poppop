package db2025.DB2025Team05_poppop.DB2025Team05_service;

import db2025.DB2025Team05_poppop.DB2025Team05_common.ErrorCode;
import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

/**
 * 팝업스토어 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 
 * 주요 기능:
 * 1. 팝업스토어 등록 (생산자 전용)
 * 2. 팝업스토어 정보 수정 (생산자 전용)
 * 3. 팝업스토어 정보 조회
 * 
 * 권한 관리:
 * - 생산자(PRODUCER): 팝업스토어 등록 및 수정 권한
 * - 다른 사용자: 조회만 가능
 */
@Service
public class PopupStoreService {
    private final PopupRepository popupRepository;
    private final UserRepository userRepository;

    /**
     * 시스템에서 사용되는 상수값들을 관리하는 내부 클래스
     * 
     * 상수 종류:
     * - MIN_ADDRESS_LENGTH: 주소 최소 길이 (5자)
     * - MAX_ADDRESS_LENGTH: 주소 최대 길이 (200자)
     * - MIN_NAME_LENGTH: 팝업스토어명 최소 길이 (2자)
     * - MAX_NAME_LENGTH: 팝업스토어명 최대 길이 (100자)
     */
    private static final class Constants {
        private static final int MIN_ADDRESS_LENGTH = 5;
        private static final int MAX_ADDRESS_LENGTH = 200;
        private static final int MIN_NAME_LENGTH = 2;
        private static final int MAX_NAME_LENGTH = 100;
    }

    /**
     * PopupStoreService 생성자
     * 
     * @param popupRepository 팝업스토어 정보를 관리하는 저장소
     * @param userRepository 사용자 정보를 관리하는 저장소
     */
    public PopupStoreService(PopupRepository popupRepository, UserRepository userRepository) {
        this.popupRepository = popupRepository;
        this.userRepository = userRepository;
    }

    /**
     * 팝업스토어 등록
     * 
     * 처리 과정:
     * 1. 생산자 권한 확인
     * 2. 입력값 유효성 검증
     * 3. 팝업스토어 정보 저장
     * 
     * @param popup 등록할 팝업스토어 정보
     * @param userId 등록을 시도하는 사용자 ID
     * @return 등록된 팝업스토어 정보
     * @throws BusinessException 권한 없음, 입력값 검증 실패, 저장 실패 시 발생
     */
    @Transactional
    public PopupManagement registerPopupStore(PopupManagement popup, int userId) {
        try {
            validateProducerPermission(userId);
            validatePopupInput(popup);
            
            popup.setUserId(userId);
            PopupManagement savedPopup = popupRepository.insertPopup(popup);
            if (savedPopup == null) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "팝업스토어 등록에 실패했습니다.");
            }

            return savedPopup;
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 팝업스토어 정보 수정
     * 
     * 처리 과정:
     * 1. 생산자 권한 확인
     * 2. 기존 팝업스토어 존재 확인
     * 3. 소유권 확인
     * 4. 입력값 유효성 검증
     * 5. 팝업스토어 정보 수정
     * 
     * @param popup 수정할 팝업스토어 정보
     * @param userId 수정을 시도하는 사용자 ID
     * @return 수정 성공 여부
     * @throws BusinessException 권한 없음, 팝업스토어 없음, 소유권 없음, 입력값 검증 실패, 수정 실패 시 발생
     */
    @Transactional
    public boolean updatePopupStore(PopupManagement popup, int userId) {
        try {
            validateProducerPermission(userId);
            Map<String, Object> existingPopup = findExistingPopup(popup.getPopupId());
            validatePopupOwnership(existingPopup, userId);
            validatePopupInput(popup);

            if (!popupRepository.updatePopup(popup)) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "팝업스토어 수정에 실패했습니다.");
            }

            return true;
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 팝업스토어 정보 삭제
     * 
     * 처리 과정:
     * 1. 생산자 권한 확인
     * 2. 기존 팝업스토어 존재 확인
     * 3. 소유권 확인
     * 4. 팝업스토어 삭제
     * 
     * @param popupId 삭제할 팝업스토어 ID
     * @param userId 삭제를 시도하는 사용자 ID
     * @return 삭제 성공 여부
     * @throws BusinessException 권한 없음, 팝업스토어 없음, 소유권 없음, 삭제 실패 시 발생
     */
    @Transactional
    public boolean deletePopupStore(int popupId, int userId) {
        try {
            validateProducerPermission(userId);
            Map<String, Object> existingPopup = findExistingPopup(popupId);
            validatePopupOwnership(existingPopup, userId);

            if (!popupRepository.deletePopup(popupId)) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "팝업스토어 삭제에 실패했습니다.");
            }

            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 팝업스토어 조회
     * 
     * @param id 조회할 팝업스토어 ID
     * @return 팝업스토어 정보 (Optional)
     * @throws BusinessException 데이터베이스 오류 발생 시
     */
    public Optional<Map<String, Object>> findPopupById(int id) throws SQLException {
        return popupRepository.findPopupById(id);

    }

    // Private helper methods

    /**
     * 생산자 권한 확인
     * 
     * @param userId 확인할 사용자 ID
     * @throws BusinessException 사용자가 없거나 생산자가 아닌 경우
     */
    private void validateProducerPermission(int userId) throws SQLException {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (userOpt.get().getRole() != Role.PRODUCER) {
            throw new BusinessException(ErrorCode.INVALID_ROLE, "팝업스토어 등록은 생산자만 가능합니다.");
        }
    }

    /**
     * 팝업스토어 입력값 검증
     * 
     * 검증 항목:
     * 1. null 체크
     * 2. 주소 길이 (5-200자)
     * 3. 팝업스토어명 길이 (2-100자)
     * 4. 운영 기간 유효성
     * 
     * @param popup 검증할 팝업스토어 정보
     * @throws BusinessException 검증 실패 시
     */
    private void validatePopupInput(PopupManagement popup) {
        if (popup == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "팝업스토어 정보가 없습니다.");
        }
        if (popup.getAddress() == null || 
            popup.getAddress().length() < Constants.MIN_ADDRESS_LENGTH || 
            popup.getAddress().length() > Constants.MAX_ADDRESS_LENGTH) {
            throw new BusinessException(ErrorCode.INVALID_ADDRESS_LENGTH);
        }
        if (popup.getName() == null || 
            popup.getName().length() < Constants.MIN_NAME_LENGTH || 
            popup.getName().length() > Constants.MAX_NAME_LENGTH) {
            throw new BusinessException(ErrorCode.INVALID_POPUP_NAME_LENGTH);
        }
        if (popup.getStartDate() == null || popup.getEndDate() == null) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION_PERIOD, "운영 기간을 입력해주세요.");
        }
        if (popup.getStartDate().isAfter(popup.getEndDate())) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION_PERIOD, "운영 종료일은 시작일 이후여야 합니다.");
        }
    }

    /**
     * 기존 팝업스토어 존재 확인
     * 
     * @param popupId 확인할 팝업스토어 ID
     * @return 존재하는 팝업스토어 정보
     * @throws BusinessException 팝업스토어가 존재하지 않는 경우
     */
    private Map<String, Object> findExistingPopup(int popupId) throws SQLException {
        return popupRepository.findPopupById(popupId)
            .orElseThrow(() -> new BusinessException(ErrorCode.POPUP_NOT_FOUND));
    }

    /**
     * 팝업스토어 소유권 확인
     * 
     * @param popup 확인할 팝업스토어 정보
     * @param userId 확인할 사용자 ID
     * @throws BusinessException 소유권이 없는 경우
     */
    private void validatePopupOwnership(Map<String, Object> popup, int userId) {
        if ((Integer)popup.get("userId") != userId) {
            throw new BusinessException(ErrorCode.INVALID_ROLE, "다른 사용자의 팝업스토어는 수정할 수 없습니다.");
        }
    }
} 