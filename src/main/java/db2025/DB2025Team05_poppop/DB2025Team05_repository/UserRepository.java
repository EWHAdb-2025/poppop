package db2025.DB2025Team05_poppop.DB2025Team05_repository;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import java.sql.*;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final Connection conn;

    public UserRepository() throws SQLException {
        this.conn = DBConnection.getConnection();
    }

    // email 중복 확인
    public boolean isEmailDuplicate(String email) {
        String sql = "select count(*) from DB2025_USER where email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("이메일 중복 확인 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    // insert
    public User insertUser(User user) throws SQLException {
        String sql = "INSERT INTO DB2025_USER (name, role, email) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getRole().toString());
            pstmt.setString(3, user.getEmail());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    return user;
                } else {
                    return null;
                }
            }
        }
    }

    // search by user id
    public Optional<User> findByUserId(int userId) {
        String sql = "select * from DB2025_USER where id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setRole(Role.valueOf(rs.getString("role").toUpperCase()));
                user.setEmail(rs.getString("email"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.out.println("사용자 조회 중 오류 발생: " + e.getMessage());
        }
        return Optional.empty();
    }

    // delete
    public boolean deleteUser(int userId) {
        String sql = "delete from DB2025_USER where id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("사용자 삭제 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    // update
    public boolean updateUser(User user) {
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
        if (user.getEmail() != null) {
            sql.append("email = ?, ");
            params.add(user.getEmail());
        }
        if (params.isEmpty()) {
            System.out.println("수정할 필드가 없습니다.");
            return false;
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(user.getId());

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("사용자 정보 수정 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    // 이메일 주소로 사용자 조회
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM DB2025_USER WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setRole(Role.fromString(rs.getString("role")));
        user.setEmail(rs.getString("email"));
        return user;
    }

    public Optional<List<User>> findAllByRole(Role role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM DB2025_USER WHERE role = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            return Optional.of(users); // 빈 리스트라도 감싸서 반환
        } catch (SQLException e) {
            System.out.println("역할별 사용자 조회 중 오류 발생: " + e.getMessage());
        }

        return Optional.empty(); // 예외 발생 시
    }


}
