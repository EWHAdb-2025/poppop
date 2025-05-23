package db2025.DB2025Team05_poppop.DB2025Team05_common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 예외
    INVALID_INPUT("잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
    RECORD_NOT_FOUND("처리 기록을 찾을 수 없습니다."),
    
    // 사용자 관련 예외
    DUPLICATE_EMAIL("이미 등록된 이메일입니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    INVALID_ROLE("유효하지 않은 권한입니다."),
    INVALID_EMAIL_FORMAT("올바른 이메일 형식이 아닙니다."),
    INVALID_NAME_LENGTH("이름은 2자 이상 50자 이하여야 합니다."),
    
    // 회사 관련 예외
    COMPANY_NOT_FOUND("회사 정보를 찾을 수 없습니다."),
    INVALID_BUSINESS_NUMBER("올바른 사업자등록번호 형식이 아닙니다."),
    DUPLICATE_BUSINESS_NUMBER("이미 등록된 사업자등록번호입니다."),
    INVALID_COMPANY_NAME_LENGTH("회사명은 2자 이상 100자 이하여야 합니다."),
    INVALID_COMPANY_ADDRESS_LENGTH("주소는 5자 이상 200자 이하여야 합니다."),
    
    // 팝업스토어 관련 예외
    POPUP_NOT_FOUND("팝업스토어를 찾을 수 없습니다."),
    INVALID_OPERATION_PERIOD("운영 기간이 올바르지 않습니다."),
    INVALID_ADDRESS_LENGTH("주소는 5자 이상 200자 이하여야 합니다."),
    INVALID_POPUP_NAME_LENGTH("팝업스토어명은 2자 이상 100자 이하여야 합니다."),
    
    // 폐기물 관련 예외
    WASTE_NOT_FOUND("폐기물 정보를 찾을 수 없습니다."),
    INVALID_WASTE_NAME_LENGTH("폐기물명은 2자 이상 100자 이하여야 합니다."),
    INVALID_WASTE_AMOUNT("폐기물 수량은 0보다 커야 합니다."),
    
    // 폐기물 처리 관련 예외
    DISPOSAL_RECORD_NOT_FOUND("폐기물 처리 기록을 찾을 수 없습니다."),
    DISPOSAL_RECORD_REGISTRATION_FAILED("폐기물 처리 이력 등록에 실패했습니다."),
    INVALID_DISPOSAL_STATUS("올바르지 않은 처리 상태입니다."),
    INVALID_DISPOSAL_DATE("처리 날짜가 올바르지 않습니다."),

    // 추가할 에러 코드들
    USER_REGISTRATION_FAILED("사용자 등록에 실패했습니다."),
    COMPANY_REGISTRATION_FAILED("회사 정보 등록에 실패했습니다."),
    USER_UPDATE_FAILED("사용자 정보 수정에 실패했습니다."),
    USER_DELETE_FAILED("사용자 삭제에 실패했습니다.");

    private final String message;
} 