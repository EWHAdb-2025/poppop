package db2025.DB2025Team05_poppop.DB2025Team05_domain;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DB2025_POPUP_MANAGEMENT {
    private int id;
    private int userId;
    private String name;
    private String address;
    private LocalDate startDate;
    private LocalDate endDate;

    public DB2025_POPUP_MANAGEMENT(int id, int userId, String address, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.userId = userId;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}