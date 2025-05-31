package db2025.DB2025Team05_poppop.DB2025Team05_repository;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.Waste;
import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
import java.sql.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class WasteRepository {
    private final Connection conn;

    public WasteRepository() throws SQLException {
        this.conn = DBConnection.getConnection();
    }

    // insert
    public Waste insertUser(Waste waste) throws SQLException {
        String sql = "insert into Waste(amount, type) values (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, waste.getAmount());
            pstmt.setString(2, waste.getType());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    waste.setId(generatedKeys.getInt(1));
                    return user;
                } else {
                    return null;
                }
            }
        }
    }

    // search
    public Optional<Waste> findByWasteId(int id) {
        String sql = "select * from Waste where id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Waste waste = Waste.builder()
                    .id(rs.getInt("id"))
                    .amount(rs.getInt("amount"))
                    .type(rs.getString("type"))
                    .build();
                return Optional.of(waste);
            }
        } catch (SQLException e) {
            System.out.println("폐기물 조회 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // 폐기물 type별 amount 조회
    public Optional<Map<String, Integer>> getTotalWasteAmountByType() {
        String sql = "select type, SUM(waste_amount) as total_amount from DB2025_WASTE group_by type";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            Map<String, Integer> result = new HashMap<>();
            while (rs.next()) {
                result.put(rs.getString("type"), rs.getInt("total_amount"));
            }
            return Optional.of(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // delete
    public boolean deleteWaste(int id) {
        String sql = "delete from Waste where id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.out.println("폐기물 삭제 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // update(dynamic query)
    public boolean updateWaste(Waste waste) {
        StringBuilder sql = new StringBuilder("update DB2025_WASTE set ");
        List<Object> params = new ArrayList<>();

        if (waste.getAmount() != null) {
            sql.append("amount = ?, ");
            params.add(waste.getAmount());
        }
        if (waste.getType() != null && !waste.getType().isBlank()) {
            sql.append("type = ?, ");
            params.add(waste.getType());
        }

        if (params.isEmpty()) {
            System.out.println("수정할 필드가 없습니다.");
            return false;
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(waste.getId());

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("폐기물 수정 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
