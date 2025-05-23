package db2025.DB2025Team05_poppop.DB2025Team05_service;

import db2025.DB2025Team05_poppop.DB2025Team05_common.ErrorCode;
import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.DisposalRecord;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.Waste;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.DispRecRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.WasteRepository;
import org.antlr.v4.runtime.atn.SemanticContext;
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
    private final WasteRepository wasteRepository;

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
    public DisposalService(DispRecRepository dispRecRepository, UserRepository userRepository, PopupRepository popupRepository, WasteRepository wasteRepository) {
        this.dispRecRepository = dispRecRepository;
        this.userRepository = userRepository;
        this.popupRepository = popupRepository;
        this.wasteRepository = wasteRepository;
    }

    /**
     * 폐기물 처리 기록 등록
     *
     * 처리 과정:
     * 1. 매니저 권한 확인
     * 2. 처리업체 존재 확인 (userId)
     * 3. 팝업스토어 존재 확인 (popupId)
     * 4. DisposalRecord 입력값 유효성 검증
     * 5. 폐기물 정보(Waste) 저장 후 생성된 wasteId 획득
     * 6. DisposalRecord에 wasteId 설정
     * 7. DisposalRecord 저장
     *
     * @param record 등록할 처리 기록
     * @param managerId 등록을 시도하는 매니저 ID
     * @return 등록된 처리 기록
     * @throws BusinessException 권한 없음, 입력값 검증 실패, 저장 실패 시 발생
     */
    @Transactional
    public DisposalRecord registerDisposalRecord(DisposalRecord record, Waste waste, int managerId) {
        try {
            validateManagerPermission(managerId);
            validateProcessorExists(record.getUserId());
            validatePopupExists(record.getPopupId());
            validateDisposalInput(record);

            // 1. Waste 먼저 저장하고 생성된 ID를 받아옴
            Integer generatedWasteId = wasteRepository.insertWaste(waste);
            if (generatedWasteId == null) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "폐기물 정보 등록에 실패했습니다.");
            }

            // 2. DisposalRecord에 wasteId 설정
            record.setWasteId(generatedWasteId);

            // 3. DisposalRecord 저장
            boolean disposalSuccess = dispRecRepository.insertDisposalRecord(record);
            if (!disposalSuccess) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "폐기물 처리 기록 등록에 실패했습니다.");
            }

            return record;
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 폐기물 처리 기록 수정
     *
     * 처리 과정:
     * 1. 매니저 권한 확인
     * 2. 처리업체 존재 확인
     * 3. 팝업스토어 존재 확인
     * 4. DisposalRecord 입력값 유효성 검증
     * 5. Waste 테이블에서 폐기물 정보 수정 (wasteId 기준)
     * 6. DisposalRecord 테이블에서 처리 기록 수정
     * 
     * @param record 수정할 처리 기록
     * @param managerId 수정을 시도하는 매니저 ID
     * @return 수정된 처리 기록
     * @throws BusinessException 권한 없음, 입력값 검증 실패, 수정 실패 시 발생
     */
    @Transactional
    public DisposalRecord updateDisposalRecord(DisposalRecord record, Waste waste, int managerId) {
        try {
            validateManagerPermission(managerId);
            validateProcessorExists(record.getUserId());
            validatePopupExists(record.getPopupId());
            validateDisposalInput(record);

            // 1. 폐기물 정보 수정 먼저
            boolean wasteUpdated = wasteRepository.updateWaste(waste);
            if (!wasteUpdated) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "폐기물 정보 수정에 실패했습니다.");
            }

            // 2. 처리 기록 수정
            boolean recordUpdated = dispRecRepository.updateDisposalRecord(record);
            if (!recordUpdated) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "폐기물 처리 기록 수정에 실패했습니다.");
            }

            return record;
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 폐기물 처리 기록 삭제
     *
     * 처리 과정:
     * 1. 매니저 권한 확인
     * 2. 처리 기록(disposalId) 존재 여부 확인
     * 3. 관련 wasteId 추출
     * 4. DisposalRecord 삭제
     * 5. 해당 wasteId로 Waste 삭제
     * 6. 삭제된 DisposalRecord 정보 반환
     * 
     * @param disposalId 삭제할 처리 기록 ID
     * @param managerId 삭제를 시도하는 매니저 ID
     * @return 삭제된 처리 기록
     * @throws BusinessException 권한 없음, 기록 없음, 삭제 실패 시 발생
     */
    @Transactional
    public DisposalRecord deleteDisposalRecord(int disposalId, int managerId) {
        try {
            validateManagerPermission(managerId);

            Optional<Map<String, Object>> recordOpt = dispRecRepository.findDisRecByDisRecId(disposalId);
            if (recordOpt.isEmpty()) {
                throw new BusinessException(ErrorCode.RECORD_NOT_FOUND, "존재하지 않는 처리 기록입니다.");
            }

            Map<String, Object> recordData = recordOpt.get();
            Integer wasteId = (Integer) recordData.get("wasteId");

            // 1. 처리 기록 삭제
            boolean recordDeleted = dispRecRepository.deleteDisRec(disposalId);
            if (!recordDeleted) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "폐기물 처리 기록 삭제에 실패했습니다.");
            }

            // 2. 연관된 폐기물 정보 삭제
            boolean wasteDeleted = wasteRepository.deleteWaste(wasteId);
            if (!wasteDeleted) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "연관된 폐기물 정보 삭제에 실패했습니다.");
            }

            // 3. 반환
            return DisposalRecord.builder()
                    .disposalId(disposalId)
                    .userId((Integer) recordData.get("userId"))
                    .popupId((Integer) recordData.get("popupId"))
                    .status((String) recordData.get("status"))
                    .disposalDate((java.time.LocalDateTime) recordData.get("disposalDate"))
                    .wasteId(wasteId)
                    .build();
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 팝업별 폐기물 처리 통계 조회
     *
     * @param managerId 조회를 시도하는 매니저 ID
     * @return 회사별 처리 통계 목록
     * @throws BusinessException 권한 없음, 조회 실패 시 발생
     */
    public Optional<List<Map<String, Object>>> getDisposalStatisticsByPopup(int managerId, String popupName) {
        try {
            validateManagerPermission(managerId);
            return dispRecRepository.getDisposalStatisticsByPopupname(popupName);
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
    public Optional<List<Map<String, Object>>> getDisposalStatisticsByMonth(int managerId, int year, int month) {
        try {
            validateManagerPermission(managerId);
            return dispRecRepository.getDisposalStatisticsByPopup(year, month);
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }


    /**
     * 회사별 폐기물 처리 통계 조회
     *
     * @param managerId 조회를 시도하는 매니저 ID
     * @return 월별 처리 통계 목록
     * @throws BusinessException 권한 없음, 조회 실패 시 발생
     */
    public Optional<List<Map<String, Object>>> getDisposalStatisticsByCompanyname(int managerId, String companyname) {
        try {
            validateManagerPermission(managerId);
            return dispRecRepository. getDisposalStatisticsByCompanyname(companyname);
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
        Optional<User> userOpt = userRepository.findByUserId(managerId);
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
        Optional<User> userOpt = userRepository.findByUserId(processorId);
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
        Optional<Map<String, Object>>  popupOpt = popupRepository.findPopupById(popupId);
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
    private void validateDisposalInput(DisposalRecord record) {
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