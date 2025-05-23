package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.*;
import db2025.DB2025Team05_poppop.DB2025Team05_service.DisposalService;
import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;

import java.sql.Connection;
import java.time.LocalDate;

public class DisposalRegisterController {
    @FXML private Label popupIdLabel;
    @FXML private Label processorIdLabel;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker disposalDate;
    @FXML private Label messageLabel;
    
    private int userId;         // manager ID
    private int popupId;        // ���� ȭ�鿡�� ����
    private int processorId;    // ���� ȭ�鿡�� ����

    public void setUserId(int id) {
        this.userId = id;
    }

    public void setPopupId(int popupId) {
        this.popupId = popupId;
        popupIdLabel.setText("�˾� ID: " + popupId);
    }

    public void setProcessorId(int processorId) {
        this.processorId = processorId;
        processorIdLabel.setText("ó����ü ID: " + processorId);
    }

    @FXML
    private void initialize() {
        statusComboBox.getItems().addAll("���", "������", "�Ϸ�");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String status = statusComboBox.getValue();
        LocalDate removeDate = removeDatePicker.getValue();

        if (status == null || removeDate == null) {
            messageLabel.setText("���¿� ö������ �Է����ּ���.");
            return;
        }

        try {
            DB2025_DISPOSAL_RECORD record = new DB2025_DISPOSAL_RECORD();
            record.setUserId(userId);
            record.setPopupId(popupId);
            record.setProcessorId(processorId);
            record.setStatus(status);
            record.setRemoveDate(removeDate);

            Connection conn = DBConnection.getConnection();
            DisposalService service = new DisposalService(conn);

            boolean result = service.registerDisposalRecord(record);
            if (result) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("ó�� �̷� ��� �Ϸ�!");
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("��� ���� (���� or �˾� ���� ����)");
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("���� �߻�");
        }
    }


}
