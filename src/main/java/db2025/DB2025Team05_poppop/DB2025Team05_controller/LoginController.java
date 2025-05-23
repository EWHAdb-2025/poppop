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
            messageLabel.setText("�̸��ϰ� ������ �������ּ���");
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
                messageLabel.setText("�������� �ʴ� �����Դϴ�.");
                return;
            }

            if (matchedUser.getRole() != selectedRole) {
                messageLabel.setText("������ ������ �ùٸ��� �ʽ��ϴ�.");
                return;
            }

            // �α��� ���� �� ���� ȭ�� ��ȯ
            String fxml = (selectedRole == Role.PRODUCER) ? "/popup_register.fxml" : "/manager_home.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(selectedRole.toString());
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("�α��� ó�� �� ���� �߻�");
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/signup_basic.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("ȸ������");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("ȸ������ ȭ�� ����");
        }
    }

}
