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
    public boolean insertWaste(Waste waste) {
        String sql = "insert into Waste(waste_id, waste_amount, waste_type) values (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, waste.getId());
            if (waste.getAmount() != null) {
                pstmt.setInt(2, waste.getAmount());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, waste.getType());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("폐기물 삽입 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // search
    public Optional<Waste> findByWasteId(int id) {
        String sql = "select * from Waste where waste_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Waste waste = Waste.builder()
                    .id(rs.getInt("waste_id"))
                    .amount(rs.getInt("waste_amount"))
                    .type(rs.getString("waste_type"))
                    .build();
                return Optional.of(waste);
            }
        } catch (SQLException e) {
            System.out.println("폐기물 조회 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // search total amount
    public Optional<Map<String, Integer>> getTotalWasteAmountByType() {
        String sql = """
            SELECT waste_type, SUM(waste_amount) AS total_amount
            FROM Waste
            GROUP BY waste_type
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            Map<String, Integer> result = new HashMap<>();
            while (rs.next()) {
                result.put(rs.getString("waste_type"), rs.getInt("total_amount"));
            }
            return Optional.of(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // delete
    public boolean deleteWaste(int id) {
        String sql = "delete from Waste where waste_id = ?";
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
        StringBuilder sql = new StringBuilder("update Waste set ");
        List<Object> params = new ArrayList<>();

        if (waste.getAmount() != null) {
            sql.append("waste_amount = ?, ");
            params.add(waste.getAmount());
        }
        if (waste.getType() != null && !waste.getType().isBlank()) {
            sql.append("waste_type = ?, ");
            params.add(waste.getType());
        }

        if (params.isEmpty()) {
            System.out.println("수정할 필드가 없습니다.");
            return false;
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE waste_id = ?");
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
