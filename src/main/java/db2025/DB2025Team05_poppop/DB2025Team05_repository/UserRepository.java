package poppop.src.DB2025Team05_poppop.DB2025Team05_repository;

import poppop.src.DB2025Team05_poppop.DB2025Team05_repository.DBConnection;
import poppop.src.DB2025Team05_poppop.DB2025Team05_domain.DB2025_USER;

import java.sql.*;
import java.util.*;

public class UserRepository {
    private final Connection conn;

    public UserRepository(Connection conn) throws SQLException{
        this.conn = DBConnection.getConnection();
    }

    // email 중복 확인
    public boolean isEmailDuplicate(String email){
        String sql="select count (*) from DB2025_USER where email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch(SQLException e){
            System.out.println("이미 가입된 이메일입니다.");
        }
        return false;
    }

    // insert
    public boolean insertUser(DB2025_USER user){
        String sql = "insert INTO DB2025_USER(user_id, name, role, email) values (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getEmail());
            pstmt.executeUpdate();
            return true;
        } catch(SQLException e){
            System.out.println("오류 발생: "+e.getMessage());
            return false;
        }
    }

    // search by user id
    public Optional<DB2025_USER> findByUserId(int userId){
        String sql = "select * from DB2025_USER where user_id=?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                DB2025_USER user = new DB2025_USER(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("email")
                );
                return Optional.of(user);
            }
            if (!rs.next()) {
                System.out.println("사용자를 찾을 수 없습니다.");
            }
        } catch(SQLException e){
            System.out.println("오류: "+e.getMessage());
        }
        return Optional.empty();
    }

    // delete
    public boolean deleteUser(int userId){
        String sql = "delete from DB2025_USER where user_id=?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            return true;
        } catch(SQLException e){
            System.out.println("삭제 실패: "+e.getMessage());
            return false;
        }
    }

    // update (dynamic queary)
    public boolean updateUser(DB2025_USER user){
        StringBuilder sql = new StringBuilder("update DB2025_USER set ");
        List<Object> params = new ArrayList<>();

        if (user.getName() != null) {
            sql.append("name = ?, ");
            params.add(user.getName());
        }
        if (user.getRole() != null) {
            sql.append("role = ?, ");
            params.add(user.getRole());
        }
        if (user.getEmail() != null){
            sql.append("email = ?, ");
            params.add(user.getEmail());
        }
        if (params.isEmpty()) {
            System.out.println("수정할 필드가 없습니다.");
            return false;
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE user_id = ?");
        params.add(user.getId());

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
