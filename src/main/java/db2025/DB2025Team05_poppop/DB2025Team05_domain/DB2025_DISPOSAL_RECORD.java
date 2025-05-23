package db2025.DB2025Team05_poppop.DB2025Team05_domain;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DB2025_DISPOSAL_RECORD {
    private int disposalId;
    private int userId;
    private int popupId;
    private int wasteId;
    private String status;
    private LocalDateTime disposalDate;
}