package poppop.src.DB2025Team05_poppop.DB2025Team05_domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "DB2025_WASTE")
public class DB2025_WASTE {

    @Id
    @Column(name = "waste_id", nullable = false)
    private int id;

    @Column(name = "waste_amount")
    private Integer amount;

    @Column(name = "waste_type")
    private String type;
}