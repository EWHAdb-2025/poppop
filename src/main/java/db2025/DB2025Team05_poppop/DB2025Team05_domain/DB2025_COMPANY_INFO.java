package poppop.src.DB2025Team05_poppop.DB2025Team05_domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "DB2025_COMPANY_INFO")
public class DB2025_COMPANY_INFO {

    @Id
    @Column(name = "business_id", length = 12, nullable = false)
    private int businessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private DB2025_USER userId;

    @Column(name = "company_name", length = 30, nullable = false)
    private String companyName;

    @Column(name = "ceo_name", length = 10, nullable = false)
    private String ceoName;

    @Column(name = "ceo_contact", length = 13, nullable = false)
    private String ceoPhoneNumber;
}