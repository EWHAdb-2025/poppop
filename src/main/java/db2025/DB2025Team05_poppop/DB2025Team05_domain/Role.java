package db2025.DB2025Team05_poppop.DB2025Team05_domain;

/**
 * 사용자 역할을 정의하는 enum
 */
public enum Role {
    PRODUCER("producer"),
    MANAGER("manager");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role fromString(String text) {
        for (Role role : Role.values()) {
            if (role.value.equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
} 