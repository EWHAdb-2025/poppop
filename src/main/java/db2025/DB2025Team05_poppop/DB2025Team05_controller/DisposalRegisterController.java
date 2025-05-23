package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

public class DisposalRegisterController {
    @FXML private Label popupIdLabel;
    @FXML private Label processorIdLabel;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker disposalDate;
    @FXML private Label messageLabel;

    private String popupId;
    private String processorId;

    // ���� ȭ�鿡�� popupId + processorId �Ѱܹ���
    public void setSelectedInfo(String popupId, String processorId) {
        this.popupId = popupId;
        this.processorId = processorId;

        popupIdLabel.setText("�˾� ID: " + popupId);
        processorIdLabel.setText("ó����ü ID: " + processorId);
    }
    @FXML
    public void initialize() {
        // ó�� ���� ��� �ڹ� �ڵ�� �߰�
        statusComboBox.getItems().addAll("���", "������", "�Ϸ�");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String status = statusComboBox.getValue();

        if (status == null || status.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("ó�� ���¸� �������ּ���.");
            return;
        }

        // �鿣��.....?
        boolean result = BackendService.registerDisposal(popupId, processorId, status);

        if (result) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("ó�� �̷� ��� �Ϸ�!");
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("��� ����!");
        }
    }
    
    
	
	

}
