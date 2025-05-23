package db2025.DB2025Team05_poppop.DB2025Team05_domain;

import lombok.*;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfo {
    private int id;
    private int userId;
    private String companyName;
    private String businessNumber;
    private String representativeName;
    private String representativePhone;
    private String address;
}