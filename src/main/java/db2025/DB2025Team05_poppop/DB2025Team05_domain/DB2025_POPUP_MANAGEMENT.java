package db2025.DB2025Team05_poppop.DB2025Team05_domain;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DB2025_POPUP_MANAGEMENT {
    private int popupId;
    private int userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String address;
    private String name;
}