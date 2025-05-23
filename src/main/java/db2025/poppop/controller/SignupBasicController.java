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
            messageLabel.setText("이메일과 이름을 입력해주세요.");
            return;
        }

        if (role.isEmpty()) {
            messageLabel.setText("역할을 선택해주세요.");
            return;
        }

        if (role.equals("manager")) {
            // manager는 바로 회원가입 처리
            boolean result = BackendService.registerUser(email, name, role);
            if (result) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("회원가입 완료!");
            } else {
                messageLabel.setText("중복된 이메일입니다.");
            }
        } else {
            // producer면 회사정보 입력 화면으로 이동
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup_company.fxml"));
                Parent root = loader.load();

                SignupProducerController controller = loader.getController();
                controller.setBasicInfo(email, name, role);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("회사 정보 입력");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("회사 정보 화면 전환 실패");
            }
        }
    }

}
