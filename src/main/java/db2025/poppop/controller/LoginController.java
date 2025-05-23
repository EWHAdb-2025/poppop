package application;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private ToggleButton producerButton;
    @FXML private ToggleButton managerButton;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String role = producerButton.isSelected() ? "producer" : managerButton.isSelected() ? "manager" : "";

        if (email.isEmpty() || role.isEmpty()) {
            messageLabel.setText("�̸��ϰ� ������ �������ּ���");
            return;
        }

        boolean result = BackendService.login(email, role);

        if (result) {
            try {
                String fxml = role.equals("producer") ? "/popup_register.fxml" : "/manager_home.fxml";
                Parent root = FXMLLoader.load(getClass().getResource(fxml));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(role);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("ȭ�� ��ȯ ����");
            }
        } else {
            messageLabel.setText("�������� �ʴ� �����Դϴ�.");
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
