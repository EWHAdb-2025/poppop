package db2025.DB2025Team05_poppop.DB2025Team05_controller;
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
            messageLabel.setText("��� �׸��� �Է����ּ���.");
            return;
        }

        if (BackendService.processorExists(businessId, email)) {
            messageLabel.setText("�̹� ��ϵ� ����ڹ�ȣ �Ǵ� �̸����Դϴ�.");
            return;
        }

        boolean result = BackendService.registerProcessor(businessId, companyName, ceoName, ceoContact, email);

        if (result) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("��� �Ϸ�!");
        } else {
            messageLabel.setText("��� ����!");
        }
    }

}
