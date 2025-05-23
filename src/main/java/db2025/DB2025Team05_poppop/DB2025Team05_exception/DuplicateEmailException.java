package db2025.DB2025Team05_poppop.DB2025Team05_exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
} 