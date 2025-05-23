package db2025.DB2025Team05_poppop.DB2025Team05_exception;

import db2025.DB2025Team05_poppop.DB2025Team05_common.ErrorCode;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException() {
        super(ErrorCode.DUPLICATE_EMAIL);
    }
} 