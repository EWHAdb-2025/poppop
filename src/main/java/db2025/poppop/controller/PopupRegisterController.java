package application;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.time.LocalDate;

public class PopupRegisterController {

    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister(ActionEvent event) {
        String email = emailField.getText();
        String address = addressField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (email.isEmpty() || address.isEmpty() || startDate == null || endDate == null) {
            messageLabel.setText("��� �׸��� �Է����ּ���.");
            return;
        }

        if (!endDate.isAfter(startDate)) {
            messageLabel.setText("� �������� ������ ���Ŀ��� �մϴ�.");
            return;
        }

        //TODO:~ 
        boolean result = BackendService.registerOrUpdatePopup(userId, address, startDate, endDate);

        if (result) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("�˾������ ��� �Ϸ�!");
        } else {
            messageLabel.setText("��� ����!");
        }
    }

}
