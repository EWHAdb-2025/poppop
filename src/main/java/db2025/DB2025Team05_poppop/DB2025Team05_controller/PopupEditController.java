package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_service.PopupStoreService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class PopupEditController extends BaseController {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button saveButton;
    @FXML private Label messageLabel;

    private PopupManagement popup;
    private PopupStoreService popupStoreService;

    @FXML
    public void initialize() {
        super.initialize();
        try {
            popupStoreService = new PopupStoreService(
                    new PopupRepository(),
                    new UserRepository()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            setError("서비스 초기화 실패");
        }
    }

    /**
     * 수정 대상 팝업을 주입받아 필드에 표시
     */
    public void setPopup(PopupManagement popup) {
        this.popup = popup;
        nameField.setText(popup.getName());
        addressField.setText(popup.getAddress());
        startDatePicker.setValue(popup.getStartDate());
        endDatePicker.setValue(popup.getEndDate());
    }

    @FXML
    private void handleSave() {
        if (popup == null) {
            setError("수정할 팝업이 지정되지 않았습니다.");
            return;
        }

        try {
            // UI 값으로 업데이트
            String newName = nameField.getText();
            String newAddress = addressField.getText();
            LocalDate newStart = startDatePicker.getValue();
            LocalDate newEnd = endDatePicker.getValue();

            popup.setName(newName);
            popup.setAddress(newAddress);
            popup.setStartDate(newStart);
            popup.setEndDate(newEnd);

            // DB 업데이트
            popupStoreService.updatePopupStore(popup, popup.getUserId());
            setSuccess("수정이 완료되었습니다.");

            // 창 닫기
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();

        } catch (BusinessException e) {
            setError(e.getMessage());
        }
    }

    private void setError(String msg) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(msg);
    }

    private void setSuccess(String msg) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(msg);
    }
}
