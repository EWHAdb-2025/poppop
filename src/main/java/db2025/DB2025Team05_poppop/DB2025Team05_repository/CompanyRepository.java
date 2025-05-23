package db2025.DB2025Team05_poppop.DB2025Team05_repository;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.CompanyInfo;
import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;

import java.sql.*;
import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class CompanyRepository {
    private final Connection conn;

    public CompanyRepository() throws SQLException {
        this.conn = DBConnection.getConnection();
    }

    public boolean insertCompanyInfo(int userId, String companyName, String businessNumber, 
                                   String representativeName, String representativePhone) {
        String sql = "insert INTO CompanyInfo(user_id, company_name, business_number, " +
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
        String sql = "select count(*) from CompanyInfo where business_number = ?";
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
        String sql = "select * from CompanyInfo where user_id = ?";
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
        String sql = "update CompanyInfo set company_name = ?, business_number = ?, " +
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

    /**
     * 회사 정보 저장
     * @param companyInfo 저장할 회사 정보
     * @return 저장된 회사 정보 (실패 시 null)
     * @throws SQLException 데이터베이스 오류 발생 시
     */
    public CompanyInfo insertCompanyInfo(CompanyInfo companyInfo) throws SQLException {
        String sql = "INSERT INTO CompanyInfo (user_id, company_name, business_number, representative_name, representative_phone, address) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyInfo.getUserId());
            pstmt.setString(2, companyInfo.getCompanyName());
            pstmt.setString(3, companyInfo.getBusinessNumber());
            pstmt.setString(4, companyInfo.getRepresentativeName());
            pstmt.setString(5, companyInfo.getRepresentativePhone());
            pstmt.setString(6, companyInfo.getAddress());
            
            boolean success = pstmt.executeUpdate() > 0;
            return success ? companyInfo : null;
        }
    }

    public CompanyInfo findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM CompanyInfo WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCompany(rs);
                }
            }
        }
        return null;
    }

    public boolean updateCompany(CompanyInfo company) throws SQLException {
        String sql = "UPDATE CompanyInfo SET company_name = ?, business_number = ?, representative_name = ?, representative_phone = ?, address = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, company.getCompanyName());
            pstmt.setString(2, company.getBusinessNumber());
            pstmt.setString(3, company.getRepresentativeName());
            pstmt.setString(4, company.getRepresentativePhone());
            pstmt.setString(5, company.getAddress());
            pstmt.setInt(6, company.getUserId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCompany(int userId) throws SQLException {
        String sql = "DELETE FROM CompanyInfo WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private CompanyInfo mapResultSetToCompany(ResultSet rs) throws SQLException {
        CompanyInfo company = new CompanyInfo();
        company.setId(rs.getInt("id"));
        company.setUserId(rs.getInt("user_id"));
        company.setCompanyName(rs.getString("company_name"));
        company.setBusinessNumber(rs.getString("business_number"));
        company.setRepresentativeName(rs.getString("representative_name"));
        company.setRepresentativePhone(rs.getString("representative_phone"));
        company.setAddress(rs.getString("address"));
        return company;
    }
}
