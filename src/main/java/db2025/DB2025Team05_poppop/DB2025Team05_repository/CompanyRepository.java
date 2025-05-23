package db2025.DB2025Team05_poppop.DB2025Team05_repository;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_COMPANY_INFO;
import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;

import java.sql.*;
import java.util.*;

public class CompanyRepository {
    private final Connection conn;

    public CompanyRepository(Connection conn) throws SQLException {
        this.conn = conn;
    }

    public boolean insertCompanyInfo(int userId, String companyName, String businessNumber, 
                                   String representativeName, String representativePhone) {
        String sql = "insert INTO DB2025_COMPANY_INFO(user_id, company_name, business_number, " +
                    "representative_name, representative_phone) values (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, companyName);
            pstmt.setString(3, businessNumber);
            pstmt.setString(4, representativeName);
            pstmt.setString(5, representativePhone);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("회사 정보 등록 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    public boolean isBusinessNumberDuplicate(String businessNumber) {
        String sql = "select count(*) from DB2025_COMPANY_INFO where business_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, businessNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("사업자번호 중복 확인 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    public Optional<Map<String, Object>> findCompanyByUserId(int userId) {
        String sql = "select * from DB2025_COMPANY_INFO where user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> company = new HashMap<>();
                company.put("userId", rs.getInt("user_id"));
                company.put("companyName", rs.getString("company_name"));
                company.put("businessNumber", rs.getString("business_number"));
                company.put("representativeName", rs.getString("representative_name"));
                company.put("representativePhone", rs.getString("representative_phone"));
                return Optional.of(company);
            }
        } catch (SQLException e) {
            System.out.println("회사 정보 조회 중 오류 발생: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean updateCompanyInfo(int userId, String companyName, String businessNumber,
                                   String representativeName, String representativePhone) {
        String sql = "update DB2025_COMPANY_INFO set company_name = ?, business_number = ?, " +
                    "representative_name = ?, representative_phone = ? where user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, companyName);
            pstmt.setString(2, businessNumber);
            pstmt.setString(3, representativeName);
            pstmt.setString(4, representativePhone);
            pstmt.setInt(5, userId);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("회사 정보 수정 중 오류 발생: " + e.getMessage());
            return false;
        }
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
