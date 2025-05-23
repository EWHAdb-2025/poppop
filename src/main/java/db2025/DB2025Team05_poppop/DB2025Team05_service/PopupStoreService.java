package db2025.DB2025Team05_poppop.DB2025Team05_service;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_POPUP_MANAGEMENT;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * 팝업스토어 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 팝업스토어 등록, 수정, 조회 기능을 제공
 * producer 권한을 가진 사용자만 팝업스토어를 등록/수정할 수 있음
 */
public class PopupStoreService {
    private final PopupRepository popupRepository;
    private final UserRepository userRepository;

    /**
     * PopupStoreService 생성자
     * @param conn 데이터베이스 연결 객체
     * @throws SQLException 데이터베이스 연결 실패 시 발생
     */
    public PopupStoreService(Connection conn) throws SQLException {
        this.popupRepository = new PopupRepository(conn);
        this.userRepository = new UserRepository(conn);
    }

    /**
     * 팝업스토어 등록
     * producer 권한 체크 및 운영 기간 유효성 검사 수행
     * 
     * @param popup 등록할 팝업스토어 정보
     * @return 등록 성공 여부
     */
    public boolean registerPopupStore(DB2025_POPUP_MANAGEMENT popup) {
        try {
            // producer 권한 체크
            var user = userRepository.findByUserId(popup.getUserId());
            if (user.isEmpty() || user.get().getRole() != Role.PRODUCER) {
                System.out.println("producer만 팝업스토어를 등록할 수 있습니다.");
                return false;
            }

            // 운영 기간 유효성 검사
            if (popup.getEndDate().isBefore(popup.getStartDate())) {
                System.out.println("운영 종료일은 시작일 이후여야 합니다");
                return false;
            }

            return popupRepository.insertPopup(popup);
        } catch (Exception e) {
            System.out.println("팝업스토어 등록 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 팝업스토어 정보 수정
     * 운영 기간 유효성 검사 수행
     * 
     * @param popup 수정할 팝업스토어 정보
     * @return 수정 성공 여부
     */
    public boolean updatePopupStore(DB2025_POPUP_MANAGEMENT popup) {
        try {
            // 운영 기간 유효성 검사
            if (popup.getEndDate().isBefore(popup.getStartDate())) {
                System.out.println("운영 종료일은 시작일 이후여야 합니다");
                return false;
            }

            return popupRepository.updatePopup(popup);
        } catch (Exception e) {
            System.out.println("팝업스토어 수정 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 팝업스토어 ID로 팝업스토어 정보 조회
     * @param popupId 조회할 팝업스토어 ID
     * @return 팝업스토어 정보 (없는 경우 null)
     */
    public DB2025_POPUP_MANAGEMENT findPopupById(int popupId) {
        return popupRepository.findByPopupId(popupId).orElse(null);
    }
} 