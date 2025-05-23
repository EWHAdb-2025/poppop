package poppop.src.DB2025Team05_poppop.DB2025Team05_domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "DB2025_POPUP_MANAGEMENT")
public class DB2025_POPUP_MANAGEMENT {
    @Id
    @Column(name = "popup_id", nullable = false)
    private int popupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private DB2025_USER userId;

    @Column(name = "popup_start_date")
    private LocalDate startDate;

    @Column(name = "popup_end_date")
    private LocalDate endDate;

    @Column(name = "popup_address", length = 50, nullable = false)
    private String address;

    @Column(name = "popup_name", length = 50)
    private String name;

}