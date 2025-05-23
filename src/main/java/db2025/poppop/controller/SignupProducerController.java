package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

public class SignupProducerController {
    @FXML private TextField businessIdField;
    @FXML private TextField companyNameField;
    @FXML private TextField ceoNameField;
    @FXML private TextField ceoContactField;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;

    private String email;
    private String name;
    private String role;

    public void setBasicInfo(String email, String name, String role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String businessId = businessIdField.getText();
        String companyName = companyNameField.getText();
        String ceoName = ceoNameField.getText();
        String ceoContact = ceoContactField.getText();

        if (businessId.isEmpty() || companyName.isEmpty() || ceoName.isEmpty() || ceoContact.isEmpty()) {
            messageLabel.setText("정보를 모두 기입해주세요.");
            return;
        }

        boolean result = BackendService.registerProducerWithCompany(
            email, name, role,
            businessId, companyName, ceoName, ceoContact
        );

        if (result) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("회원가입 완료!");

        } else {
            messageLabel.setText("회원가입 실패");
        }
    }

}
