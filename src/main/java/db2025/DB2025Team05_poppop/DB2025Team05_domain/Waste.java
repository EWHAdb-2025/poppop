package db2025.DB2025Team05_poppop.DB2025Team05_domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class Waste {
    private int id;
    private Integer amount;
    private String type;
}