package db2025.DB2025Team05_poppop.DB2025Team05_domain;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopupManagement {
    private int popupId;
    private int userId;
    private String name;
    private String address;
    private LocalDate startDate;
    private LocalDate endDate;

    public PopupManagement(int popupId, int userId, String address, LocalDate startDate, LocalDate endDate) {
        this.popupId = popupId;
        this.userId = userId;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}