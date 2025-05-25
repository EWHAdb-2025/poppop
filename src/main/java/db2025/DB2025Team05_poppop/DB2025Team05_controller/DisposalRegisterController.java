//package db2025.DB2025Team05_poppop.DB2025Team05_controller;
//
//import javafx.fxml.FXML;
//
//import javafx.scene.control.*;
//import javafx.event.ActionEvent;
//import db2025.DB2025Team05_poppop.DB2025Team05_domain.*;
//import db2025.DB2025Team05_poppop.DB2025Team05_service.DisposalService;
//import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
//
//import java.sql.Connection;
//import java.time.LocalDate;
//
//public class DisposalRegisterController {
//    @FXML private Label popupIdLabel;
//    @FXML private Label processorIdLabel;
//    @FXML private ComboBox<String> statusComboBox;
//    @FXML private DatePicker disposalDate;
//    @FXML private Label messageLabel;
//
//    private int userId;         // manager ID
//    private int popupId;        // 이전 화면에서 전달
//    private int processorId;    // 이전 화면에서 전달
//
//    public void setUserId(int id) {
//        this.userId = id;
//    }
//
//    public void setPopupId(int popupId) {
//        this.popupId = popupId;
//        popupIdLabel.setText("팝업 ID: " + popupId);
//    }
//
//    public void setProcessorId(int processorId) {
//        this.processorId = processorId;
//        processorIdLabel.setText("처리업체 ID: " + processorId);
//    }
//
//    @FXML
//    private void initialize() {
//        statusComboBox.getItems().addAll("대기", "진행중", "완료");
//    }
//
//    @FXML
//    private void handleRegister(ActionEvent event) {
//        String status = statusComboBox.getValue();
//        LocalDate removeDate = removeDatePicker.getValue();
//
//        if (status == null || removeDate == null) {
//            messageLabel.setText("상태와 철거일을 입력해주세요.");
//            return;
//        }
//
//        try {
//            DB2025_DISPOSAL_RECORD record = new DB2025_DISPOSAL_RECORD();
//            record.setUserId(userId);
//            record.setPopupId(popupId);
//            record.setProcessorId(processorId);
//            record.setStatus(status);
//            record.setRemoveDate(removeDate);
//
//            Connection conn = DBConnection.getConnection();
//            DisposalService service = new DisposalService(conn);
//
//            boolean result = service.registerDisposalRecord(record);
//            if (result) {
//                messageLabel.setStyle("-fx-text-fill: green;");
//                messageLabel.setText("처리 이력 등록 완료!");
//            } else {
//                messageLabel.setStyle("-fx-text-fill: red;");
//                messageLabel.setText("등록 실패 (권한 or 팝업 존재 오류)");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            messageLabel.setStyle("-fx-text-fill: red;");
//            messageLabel.setText("오류 발생");
//        }
//    }
//
//}
