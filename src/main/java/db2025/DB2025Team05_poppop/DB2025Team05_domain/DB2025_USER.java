package db2025.DB2025Team05_poppop.DB2025Team05_domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DB2025_USER {
    private int id;
    private String name;
    private Role role;
    private String email;
}