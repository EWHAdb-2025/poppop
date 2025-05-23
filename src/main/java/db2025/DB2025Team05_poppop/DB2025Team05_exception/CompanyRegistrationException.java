package db2025.DB2025Team05_poppop.DB2025Team05_exception;

import db2025.DB2025Team05_poppop.DB2025Team05_common.ErrorCode;

public class CompanyRegistrationException extends BusinessException {
    public CompanyRegistrationException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR, "회사 정보 등록 중 오류가 발생했습니다.");
    }
} 