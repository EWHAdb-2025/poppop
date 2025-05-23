package poppop.src.DB2025Team05_poppop.DB2025Team05_repository;

import poppop.src.DB2025Team05_poppop.DB2025Team05_repository.DBConnection;
import poppop.src.DB2025Team05_poppop.DB2025Team05_domain.DB2025_POPUP_MANAGEMENT;

import java.sql.*;
import java.util.*;

public class PopupRepository {
    private final Connection conn;

    public PopupRepository() throws SQLException{
        this.conn = DBConnection.getConnection();
    }

    // insert
    public boolean insertPopup(DB2025_POPUP_MANAGEMENT popup){
        String sql = "insert into DB2025_POPUP_MANAGEMENT(popup_id, user_id, popup_start_date, popup_end_date, popup_address, popup_name) values (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, popup.getPopupId());
            pstmt.setInt(2, popup.getUserId().getId());
            pstmt.setDate(3, java.sql.Date.valueOf(popup.getStartDate()));
            pstmt.setDate(4, java.sql.Date.valueOf(popup.getEndDate()));
            pstmt.setString(5, popup.getAddress());
            pstmt.setString(6, popup.getName());
            pstmt.executeUpdate();
            return true;
        } catch(SQLException e){
            System.out.println("팝업 삽입 오류: "+e.getMessage());
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
    public boolean updatePopup(DB2025_POPUP_MANAGEMENT popup){
        StringBuilder sql = new StringBuilder("update DB2025_POPUP_MANAGEMENT set ");
        List<Object> params = new ArrayList<>();

        if (popup.getAddress() != null && !popup.getAddress().isBlank()) {
            sql.append("popup_address = ?, ");
            params.add(popup.getAddress());
        }
        if (popup.getStartDate() != null) {
            sql.append("popup_start_date = ?, ");
            params.add(java.sql.Date.valueOf(popup.getStartDate()));
        }
        if (popup.getEndDate() != null) {
            sql.append("popup_end_date = ?, ");
            params.add(java.sql.Date.valueOf(popup.getEndDate()));
        }
        if (popup.getName() != null && !popup.getName().isBlank()) {
            sql.append("popup_name = ?, ");
            params.add(popup.getName());
        }
        if (params.isEmpty()) {
            System.out.println("수정할 필드가 없습니다.");
            return false;
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE popup_id = ?");
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
}

