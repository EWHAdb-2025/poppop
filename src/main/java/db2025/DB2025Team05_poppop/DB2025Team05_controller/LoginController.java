package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_common.AppSession;
import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
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
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private ToggleButton producerButton;
    @FXML private ToggleButton managerButton;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        Role selectedRole = producerButton.isSelected() ? Role.PRODUCER :
                            managerButton.isSelected() ? Role.MANAGER : null;

        if (email.isEmpty() || selectedRole == null) {
            messageLabel.setText("이메일과 역할을 설정해주세요");
            return;
        }

        try {
            /// ✅ 서비스 계층 사용
            UserService userService = new UserService(new UserRepository(), new CompanyRepository());
            User user = userService.loginWithEmail(email);

            if (user.getRole() != selectedRole) {
                messageLabel.setText("선택한 역할이 올바르지 않습니다.");
                return;
            }

            // 로그인 성공 → AppSession 저장
            AppSession.login(user);

            // 화면 전환
            String fxml = (selectedRole == Role.PRODUCER) ? "/producer_home.fxml" : "/manager_home.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(selectedRole.toString());
            stage.show();

        } catch (BusinessException | SQLException e) {
            messageLabel.setText(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/signup_basic.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("회원가입");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("회원가입 화면 오류");
        }
    }
}
