package db2025.DB2025Team05_poppop.DB2025Team05_service;

import db2025.DB2025Team05_poppop.DB2025Team05_common.ErrorCode;
import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.CompanyInfo;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.CompanyRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 
 * 주요 기능:
 * 1. 사용자 등록 (일반 사용자, 생산자, 처리업체)
 * 2. 사용자 정보 조회/수정/삭제
 * 3. 회사 정보 관리 (생산자, 처리업체)
 * 
 * 권한 관리:
 * - 일반 사용자: 기본적인 사용자 정보 관리
 * - 생산자(PRODUCER): 회사 정보 추가 관리
 * - 처리업체(PROCESSOR): manager에 의해서만 등록 가능
 * - 관리자(MANAGER): 처리업체 등록 권한 보유
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    /**
     * 시스템에서 사용되는 상수값들을 관리하는 내부 클래스
     * 
     * 상수 종류:
     * - EMAIL_PATTERN: 이메일 유효성 검사를 위한 정규식 패턴
     * - BUSINESS_NUMBER_PATTERN: 사업자등록번호 유효성 검사를 위한 정규식 패턴
     * - MIN_NAME_LENGTH: 이름 최소 길이 (2자)
     * - MAX_NAME_LENGTH: 이름 최대 길이 (50자)
     * - MAX_COMPANY_NAME_LENGTH: 회사명 최대 길이 (100자)
     */
    private static final class Constants {
        private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        private static final Pattern BUSINESS_NUMBER_PATTERN = Pattern.compile("^\\d{10}$");
        private static final int MIN_NAME_LENGTH = 2;
        private static final int MAX_NAME_LENGTH = 50;
        private static final int MAX_COMPANY_NAME_LENGTH = 100;
    }

    /**
     * UserService 생성자
     * 
     * @param userRepository 사용자 정보를 관리하는 저장소
     * @param companyRepository 회사 정보를 관리하는 저장소
     */
    public UserService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * 일반 사용자 등록
     * 
     * 처리 과정:
     * 1. 입력값 유효성 검증 (사용자 정보, 회사 정보)
     * 2. 이메일 중복 확인
     * 3. 사용자 정보 저장
     * 4. 생산자인 경우 회사 정보 저장
     * 
     * @param user 등록할 사용자 정보 (이메일, 이름, 역할 필수)
     * @param companyInfo 회사 정보 (생산자인 경우 필수, 그 외 null 가능)
     * @return 등록된 사용자 정보
     * @throws BusinessException 입력값 검증 실패, 이메일 중복, 저장 실패 시 발생
     */
    @Transactional
    public User registerUser(User user, CompanyInfo companyInfo) {
        try {
            validateRegistrationInput(user, companyInfo);
            checkEmailDuplicate(user.getEmail());
            
            User savedUser = saveUser(user);
            if (user.getRole() == Role.PRODUCER) {
                saveCompanyInfo(savedUser.getId(), companyInfo);
            }
            
            return savedUser;
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 폐기물 처리 업체 등록 (관리자 전용)
     * 
     * 처리 과정:
     * 1. 관리자 권한 확인
     * 2. 입력값 유효성 검증
     * 3. 이메일 및 사업자번호 중복 확인
     * 4. 사용자 정보 저장 (역할: PROCESSOR)
     * 5. 회사 정보 저장
     * 
     * @param managerId 등록을 수행하는 관리자의 ID
     * @param user 등록할 처리업체 사용자 정보
     * @param companyInfo 등록할 처리업체 회사 정보
     * @return 등록된 사용자 정보
     * @throws BusinessException 권한 없음, 입력값 검증 실패, 중복 확인 실패, 저장 실패 시 발생
     */
    @Transactional
    public User registerProcessor(int managerId, User user, CompanyInfo companyInfo) {
        try {
            validateManagerPermission(managerId);
            validateRegistrationInput(user, companyInfo);
            checkEmailDuplicate(user.getEmail());
            checkBusinessNumberDuplicate(companyInfo.getBusinessNumber());
            
            user.setRole(Role.PROCESSOR);
            User savedUser = saveUser(user);
            saveCompanyInfo(savedUser.getId(), companyInfo);
            
            return savedUser;
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자 정보 수정
     * 
     * 처리 과정:
     * 1. 입력값 유효성 검증
     * 2. 기존 사용자 존재 확인
     * 3. 이메일 변경 시 중복 확인
     * 4. 사용자 정보 수정
     * 
     * @param user 수정할 사용자 정보 (ID, 이메일, 이름 필수)
     * @return 수정된 사용자 정보
     * @throws BusinessException 입력값 검증 실패, 사용자 없음, 이메일 중복, 수정 실패 시 발생
     */
    @Transactional
    public User updateUser(User user) {
        try {
            validateUserInput(user);
            User existingUser = findExistingUser(user.getId());
            validateEmailChange(existingUser, user);
            
            if (!userRepository.updateUser(user)) {
                throw new BusinessException(ErrorCode.USER_UPDATE_FAILED);
            }
            
            return user;
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자 삭제
     * 
     * 처리 과정:
     * 1. 기존 사용자 존재 확인
     * 2. 사용자 정보 삭제
     * 
     * @param userId 삭제할 사용자 ID
     * @return 삭제 성공 여부
     * @throws BusinessException 사용자 없음, 삭제 실패 시 발생
     */
    @Transactional
    public boolean deleteUser(int userId) {
        try {
            User user = findExistingUser(userId);
            
            if (!userRepository.deleteUser(userId)) {
                throw new BusinessException(ErrorCode.USER_DELETE_FAILED);
            }
            
            return true;
        } catch (SQLException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자 ID로 조회
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 (Optional)
     * @throws BusinessException 데이터베이스 오류 발생 시
     */
    public Optional<User> findUserById(int userId) {
        return userRepository.findByUserId(userId);
    }

    // Private helper methods

    /**
     * 사용자 등록을 위한 입력값 검증
     * 
     * @param user 검증할 사용자 정보
     * @param companyInfo 검증할 회사 정보
     * @throws BusinessException 검증 실패 시
     */
    private void validateRegistrationInput(User user, CompanyInfo companyInfo) {
        validateUserInput(user);
        if (user.getRole() == Role.PRODUCER) {
            validateCompanyInput(companyInfo);
        }
    }

    /**
     * 사용자 정보 입력값 검증
     * 
     * 검증 항목:
     * 1. null 체크
     * 2. 이메일 형식
     * 3. 이름 길이 (2-50자)
     * 4. 역할 지정 여부
     * 
     * @param user 검증할 사용자 정보
     * @throws BusinessException 검증 실패 시
     */
    private void validateUserInput(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "사용자 정보가 없습니다.");
        }
        if (user.getEmail() == null || !Constants.EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
        if (user.getName() == null || user.getName().length() < Constants.MIN_NAME_LENGTH || 
            user.getName().length() > Constants.MAX_NAME_LENGTH) {
            throw new BusinessException(ErrorCode.INVALID_NAME_LENGTH);
        }
        if (user.getRole() == null) {
            throw new BusinessException(ErrorCode.INVALID_ROLE, "역할을 선택해주세요.");
        }
    }

    /**
     * 회사 정보 입력값 검증
     * 
     * 검증 항목:
     * 1. null 체크
     * 2. 사업자등록번호 형식 (10자리 숫자)
     * 3. 회사명 길이 (2-100자)
     * 
     * @param companyInfo 검증할 회사 정보
     * @throws BusinessException 검증 실패 시
     */
    private void validateCompanyInput(CompanyInfo companyInfo) {
        if (companyInfo == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "회사 정보가 없습니다.");
        }
        if (companyInfo.getBusinessNumber() == null || 
            !Constants.BUSINESS_NUMBER_PATTERN.matcher(companyInfo.getBusinessNumber()).matches()) {
            throw new BusinessException(ErrorCode.INVALID_BUSINESS_NUMBER);
        }
        if (companyInfo.getCompanyName() == null || 
            companyInfo.getCompanyName().length() < Constants.MIN_NAME_LENGTH || 
            companyInfo.getCompanyName().length() > Constants.MAX_COMPANY_NAME_LENGTH) {
            throw new BusinessException(ErrorCode.INVALID_COMPANY_NAME_LENGTH);
        }
    }

    /**
     * 관리자 권한 확인
     * 
     * @param managerId 확인할 관리자 ID
     * @throws BusinessException 관리자가 아니거나 존재하지 않는 경우
     */
    private void validateManagerPermission(int managerId) throws SQLException {
        Optional<User> managerOpt = userRepository.findByUserId(managerId);
        if (managerOpt.isEmpty() || managerOpt.get().getRole() != Role.MANAGER) {
            throw new BusinessException(ErrorCode.INVALID_ROLE, "관리자만 폐기물 처리 업체를 등록할 수 있습니다.");
        }
    }

    /**
     * 이메일 중복 확인
     * 
     * @param email 확인할 이메일
     * @throws BusinessException 이미 등록된 이메일인 경우
     */
    private void checkEmailDuplicate(String email) throws SQLException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    /**
     * 사업자등록번호 중복 확인
     * 
     * @param businessNumber 확인할 사업자등록번호
     * @throws BusinessException 이미 등록된 사업자등록번호인 경우
     */
    private void checkBusinessNumberDuplicate(String businessNumber) throws SQLException {
        if (companyRepository.isBusinessNumberDuplicate(businessNumber)) {
            throw new BusinessException(ErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }
    }

    /**
     * 사용자 존재 여부 확인
     * 
     * @param userId 확인할 사용자 ID
     * @return 존재하는 사용자 정보
     * @throws BusinessException 사용자가 존재하지 않는 경우
     */
    private User findExistingUser(int userId) throws SQLException {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 이메일 변경 시 중복 확인
     * 
     * @param existingUser 기존 사용자 정보
     * @param newUser 새로운 사용자 정보
     * @throws BusinessException 이메일이 중복되는 경우
     */
    private void validateEmailChange(User existingUser, User newUser) throws SQLException {
        if (!existingUser.getEmail().equals(newUser.getEmail())) {
            checkEmailDuplicate(newUser.getEmail());
        }
    }

    /**
     * 사용자 정보 저장
     * 
     * @param user 저장할 사용자 정보
     * @return 저장된 사용자 정보
     * @throws BusinessException 저장 실패 시
     */
    private User saveUser(User user) throws SQLException {
        User savedUser = userRepository.insertUser(user);
        if (savedUser == null) {
            throw new BusinessException(ErrorCode.USER_REGISTRATION_FAILED);
        }
        return savedUser;
    }

    /**
     * 회사 정보 저장
     * 
     * @param userId 사용자 ID
     * @param companyInfo 저장할 회사 정보
     * @throws BusinessException 저장 실패 시
     */
    private void saveCompanyInfo(int userId, CompanyInfo companyInfo) throws SQLException {
        companyInfo.setUserId(userId);
        if (companyRepository.insertCompanyInfo(companyInfo) == null) {
            throw new BusinessException(ErrorCode.COMPANY_REGISTRATION_FAILED);
        }
    }
} 