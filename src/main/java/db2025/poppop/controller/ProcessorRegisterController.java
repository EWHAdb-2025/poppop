package application;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

public class ProcessorRegisterController {
    @FXML private TextField businessIdField;
    @FXML private TextField companyNameField;
    @FXML private TextField ceoNameField;
    @FXML private TextField ceoContactField;
    @FXML private TextField emailField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister(ActionEvent event) {
        String businessId = businessIdField.getText();
        String companyName = companyNameField.getText();
        String ceoName = ceoNameField.getText();
        String ceoContact = ceoContactField.getText();
        String email = emailField.getText();

        if (businessId.isEmpty() || companyName.isEmpty() || ceoName.isEmpty() || ceoContact.isEmpty() || email.isEmpty()) {
            messageLabel.setText("모든 항목을 입력해주세요.");
            return;
        }

        if (BackendService.processorExists(businessId, email)) {
            messageLabel.setText("이미 등록된 사업자번호 또는 이메일입니다.");
            return;
        }

        boolean result = BackendService.registerProcessor(businessId, companyName, ceoName, ceoContact, email);

        if (result) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("등록 완료!");
        } else {
            messageLabel.setText("등록 실패!");
        }
    }

}
