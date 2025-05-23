package application;

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

    // 이전 화면에서 popupId + processorId 넘겨받음
    public void setSelectedInfo(String popupId, String processorId) {
        this.popupId = popupId;
        this.processorId = processorId;

        popupIdLabel.setText("팝업 ID: " + popupId);
        processorIdLabel.setText("처리업체 ID: " + processorId);
    }
    @FXML
    public void initialize() {
        // 처리 상태 목록 자바 코드로 추가
        statusComboBox.getItems().addAll("대기", "진행중", "완료");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String status = statusComboBox.getValue();

        if (status == null || status.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("처리 상태를 선택해주세요.");
            return;
        }

        // 백엔드.....?
        boolean result = BackendService.registerDisposal(popupId, processorId, status);

        if (result) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("처리 이력 등록 완료!");
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("등록 실패!");
        }
    }
    
    
	
	

}
