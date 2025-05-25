//package application;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.event.ActionEvent;
//
//import db2025.DB2025Team05_poppop.DB2025Team05_domain.*;
//import db2025.DB2025Team05_poppop.DB2025Team05_service.PopupStoreService;
//import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
//
//import java.sql.Connection;
//import java.time.LocalDate;
//
//public class PopupRegisterController {
//    @FXML private TextField addressField;
//    @FXML private DatePicker startDatePicker;
//    @FXML private DatePicker endDatePicker;
//    @FXML private Label messageLabel;
//
//    private int userId;
//
//    public void setUserId(int userId) {
//        this.userId = userId;
//    }
//
//    @FXML
//    private void handleRegister(ActionEvent event) {
//        String address = addressField.getText();
//        LocalDate startDate = startDatePicker.getValue();
//        LocalDate endDate = endDatePicker.getValue();
//
//        if (address.isEmpty() || startDate == null || endDate == null) {
//            messageLabel.setText("모든 정보를 입력해주세요.");
//            return;
//        }
//
//        try {
//            DB2025_POPUP_MANAGEMENT popup = new DB2025_POPUP_MANAGEMENT();
//            popup.setUserId(userId);
//            popup.setPopupAddress(address);
//            popup.setStartDate(startDate);
//            popup.setEndDate(endDate);
//
//            Connection conn = DBConnection.getConnection();
//            PopupStoreService popupService = new PopupStoreService(conn);
//
//            boolean success = popupService.registerPopupStore(popup);
//
//            if (success) {
//                messageLabel.setStyle("-fx-text-fill: green;");
//                messageLabel.setText("팝업스토어 등록 완료!");
//            } else {
//                messageLabel.setStyle("-fx-text-fill: red;");
//                messageLabel.setText("등록 실패! (날짜 유효성 또는 권한 확인)");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            messageLabel.setStyle("-fx-text-fill: red;");
//            messageLabel.setText("팝업 등록 중 오류 발생");
//        }
//    }
//}
