package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.*;
import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
import db2025.DB2025Team05_poppop.DB2025Team05_service.UserService;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;

import java.sql.Connection;
import java.util.List;


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
            Connection conn = DBConnection.getConnection();
            UserRepository userRepository = new UserRepository(conn);
            List<DB2025_USER> allUsers = userRepository.findAll();

            DB2025_USER matchedUser = null;
            for (DB2025_USER user : allUsers) {
                if (user.getEmail().equalsIgnoreCase(email)) {
                    matchedUser = user;
                    break;
                }
            }

            if (matchedUser == null) {
                messageLabel.setText("존재하지 않는 계정입니다.");
                return;
            }

            if (matchedUser.getRole() != selectedRole) {
                messageLabel.setText("선택한 역할이 올바르지 않습니다.");
                return;
            }

            // 로그인 성공 → 다음 화면 전환
            String fxml = (selectedRole == Role.PRODUCER) ? "/popup_register.fxml" : "/manager_home.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(selectedRole.toString());
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("로그인 처리 중 오류 발생");
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
