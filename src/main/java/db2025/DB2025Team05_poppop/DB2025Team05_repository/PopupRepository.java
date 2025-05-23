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
        String sql = "INSERT INTO PopupManagement (user_id, name, address, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
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
        String sql = "SELECT * FROM PopupManagement WHERE popup_name = ?";
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
            SELECT pm.* FROM PopupManagement pm
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
    public boolean deletePopup(int id) throws SQLException {
        String sql = "DELETE FROM PopupManagement WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    // update (동적 쿼리 구현)
    public boolean updatePopup(PopupManagement popup) throws SQLException {
        String sql = "UPDATE PopupManagement SET name = ?, address = ?, start_date = ?, end_date = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, popup.getName());
            pstmt.setString(2, popup.getAddress());
            pstmt.setDate(3, java.sql.Date.valueOf(popup.getStartDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(popup.getEndDate()));
            pstmt.setInt(5, popup.getPopupId());
            
            return pstmt.executeUpdate() > 0;
        }
    }


}

