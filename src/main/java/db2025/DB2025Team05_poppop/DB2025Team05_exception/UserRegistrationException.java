package db2025.DB2025Team05_poppop.DB2025Team05_exception;

import db2025.DB2025Team05_poppop.DB2025Team05_common.ErrorCode;

public class UserRegistrationException extends BusinessException {
    public UserRegistrationException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 등록 중 오류가 발생했습니다.");
    }
} 