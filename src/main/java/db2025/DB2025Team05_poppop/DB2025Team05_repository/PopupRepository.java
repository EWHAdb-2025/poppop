package db2025.DB2025Team05_poppop.DB2025Team05_repository;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
import java.sql.*;
import java.util.*;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PopupRepository {
    private final Connection conn;

    public PopupRepository() throws SQLException {
        this.conn = DBConnection.getConnection();
    }

    // insert
    public PopupManagement insertPopup(PopupManagement popup) throws SQLException {
        String sql = "insert into DB2025_POPUP_MANAGEMENT (user_id, name, address, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, popup.getUserId());
            pstmt.setString(2, popup.getName());
            pstmt.setString(3, popup.getAddress());
            pstmt.setDate(4, java.sql.Date.valueOf(popup.getStartDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(popup.getEndDate()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    popup.setPopupId(generatedKeys.getInt(1));
                    return popup;
                } else {
                    return null;
                }
            }
        }
    }

    // DB2025_POPUP_COMPANY_VIEW 로 팝업 & 폐기물 처리 관리 회사 통합 조회
    public Optional<Map<String, Object>> findPopupById(int popupId) {
        String sql = "select * from DB2025_POPUP_COMPANY_VIEW where popup_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, popupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("popupId", rs.getInt("popup_id"));
                result.put("popupName", rs.getString("popup_name"));
                result.put("businessNumber", rs.getString("business_number"));
                result.put("representativeName", rs.getString("representative_name"));
                result.put("representativePhone", rs.getString("representative_phone"));
                result.put("companyName", rs.getString("company_name"));
                result.put("companyAddress", rs.getString("company_address"));

                return Optional.of(result);
            }
        } catch (SQLException e) {
            System.out.println("조회 오류: " + e.getMessage());
        }
        return Optional.empty();
    }

    // 팝업 이름으로 조회
    public Optional<List<Map<String, Object>>> findPopupByName(String popupName) {
        String sql = "select * from DB2025_POPUP_MANAGEMENT where popup_name = ?"; // DB2025_POPUP_NAME_INDEX 사용
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, popupName);
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> popup = new HashMap<>();
                popup.put("popupId", rs.getInt("id"));
                popup.put("popupName", rs.getString("name"));
                popup.put("popupAddress", rs.getString("address"));
                popup.put("startDate", rs.getDate("start_date").toLocalDate());
                popup.put("endDate", rs.getDate("end_date").toLocalDate());
                results.add(popup);
            }
            return Optional.of(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // user id로 조회
    public List<PopupManagement> findPopupByUserId(int userId) {
        String sql = "select * from DB2025_POPUP_MANAGEMENT where user_id = ?";
        List<PopupManagement> popups = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PopupManagement popup = new PopupManagement();
                popup.setPopupId(rs.getInt("id"));
                popup.setName(rs.getString("name"));
                popup.setAddress(rs.getString("address"));
                popup.setStartDate(rs.getDate("start_date").toLocalDate());
                popup.setEndDate(rs.getDate("end_date").toLocalDate());
                popup.setUserId(rs.getInt("user_id"));
                popups.add(popup);
            }
            return popups;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // search by producer email
    public Optional<List<Map<String, Object>>> findPopupsByProducerEmail(String email) {
        String sql = "select pm.* FROM DB2025_POPUP_MANAGEMENT pm join DB2025_USER u on pm.user_id = u.id where u.email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> popup = new HashMap<>();
                popup.put("popupId", rs.getInt("id"));
                popup.put("popupName", rs.getString("name"));
                popup.put("popupAddress", rs.getString("address"));
                popup.put("startDate", rs.getDate("start_date").toLocalDate());
                popup.put("endDate", rs.getDate("end_date").toLocalDate());
                results.add(popup);
            }
            return Optional.of(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // delete
    public boolean deletePopup(PopupManagement popup) throws SQLException {
        String sql = "delete FROM DB2025_POPUP_MANAGEMENT WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, popup.getPopupId());
            return pstmt.executeUpdate() > 0;
        }
    }

    // 동적 쿼리 이용해 수정사항 있는 필드만 update
    public boolean updatePopup(PopupManagement popup) throws SQLException {
        StringBuilder sql = new StringBuilder("update DB2025_POPUP_MANAGEMENT set ");
        List<Object> params = new ArrayList<>();

        if(popup.getName() != null){
            sql.append("name = ?, ");
            params.add(popup.getName());
        }
        if (popup.getAddress() != null && !popup.getAddress().isBlank()) {
            sql.append("address = ?, ");
            params.add(popup.getAddress());
        }
        if (popup.getStartDate() != null) {
            sql.append("start_date = ?, ");
            params.add(java.sql.Date.valueOf(popup.getStartDate()));
        }
        if (popup.getEndDate() != null) {
            sql.append("end_date = ?, ");
            params.add(java.sql.Date.valueOf(popup.getEndDate()));
        }
        if (params.isEmpty()) {
            System.out.println("수정할 필드가 없습니다.");
            return false;
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(popup.getPopupId());

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Optional<List<PopupManagement>> findAllPopupStores() {
        List<PopupManagement> popups = new ArrayList<>();
        String sql = "SELECT * FROM DB2025_POPUP_MANAGEMENT";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PopupManagement popup = new PopupManagement();
                popup.setPopupId(rs.getInt("id"));
                popup.setName(rs.getString("name"));
                popup.setAddress(rs.getString("address"));
                popup.setStartDate(rs.getDate("start_date").toLocalDate());
                popup.setEndDate(rs.getDate("end_date").toLocalDate());
                popup.setUserId(rs.getInt("user_id"));
                popups.add(popup);
            }

            return Optional.of(popups); // empty list도 감싸서 Optional로 반환

        } catch (SQLException e) {
            System.out.println("팝업스토어 전체 조회 중 오류 발생: " + e.getMessage());
        }

        return Optional.empty(); // 오류 발생 시
    }


}