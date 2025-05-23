package poppop.src.DB2025Team05_poppop.DB2025Team05_domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "DB2025_USER")    
public class DB2025_USER {
    @Id
    @Column(name="user_id", nullable=false)
    private int id;
    
    @Column(name = "name", length = 10, nullable = false)
    private String name;

    @Column(name = "role", length = 9, nullable = false)
    private String role;

    @Column(name = "email", length = 30, unique = true)
    private String email;
}