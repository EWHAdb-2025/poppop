package db2025.DB2025Team05_poppop.DB2025Team05_service;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_DISPOSAL_RECORD;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.DispRecRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 폐기물 처리 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 폐기물 처리 이력 등록 및 통계 조회 기능을 제공
 * manager 권한을 가진 사용자만 폐기물 처리 이력을 등록할 수 있음
 */
public class DisposalService {
    private final DispRecRepository dispRecRepository;
    private final PopupRepository popupRepository;
    private final UserRepository userRepository;

    /**
     * DisposalService 생성자
     * @param conn 데이터베이스 연결 객체
     * @throws SQLException 데이터베이스 연결 실패 시 발생
     */
    public DisposalService(Connection conn) throws SQLException {
        this.dispRecRepository = new DispRecRepository(conn);
        this.popupRepository = new PopupRepository(conn);
        this.userRepository = new UserRepository(conn);
    }

    /**
     * 폐기물 처리 이력 등록
     * manager 권한 체크 및 팝업스토어 존재 여부 확인
     * 
     * @param record 등록할 폐기물 처리 이력 정보
     * @return 등록 성공 여부
     */
    public boolean registerDisposalRecord(DB2025_DISPOSAL_RECORD record) {
        try {
            // manager 권한 체크
            var user = userRepository.findByUserId(record.getUserId());
            if (user.isEmpty() || user.get().getRole() != Role.MANAGER) {
                System.out.println("manager만 폐기물 처리 이력을 등록할 수 있습니다.");
                return false;
            }

            // 팝업스토어 존재 여부 체크
            var popup = popupRepository.findByPopupId(record.getPopupId());
            if (popup.isEmpty()) {
                System.out.println("팝업 정보가 존재하지 않습니다");
                return false;
            }

            return dispRecRepository.insertDisposalRecord(record);
        } catch (Exception e) {
            System.out.println("폐기물 처리 이력 등록 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 업체별 폐기물 처리 통계 조회
     * DB2025_Disposal_Company_View 뷰를 사용하여 통계 정보 조회
     * 
     * @return 업체별 폐기물 처리 통계 정보 리스트
     */
    public List<Map<String, Object>> getDisposalStatisticsByCompany() {
        return dispRecRepository.getDisposalStatisticsByCompany();
    }

    /**
     * 팝업별 폐기물 처리 통계 조회
     * DB2025_Popup_Company_View 뷰를 사용하여 통계 정보 조회
     * 
     * @return 팝업별 폐기물 처리 통계 정보 리스트
     */
    public List<Map<String, Object>> getDisposalStatisticsByPopup() {
        return dispRecRepository.getDisposalStatisticsByPopup();
    }

    /**
     * 월별 폐기물 처리 통계 조회
     * 폐기물 처리 날짜를 기준으로 월별 통계 정보 조회
     * 
     * @return 월별 폐기물 처리 통계 정보 리스트
     */
    public List<Map<String, Object>> getDisposalStatisticsByMonth() {
        return dispRecRepository.getDisposalStatisticsByMonth();
    }
} 