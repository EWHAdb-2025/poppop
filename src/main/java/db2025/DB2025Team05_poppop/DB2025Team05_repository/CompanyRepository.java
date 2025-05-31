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

    // insert
    public boolean insertCompanyInfo(int userId, String companyName, String businessNumber, String representativeName, String representativePhone) {
        String sql = "insert INTO DB2025_COMPANY_INFO(user_id, company_name, business_number, representative_name, representative_phone) values (?, ?, ?, ?, ?)";
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

    // 사업자번호 중복 확인
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

    // userid로 검색
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

    // company name으로 user_id 조회
    public Integer findUserIdByCompanyName(String companyName) {
        String sql = "SELECT user_id FROM DB2025_COMPANY_INFO WHERE company_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, companyName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.out.println("회사 정보 조회 중 오류 발생: " + e.getMessage());
        }
        return null; // 조회 실패 시 null 반환
    }



    // 동적 쿼리 사용해 수정된 필드만 update
    public boolean updateCompanyInfo(CompanyInfo comp) {
        StringBuilder sql = new StringBuilder("update DB2025_COMPANY_INFO set ");
        List<Object> params = new ArrayList<>();

        if (comp.getCompanyName() != null) {
            sql.append("company_name = ?, ");
            params.add(comp.getCompanyName());
        }
        if (comp.getAddress() !=null){
            sql.append("address = ?,");
            params.add(comp.getAddress());
        }
        if (comp.getBusinessNumber() != null) {
            sql.append("business_number = ?, ");
            params.add(comp.getBusinessNumber());
        }
        if (comp.getRepresentativeName() != null) {
            sql.append("representative_name = ?, ");
            params.add(comp.getRepresentativeName());
        }
        if(comp.getRepresentativePhone() !=null){
            sql.append("representative_phone = ?, ");
            params.add(comp.getRepresentativePhone());
        }
        if (params.isEmpty()) {
            System.out.println("수정할 필드가 없습니다.");
            return false;
        }

        sql.setLength(sql.length() - 2);
        sql.append("where id = ?");
        params.add(comp.getId());

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("회사 정보 수정 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    public Optional<List<CompanyInfo>> findAllProcessorCompanies() {
        List<CompanyInfo> companies = new ArrayList<>();
        String sql = "SELECT * FROM DB2025_COMPANY_INFO WHERE user_id IN (SELECT id FROM DB2025_USER WHERE role = 'PROCESSOR')";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                CompanyInfo info = new CompanyInfo();
                info.setId(rs.getInt("id"));
                info.setUserId(rs.getInt("user_id"));
                info.setBusinessNumber(rs.getString("business_number"));
                info.setCompanyName(rs.getString("company_name"));
                info.setRepresentativeName(rs.getString("representative_name"));
                info.setRepresentativePhone(rs.getString("representative_phone"));
                info.setAddress(rs.getString("address"));
                companies.add(info);
            }

            return Optional.of(companies); // 빈 리스트라도 감싸서 반환

        } catch (SQLException e) {
            System.out.println("처리업체 회사 목록 조회 중 오류 발생: " + e.getMessage());
        }

        return Optional.empty(); // 예외 발생 시
    }


}
