package poppop.src.DB2025Team05_poppop.DB2025Team05_repository;

import poppop.src.DB2025Team05_poppop.DB2025Team05_repository.DBConnection;
import poppop.src.DB2025Team05_poppop.DB2025Team05_domain.DB2025_COMPANY_INFO;

import java.sql.*;
import java.util.*;

public class CompanyRepository {
    private final Connection conn;

    public CompanyRepository() throws SQLException{
        this.conn = DBConnection.getConnection();
    }

    // insert
    public boolean insertCompany(DB2025_COMPANY_INFO comp){
        String sql = "insert INTO DB2025_COMPANY_INFO(business_id, user_id, company_name, ceo_name, ceo_varchar) values (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, comp.getBusinessId());
            pstmt.setInt(2, comp.getUserId().getId());
            pstmt.setString(3, comp.getCompanyName());
            pstmt.setString(4, comp.getCeoName());
            pstmt.setString(5, comp.getCeoPhoneNumber());
            pstmt.executeUpdate();
            return true;
        } catch(SQLException e){
            System.out.println("오류 발생: "+e.getMessage());
            return false;
        }
    }

    // search (dynamic query) by user id
    public Optional<DB2025_COMPANY_INFO> findByUserId(int userId){
        String sql = "select * from DB2025_COMPANY_INFO where user_id=?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                DB2025_COMPANY_INFO comp = DB2025_COMPANY_INFO.builder()
                    .businessId(rs.getInt("business_id"))
                    .companyName(rs.getString("company_name"))
                    .ceoName(rs.getString("ceo_name"))
                    .ceoPhoneNumber(rs.getString("ceo_varchar"))
                    .build();
                return Optional.of(comp);
            }
        } catch(SQLException e){
            System.out.println("조회 오류: "+e.getMessage());
        }
        return Optional.empty();
    }

    // delete
    public boolean deleteCompany(int businessId){
        String sql = "delete from DB2025_COMPANY_INFO where business_id=?";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, businessId);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch(SQLException e){
            System.out.println("삭제 실패: "+e.getMessage());
            return false;
        }
    }

    // update (dynamic queary)
    public boolean updateCompany(DB2025_COMPANY_INFO comp){
        StringBuilder sql = new StringBuilder("update DB2025_COMPANY_INFO set ");
        List<Object> params = new ArrayList<>();

        if (comp.getCompanyName() != null){
            sql.append("company_name = ?, ");
            params.add(comp.getCompanyName());
        }
        if (comp.getCeoName() != null){
            sql.append("ceo_name = ?, ");
            params.add(comp.getCeoName());
        }
        if (comp.getCeoPhoneNumber() != null){
            sql.append("company_contact = ?, ");
            params.add(comp.getCeoPhoneNumber());
        }
        if (params.isEmpty()) {
            System.out.println("수정할 필드가 없습니다.");
            return false;
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE business_id = ?");
        params.add(comp.getBusinessId());

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
