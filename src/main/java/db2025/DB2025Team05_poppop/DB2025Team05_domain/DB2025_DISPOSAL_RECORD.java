package poppop.src.DB2025Team05_poppop.DB2025Team05_domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "DB2025_DISPOSAL_RECORD")
public class DB2025_DISPOSAL_RECORD {

    @Id
    @Column(name = "disposal_record_id", nullable = false)
    private int disposalRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private DB2025_USER userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private DB2025_POPUP_MANAGEMENT popupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waste_id")
    private DB2025_WASTE wasteId;

    @Column(name = "status")
    private String status;

    @Column(name = "remover_date")
    private LocalDateTime removerDate;
}