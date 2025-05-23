package db2025.DB2025Team05_poppop.DB2025Team05_repository;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_POPUP_MANAGEMENT;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_USER;
import java.sql.*;
import java.util.*;

public class PopupRepository {
    private final Connection conn;

    public PopupRepository(Connection conn) throws SQLException {
        this.conn = conn;
    }

    // insert
    public boolean insertPopup(DB2025_POPUP_MANAGEMENT popup) {
        String sql = "insert INTO DB2025_POPUP_MANAGEMENT(popup_id, user_id, address, " +
                    "start_date, end_date) values (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, popup.getPopupId());
            pstmt.setInt(2, popup.getUserId());
            pstmt.setString(3, popup.getAddress());
            pstmt.setDate(4, Date.valueOf(popup.getStartDate()));
            pstmt.setDate(5, Date.valueOf(popup.getEndDate()));
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("팝업스토어 등록 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    // search with view
    public Optional<Map<String, Object>> findPopupById(int popupId) {
        String sql = "select * from DB2025_POPUP_COMPANY_VIEW where popup_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, popupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("popupId", rs.getInt("popup_id"));
                result.put("address", rs.getString("popup_address"));
                result.put("startDate", rs.getDate("popup_start_date").toLocalDate());
                result.put("endDate", rs.getDate("popup_end_date").toLocalDate());
                result.put("userName", rs.getString("user_name"));
                result.put("companyName", rs.getString("company_name"));
    
                return Optional.of(result);
            }
        } catch (SQLException e) {
            System.out.println("조회 오류: " + e.getMessage());
        }
        return Optional.empty();
    }

    // search by popup name
    public Optional<List<Map<String, Object>>> findPopupByName(String popupName) {
        String sql = "SELECT * FROM DB2025_POPUP_MANAGEMENT WHERE popup_name = ?";
        // 인덱스: idx_popup_name 사용 (popup_name 컬럼 조건 조회)
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, popupName);
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> popup = new HashMap<>();
                popup.put("popupId", rs.getInt("popup_id"));
                popup.put("popupName", rs.getString("popup_name"));
                popup.put("popupAddress", rs.getString("popup_address"));
                popup.put("startDate", rs.getDate("popup_start_date").toLocalDate());
                popup.put("endDate", rs.getDate("popup_end_date").toLocalDate());
                results.add(popup);
            }
            return Optional.of(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // search by producer email
    public Optional<List<Map<String, Object>>> findPopupsByProducerEmail(String email) {
        String sql = """
            SELECT pm.* FROM DB2025_POPUP_MANAGEMENT pm
            JOIN DB2025_USER u ON pm.user_id = u.user_id
            WHERE u.email = ?
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> popup = new HashMap<>();
                popup.put("popupId", rs.getInt("popup_id"));
                popup.put("popupName", rs.getString("popup_name"));
                popup.put("popupAddress", rs.getString("popup_address"));
                popup.put("startDate", rs.getDate("popup_start_date").toLocalDate());
                popup.put("endDate", rs.getDate("popup_end_date").toLocalDate());
                results.add(popup);
            }
            return Optional.of(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    
    
    // delete
    public boolean deletePopup(int popupId) {
        String sql = "delete from DB2025_POPUP_MANAGEMENT where popup_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, popupId);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.out.println("팝업 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    // update (동적 쿼리 구현)
    public boolean updatePopup(DB2025_POPUP_MANAGEMENT popup) {
        String sql = "update DB2025_POPUP_MANAGEMENT set address = ?, start_date = ?, " +
                    "end_date = ? where popup_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, popup.getAddress());
            pstmt.setDate(2, Date.valueOf(popup.getStartDate()));
            pstmt.setDate(3, Date.valueOf(popup.getEndDate()));
            pstmt.setInt(4, popup.getPopupId());
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("팝업스토어 수정 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    public Optional<DB2025_POPUP_MANAGEMENT> findByPopupId(int popupId) {
        String sql = "select * from DB2025_POPUP_MANAGEMENT where popup_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, popupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                DB2025_POPUP_MANAGEMENT popup = new DB2025_POPUP_MANAGEMENT(
                    rs.getInt("popup_id"),
                    rs.getInt("user_id"),
                    rs.getString("address"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate()
                );
                return Optional.of(popup);
            }
        } catch (SQLException e) {
            System.out.println("팝업스토어 조회 중 오류 발생: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<DB2025_POPUP_MANAGEMENT> findByUserId(int userId) {
        List<DB2025_POPUP_MANAGEMENT> popups = new ArrayList<>();
        String sql = "select * from DB2025_POPUP_MANAGEMENT where user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                DB2025_POPUP_MANAGEMENT popup = new DB2025_POPUP_MANAGEMENT(
                    rs.getInt("popup_id"),
                    rs.getInt("user_id"),
                    rs.getString("address"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate()
                );
                popups.add(popup);
            }
        } catch (SQLException e) {
            System.out.println("팝업스토어 목록 조회 중 오류 발생: " + e.getMessage());
        }
        return popups;
    }
}

