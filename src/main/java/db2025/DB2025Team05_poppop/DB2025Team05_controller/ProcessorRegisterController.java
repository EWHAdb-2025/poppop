package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.CompanyInfo;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.CompanyRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProcessorRegisterController extends BaseController {

    @FXML private TextField bizNumberField;
    @FXML private TextField companyNameField;
    @FXML private TextField ceoNameField;
    @FXML private TextField ceoPhoneField;
    @FXML private TextField emailField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister(ActionEvent event) {
        String bizNum = bizNumberField.getText().trim();
        String companyName = companyNameField.getText().trim();
        String ceoName = ceoNameField.getText().trim();
        String ceoPhone = ceoPhoneField.getText().trim();
        String email = emailField.getText().trim();

        // 기본 유효성 검증 (서비스에서 상세 검증 수행됨)
        if (email.isEmpty() || companyName.isEmpty() || bizNum.isEmpty() || ceoName.isEmpty() || ceoPhone.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("모든 항목을 입력해주세요.");
            return;
        }

        try {
            // 처리자 사용자 정보 (이름, 이메일만 필요)
            User user = new User();
            user.setName(ceoName);
            user.setEmail(email);
            user.setRole(Role.PROCESSOR);

            // 처리 업체 회사 정보
            CompanyInfo company = new CompanyInfo();
            company.setCompanyName(companyName);
            company.setBusinessNumber(bizNum);
            company.setRepresentativeName(ceoName);
            company.setRepresentativePhone(ceoPhone);

            // 서비스 호출
            UserService userService = new UserService(
                    new UserRepository(), new CompanyRepository()
            );

            userService.registerProcessor(currentUser.getId(), user, company);

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("처리 업체 등록 성공!");

        } catch (BusinessException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("시스템 오류 발생");
        }
    }
}


