package db2025.DB2025Team05_poppop.DB2025Team05_repository;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_POPUP_MANAGEMENT;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_USER;
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
    public DB2025_POPUP_MANAGEMENT insertPopup(DB2025_POPUP_MANAGEMENT popup) throws SQLException {
        String sql = "INSERT INTO DB2025_POPUP_MANAGEMENT (user_id, name, address, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
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
                    popup.setId(generatedKeys.getInt(1));
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
    public boolean deletePopup(int id) throws SQLException {
        String sql = "DELETE FROM DB2025_POPUP_MANAGEMENT WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    // update (동적 쿼리 구현)
    public boolean updatePopup(DB2025_POPUP_MANAGEMENT popup) throws SQLException {
        String sql = "UPDATE DB2025_POPUP_MANAGEMENT SET name = ?, address = ?, start_date = ?, end_date = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, popup.getName());
            pstmt.setString(2, popup.getAddress());
            pstmt.setDate(3, java.sql.Date.valueOf(popup.getStartDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(popup.getEndDate()));
            pstmt.setInt(5, popup.getId());
            
            return pstmt.executeUpdate() > 0;
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

    public Optional<DB2025_POPUP_MANAGEMENT> findById(int id) throws SQLException {
        String sql = "SELECT * FROM DB2025_POPUP_MANAGEMENT WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPopup(rs));
                }
            }
        }
        return Optional.empty();
    }

    private DB2025_POPUP_MANAGEMENT mapResultSetToPopup(ResultSet rs) throws SQLException {
        DB2025_POPUP_MANAGEMENT popup = new DB2025_POPUP_MANAGEMENT();
        popup.setId(rs.getInt("id"));
        popup.setUserId(rs.getInt("user_id"));
        popup.setName(rs.getString("name"));
        popup.setAddress(rs.getString("address"));
        popup.setStartDate(rs.getDate("start_date").toLocalDate());
        popup.setEndDate(rs.getDate("end_date").toLocalDate());
        return popup;
    }

    /**
     * 팝업스토어 ID로 존재 여부 확인
     * @param popupId 확인할 팝업스토어 ID
     * @return 존재 여부
     * @throws SQLException 데이터베이스 오류 발생 시
     */
    public boolean existsById(int popupId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DB2025_POPUP_MANAGEMENT WHERE popup_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, popupId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}

