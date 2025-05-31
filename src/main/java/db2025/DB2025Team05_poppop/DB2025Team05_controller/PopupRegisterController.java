package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_service.PopupStoreService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class PopupRegisterController extends BaseController{

//    @FXML private TextField emailField;  // (UI 표시용, 실제 저장은 userId)
    @FXML private TextField popupnameField;
    @FXML private TextField addressField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label messageLabel;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        super.initialize();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/manager_home.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Manager 홈");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            setError("이전 화면으로 돌아가는 데 실패했습니다.");
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

    @FXML
    private void handleRegister(ActionEvent event) {
        String popupName = popupnameField.getText().trim();
        String address = addressField.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // 유효성 검사
        if (popupName.length() < 2 || popupName.length() > 100) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("팝업 이름은 2자 이상 100자 이하입니다.");
            return;
        }
        if (address.length() < 5 || address.length() > 200) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("주소는 5자 이상 200자 이하입니다.");
            return;
        }
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("운영 시작일과 종료일을 올바르게 입력해주세요.");
            return;
        }

        try {
            // 팝업 객체 구성
            PopupManagement popup = new PopupManagement();
            popup.setName(popupName);
            popup.setAddress(address);
            popup.setStartDate(startDate);
            popup.setEndDate(endDate);
            // ❗ popup.setUserId(userId); 는 서비스에서 처리

            // 서비스 호출
            PopupStoreService popupService = new PopupStoreService(
                    new PopupRepository(), new UserRepository()
            );

            PopupManagement result = popupService.registerPopupStore(popup, currentUser.getId());

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("팝업 등록 성공!");

            // TODO: 이후 화면 전환이나 목록 갱신 필요 시 여기에 작성

        } catch (BusinessException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(e.getMessage());  // 사용자 친화적 오류 메시지

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("시스템 오류 발생");
        }
    }
}

