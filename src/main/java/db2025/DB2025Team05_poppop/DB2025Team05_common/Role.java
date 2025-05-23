package db2025.DB2025Team05_poppop.DB2025Team05_common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    PRODUCER("생산자"),
    MANAGER("관리자"),
    PROCESSOR("처리업체");

    private final String description;

    public String getDescription() {
        return description;
    }

    public static Role fromString(String text) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
} 