package db2025.DB2025Team05_poppop.DB2025Team05_repository;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.DisposalRecord;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;  
import db2025.DB2025Team05_poppop.DB2025Team05_domain.Waste;
import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
import java.sql.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class DispRecRepository {
    private final Connection conn;

    public DispRecRepository() throws SQLException {
        this.conn = DBConnection.getConnection();
    }

    public DispRecRepository(Connection conn) {
        this.conn = conn;
    }

    // insert
    public boolean insertDisposalRecord(DisposalRecord dr) {
        String sql = "insert INTO DB2025_DISPOSAL_RECORD(id, user_id, popup_id, waste_id, status, disposal_date) values (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dr.getId());
            pstmt.setInt(2, dr.getUserId());
            pstmt.setInt(3, dr.getPopupId());
            pstmt.setInt(4, dr.getWasteId());
            pstmt.setString(5, dr.getStatus());
            pstmt.setTimestamp(6, Timestamp.valueOf(dr.getDisposalDate()));
            pstmt.executeUpdate();
            return true;
        } catch(SQLException e) {
            System.out.println("오류 발생: "+e.getMessage());
            return false;
        }
    }

    // 기간으로 폐기물 처리 정보 조회 (index 사용)
    public Optional<List<Map<String, Object>>> getDisposalStatisticsByMonth(int year, int month) {
        String sql = "select * from DB2025_DISPOSAL_RECORD where YEAR(disposal_date) = ? AND MONTH(disposal_date) = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", rs.getInt("id"));
                record.put("popupId", rs.getInt("popup_id"));
                record.put("status", rs.getString("status"));
                record.put("disposalDate", rs.getTimestamp("disposal_date").toLocalDateTime());
                results.add(record);
            }
            return Optional.of(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // delete
    public boolean deleteDisRec(int disRecId) {
        String sql = "delete from DB2025_DISPOSAL_RECORD where id=?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, disRecId);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch(SQLException e) {
            System.out.println("삭제 실패: "+e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 동적 쿼리 사용해 수정된 필드만 update
    public boolean updateDisposalRecord(DisposalRecord dr) {
        StringBuilder sql = new StringBuilder("UPDATE DB2025_DISPOSAL_RECORD SET ");
        List<Object> params = new ArrayList<>();
    
        if (dr.getUserId() != 0) {
            sql.append("user_id = ?, ");
            params.add(dr.getUserId());
        }
        if (dr.getPopupId() != 0) {
            sql.append("popup_id = ?, ");
            params.add(dr.getPopupId());
        }
        if (dr.getWasteId() != 0) {
            sql.append("waste_id = ?, ");
            params.add(dr.getWasteId());
        }
        if (dr.getStatus() != null && !dr.getStatus().isBlank()) {
            sql.append("status = ?, ");
            params.add(dr.getStatus());
        }
        if (dr.getDisposalDate() != null) {
            sql.append("disposal_date = ?, ");
            params.add(Timestamp.valueOf(dr.getDisposalDate()));
        }
        if (params.isEmpty()) {
            System.out.println("수정할 필드가 없습니다.");
            return false;
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(dr.getId());
    
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


    // DB2025_DISPOSAL_VIEW 이용해 팝업별 폐기물 처리 정보 조회
    public Optional<List<Map<String, Object>>> getDisposalStatisticsByPopupname(String popupName) {
        String sql = "select popup_name, type, amount, status, disposal_date from DB2025_DISPOSAL_VIEW where popup_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, popupName)
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("popupName", rs.getString("popup_name"));
                record.put("type", rs.getString("type"));
                record.put("amount", rs.getInt("amount"));
                record.put("status", rs.getString("status"));

                Timestamp ts = rs.getTimestamp("disposal_date");
                result.put("disposalDate", ts != null ? ts.toLocalDateTime() : null);

                results.add(record);
            }
            return Optional.of(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // DB2025_DISPOSAL_VIEW 이용해 회사별 폐기물 처리 정보 조회
    public Optional<List<Map<String, Object>>> getDisposalStatisticsByCompanyname(String companyName) {
        String sql = "select company_name, popup_name, type, amount, status, disposal date from DB2025_DISPOSAL_VIEW where company_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, popupName)
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("companyName", rs.getString("company_name"));
                record.put("popupName", rs.getString("popup_name"));
                record.put("type", rs.getString("type"));
                record.put("amount", rs.getInt("amount"));
                record.put("status", rs.getString("status"));

                Timestamp ts = rs.getTimestamp("disposal_date");
                result.put("disposalDate", ts != null ? ts.toLocalDateTime() : null);

                results.add(record);
            }
            return Optional.of(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
