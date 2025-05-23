package db2025.DB2025Team05_poppop.DB2025Team05_repository;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_DISPOSAL_RECORD;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_USER;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_POPUP_MANAGEMENT;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_WASTE;
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
    public boolean insertDisRec(DB2025_DISPOSAL_RECORD dr) {
        String sql = "insert INTO DB2025_DISPOSAL_RECORD(disposal_id, user_id, popup_id, waste_id, status, disposal_date) values (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dr.getDisposalId());
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

    // search using view
    public Optional<Map<String, Object>> findDisRecByDisRecId(int disRecId) {
        String sql = "select * from DB2025_DISPOSAL_COMPANY_VIEW where disposal_id=?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, disRecId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> result = new HashMap<>();
                result.put("disposalId", rs.getInt("disposal_id"));
                result.put("userId", rs.getInt("user_id"));
                result.put("userName", rs.getString("user_name"));
                result.put("companyName", rs.getString("company_name"));
                result.put("popupId", rs.getInt("popup_id"));
                result.put("status", rs.getString("status"));

                Timestamp ts = rs.getTimestamp("disposal_date");
                result.put("disposalDate", ts != null ? ts.toLocalDateTime() : null);

                return Optional.of(result);
            }
        } catch(SQLException e) {
            System.out.println("조회 오류: "+e.getMessage());
        }
        return Optional.empty();
    }

    // search by month
    public Optional<List<Map<String, Object>>> findDisposalRecordsByMonth(int year, int month) {
        String sql = """
            SELECT * FROM DB2025_DISPOSAL_RECORD
            WHERE YEAR(disposal_date) = ? AND MONTH(disposal_date) = ?
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("disposalId", rs.getInt("disposal_id"));
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
        String sql = "delete from DB2025_DISPOSAL_RECORD where disposal_id=?";

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

    // update (dynamic query)
    public boolean updateDisposalRecord(DB2025_DISPOSAL_RECORD dr) {
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
        sql.append(" WHERE disposal_id = ?");
        params.add(dr.getDisposalId());
    
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

    public boolean insertDisposalRecord(DB2025_DISPOSAL_RECORD record) {
        String sql = "insert INTO DB2025_DISPOSAL_RECORD(disposal_id, user_id, popup_id, status) " +
                    "values (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, record.getDisposalId());
            pstmt.setInt(2, record.getUserId());
            pstmt.setInt(3, record.getPopupId());
            pstmt.setString(4, record.getStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("폐기물 처리 이력 등록 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    public List<Map<String, Object>> getDisposalStatisticsByCompany() {
        List<Map<String, Object>> statistics = new ArrayList<>();
        String sql = "select * from DB2025_Disposal_Company_View";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("companyName", rs.getString("company_name"));
                stat.put("totalDisposal", rs.getInt("total_disposal"));
                statistics.add(stat);
            }
        } catch (SQLException e) {
            System.out.println("업체별 통계 조회 중 오류 발생: " + e.getMessage());
        }
        return statistics;
    }

    public List<Map<String, Object>> getDisposalStatisticsByPopup() {
        List<Map<String, Object>> statistics = new ArrayList<>();
        String sql = "select * from DB2025_Popup_Company_View";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("popupId", rs.getInt("popup_id"));
                stat.put("totalDisposal", rs.getInt("total_disposal"));
                statistics.add(stat);
            }
        } catch (SQLException e) {
            System.out.println("팝업별 통계 조회 중 오류 발생: " + e.getMessage());
        }
        return statistics;
    }

    public List<Map<String, Object>> getDisposalStatisticsByMonth() {
        List<Map<String, Object>> statistics = new ArrayList<>();
        String sql = "select DATE_FORMAT(disposal_date, '%Y-%m') as month, " +
                    "count(*) as total_disposal from DB2025_DISPOSAL_RECORD " +
                    "group by DATE_FORMAT(disposal_date, '%Y-%m')";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("month", rs.getString("month"));
                stat.put("totalDisposal", rs.getInt("total_disposal"));
                statistics.add(stat);
            }
        } catch (SQLException e) {
            System.out.println("월별 통계 조회 중 오류 발생: " + e.getMessage());
        }
        return statistics;
    }
}
