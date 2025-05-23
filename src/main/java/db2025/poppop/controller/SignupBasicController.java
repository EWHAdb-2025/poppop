package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class SignupBasicController {
	
    @FXML private TextField emailField;
    @FXML private TextField nameField;
    @FXML private ToggleButton producerButton;
    @FXML private ToggleButton managerButton;
    @FXML private Button nextButton;
    @FXML private Label messageLabel;

    @FXML
    private void handleNext(ActionEvent event) {
        String email = emailField.getText();
        String name = nameField.getText();
        String role = producerButton.isSelected() ? "producer" : managerButton.isSelected() ? "manager" : "";

        if (email.isEmpty() || name.isEmpty()) {
            messageLabel.setText("�̸��ϰ� �̸��� �Է����ּ���.");
            return;
        }

        if (role.isEmpty()) {
            messageLabel.setText("������ �������ּ���.");
            return;
        }

        if (role.equals("manager")) {
            // manager�� �ٷ� ȸ������ ó��
            boolean result = BackendService.registerUser(email, name, role);
            if (result) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("ȸ������ �Ϸ�!");
            } else {
                messageLabel.setText("�ߺ��� �̸����Դϴ�.");
            }
        } else {
            // producer�� ȸ������ �Է� ȭ������ �̵�
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup_company.fxml"));
                Parent root = loader.load();

                SignupProducerController controller = loader.getController();
                controller.setBasicInfo(email, name, role);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("ȸ�� ���� �Է�");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("ȸ�� ���� ȭ�� ��ȯ ����");
            }
        }
    }

}
