package db2025.DB2025Team05_poppop.DB2025Team05_domain;

import lombok.*;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_USER;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DB2025_COMPANY_INFO {
    private int businessId;
    private DB2025_USER userId;
    private String companyName;
    private String ceoName;
    private String ceoPhoneNumber;
}