package poppop.src.DB2025Team05_poppop.DB2025Team05_repository;

import poppop.src.DB2025Team05_poppop.DB2025Team05_repository.DBConnection;
import poppop.src.DB2025Team05_poppop.DB2025Team05_domain.DB2025_DISPOSAL_RECORD;

import java.sql.*;
import java.util.*;

public class DispRecRepository {
    private final Connection conn;

    public DispRecRepository() throws SQLException{
        this.conn = DBConnection.getConnection();
    }

    // insert
    public boolean insertDisRec(DB2025_DISPOSAL_RECORD dr){
        String sql = "insert INTO DB2025_DISPOSAL_RECORD(disposal_record_id, user_id, popup_id, waste_id, status, remover_date) values (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, dr.getDisposalRecordId());
            pstmt.setInt(2, dr.getUserId().getId());
            pstmt.setInt(3, dr.getPopupId().getPopupId());
            pstmt.setInt(4, dr.getWasteId().getId());
            pstmt.setString(5, dr.getStatus());
            pstmt.setTimestamp(6, Timestamp.valueOf(dr.getRemoverDate()));
            pstmt.executeUpdate();
            return true;
        } catch(SQLException e){
            System.out.println("오류 발생: "+e.getMessage());
            return false;
        }
    }

    // search using view
    public Optional<Map<String, Object>> findDisRecByDisRecId(int disRecId){
        String sql = "select * from DB2025_DISPOSAL_COMPANY_VIEW where disposal_record_id=?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, disRecId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                Map<String, Object> result = new HashMap<>();
                result.put("disposalRecordId", rs.getInt("disposal_record_id"));
                result.put("userId", rs.getInt("user_id"));
                result.put("userName", rs.getString("user_name"));
                result.put("companyName", rs.getString("company_name"));
                result.put("popupId", rs.getInt("popup_id"));
                result.put("status", rs.getString("status"));

                Timestamp ts = rs.getTimestamp("remover_date");
                result.put("removerDate", ts != null ? ts.toLocalDateTime() : null);

                return Optional.of(result);
            }
        } catch(SQLException e){
            System.out.println("조회 오류: "+e.getMessage());
        }
        return Optional.empty();
    }

    // search by month
    public Optional<List<Map<String, Object>>> findDisposalRecordsByMonth(int year, int month) {
        String sql = """
            SELECT * FROM DB2025_DISPOSAL_RECORD
            WHERE YEAR(remover_date) = ? AND MONTH(remover_date) = ?
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("disposalRecordId", rs.getInt("disposal_record_id"));
                record.put("popupId", rs.getInt("popup_id"));
                record.put("status", rs.getString("status"));
                record.put("removerDate", rs.getTimestamp("remover_date").toLocalDateTime());
                results.add(record);
            }
            return Optional.of(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    

    // delete
    public boolean deleteDisRec(int disRecId){
        String sql = "delete from DB2025_DISPOSAL_RECORD where disposal_record_id=?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, disRecId);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch(SQLException e){
            System.out.println("삭제 실패: "+e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // update (dynamic queary)
    public boolean updateDisposalRecord(DB2025_DISPOSAL_RECORD dr) {
        StringBuilder sql = new StringBuilder("UPDATE DB2025_DISPOSAL_RECORD SET ");
        List<Object> params = new ArrayList<>();
    
        if (dr.getUserId() != null) {
            sql.append("user_id = ?, ");
            params.add(dr.getUserId().getId());
        }
        if (dr.getPopupId() != null) {
            sql.append("popup_id = ?, ");
            params.add(dr.getPopupId().getPopupId());
        }
        if (dr.getWasteId() != null) {
            sql.append("waste_id = ?, ");
            params.add(dr.getWasteId().getId());
        }
        if (dr.getStatus() != null && !dr.getStatus().isBlank()) {
            sql.append("status = ?, ");
            params.add(dr.getStatus());
        }
        if (dr.getRemoverDate() != null) {
            sql.append("remover_date = ?, ");
            params.add(Timestamp.valueOf(dr.getRemoverDate()));
        }
        if (params.isEmpty()) {
            System.out.println("수정할 필드가 없습니다.");
            return false;
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE disposal_record_id = ?");
        params.add(dr.getDisposalRecordId());
    
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
