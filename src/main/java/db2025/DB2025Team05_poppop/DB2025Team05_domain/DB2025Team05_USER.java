package db2025.DB2025Team05_poppop.DB2025Team05_domain;

import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DB2025Team05_USER {
    private int id;
    private String name;
    private Role role;
    private String email;

    public DB2025Team05_USER(int id, String name, String role, String email) {
        this.id = id;
        this.name = name;
        this.role = Role.fromString(role);
        this.email = email;
    }
}