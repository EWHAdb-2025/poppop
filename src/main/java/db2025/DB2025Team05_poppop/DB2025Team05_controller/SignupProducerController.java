package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.CompanyInfo;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.CompanyRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class SignupProducerController{

    @FXML private TextField companyNameField;
    @FXML private TextField bizNumberField;
    @FXML private TextField ceoNameField;
    @FXML private TextField ceoPhoneField;
    @FXML private Label messageLabel;
    @FXML private Button backButton;

    private String email;
    private String name;
    private Role role;

    public SignupProducerController() throws SQLException {
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("로그인");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            setError("이전 화면으로 돌아가는 데 실패했습니다.");
        }
    }

    private void setError(String msg) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(msg);
    }
    private void setSuccess(String msg) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(msg);
    }


    public void setBasicInfo(String email, String name, String roleStr) {
        this.email = email;
        this.name = name;
        this.role = Role.valueOf(roleStr.toUpperCase());
    }

    private final UserService userService = new UserService(
            new UserRepository(),
            new CompanyRepository()
    );

    @FXML
    private void handleRegister(ActionEvent event) {
        String companyName = companyNameField.getText().trim();
        String businessNumber = bizNumberField.getText().trim();
        String ceoName = ceoNameField.getText().trim();
        String ceoPhone = ceoPhoneField.getText().trim();

        if (companyName.isEmpty() || businessNumber.isEmpty() ||
                ceoName.isEmpty() || ceoPhone.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("모든 회사 정보를 입력해주세요.");
            return;
        }

        try {
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setRole(role);

            CompanyInfo companyInfo = new CompanyInfo();
            companyInfo.setCompanyName(companyName);
            companyInfo.setBusinessNumber(businessNumber);
            companyInfo.setRepresentativeName(ceoName);
            companyInfo.setRepresentativePhone(ceoPhone);

            userService.registerUser(user, companyInfo);

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("회원가입 완료! 팝업 등록 화면으로 이동합니다.");

            // 화면 전환
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/popup_register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("팝업스토어 등록");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("회원가입 처리 중 오류 발생");
        }
    }
}




