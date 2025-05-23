package db2025.DB2025Team05_poppop.DB2025Team05_service;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.DB2025_USER;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.CompanyRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 회원가입, 사용자 정보 조회/수정/삭제 기능을 제공
 */
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    // 이메일 검증을 위한 정규식 패턴
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    // 사업자번호 검증을 위한 정규식 패턴 (10자리 숫자)
    private static final Pattern BUSINESS_NUMBER_PATTERN = Pattern.compile(
        "^\\d{10}$"
    );

    /**
     * UserService 생성자
     * @param conn 데이터베이스 연결 객체
     * @throws SQLException 데이터베이스 연결 실패 시 발생
     */
    public UserService(Connection conn) throws SQLException {
        this.userRepository = new UserRepository(conn);
        this.companyRepository = new CompanyRepository(conn);
    }

    /**
     * 이메일 형식 검증
     * @param email 검증할 이메일
     * @return 이메일 형식이 올바른지 여부
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 사업자번호 형식 검증
     * @param businessNumber 검증할 사업자번호
     * @return 사업자번호 형식이 올바른지 여부
     */
    private boolean isValidBusinessNumber(String businessNumber) {
        return businessNumber != null && BUSINESS_NUMBER_PATTERN.matcher(businessNumber).matches();
    }

    /**
     * 사용자 회원가입 처리
     * producer인 경우 회사 정보도 함께 등록
     * 
     * @param user 사용자 정보 (id, name, role, email)
     * @param companyName 회사명 (producer인 경우 필수)
     * @param businessNumber 사업자번호 (producer인 경우 필수)
     * @param representativeName 대표자명 (producer인 경우 필수)
     * @param representativePhone 대표자 연락처 (producer인 경우 필수)
     * @return 회원가입 성공 여부
     * @throws DuplicateEmailException 이메일 중복 시 발생
     * @throws UserRegistrationException 사용자 정보 저장 실패 시 발생
     * @throws CompanyRegistrationException 회사 정보 저장 실패 시 발생
     */
    public boolean registerUser(DB2025_USER user, String companyName, String businessNumber, 
                              String representativeName, String representativePhone) {
        try {
            // 기본 입력값 검증
            if (user.getName() == null || user.getName().trim().length() < 2) {
                throw new IllegalArgumentException("이름은 2자 이상이어야 합니다.");
            }

            if (!isValidEmail(user.getEmail())) {
                throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
            }

            // 이메일 중복 체크
            if (userRepository.isEmailDuplicate(user.getEmail())) {
                throw new DuplicateEmailException("이미 가입된 이메일입니다.");
            }

            // 역할 체크
            if (user.getRole() == null) {
                throw new IllegalArgumentException("역할을 선택해주세요.");
            }

            // producer인 경우 회사 정보 체크
            if (user.getRole() == Role.PRODUCER) {
                if (companyName == null || companyName.trim().isEmpty()) {
                    throw new IllegalArgumentException("회사명을 입력해주세요.");
                }
                if (!isValidBusinessNumber(businessNumber)) {
                    throw new IllegalArgumentException("올바른 사업자번호 형식이 아닙니다.");
                }
                if (representativeName == null || representativeName.trim().isEmpty()) {
                    throw new IllegalArgumentException("대표자명을 입력해주세요.");
                }
                if (representativePhone == null || representativePhone.trim().isEmpty()) {
                    throw new IllegalArgumentException("대표자 연락처를 입력해주세요.");
                }
            }

            // 사용자 정보 저장
            boolean userInserted = userRepository.insertUser(user);
            if (!userInserted) {
                throw new UserRegistrationException("사용자 정보 저장에 실패했습니다.");
            }

            // producer인 경우 회사 정보 저장
            if (user.getRole() == Role.PRODUCER) {
                boolean companyInserted = companyRepository.insertCompanyInfo(
                    user.getId(), companyName, businessNumber, 
                    representativeName, representativePhone
                );
                if (!companyInserted) {
                    // 회사 정보 저장 실패 시 사용자 정보 롤백
                    userRepository.deleteUser(user.getId());
                    throw new CompanyRegistrationException("회사 정보 저장에 실패했습니다.");
                }
            }

            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("입력값 오류: " + e.getMessage());
            return false;
        } catch (DuplicateEmailException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (UserRegistrationException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (CompanyRegistrationException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("회원가입 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 사용자 ID로 사용자 정보 조회
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 (없는 경우 null)
     */
    public DB2025_USER findUserById(int userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

    /**
     * 사용자 정보 수정
     * @param user 수정할 사용자 정보
     * @return 수정 성공 여부
     */
    public boolean updateUser(DB2025_USER user) {
        return userRepository.updateUser(user);
    }

    /**
     * 사용자 삭제
     * @param userId 삭제할 사용자 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteUser(int userId) {
        return userRepository.deleteUser(userId);
    }
} 