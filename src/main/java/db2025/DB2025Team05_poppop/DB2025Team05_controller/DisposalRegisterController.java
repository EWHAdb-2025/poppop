package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.DisposalRecord;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.Waste;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.*;
import db2025.DB2025Team05_poppop.DB2025Team05_service.DisposalService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DisposalRegisterController extends BaseController {

    @FXML private ComboBox<User> processorComboBox;
    @FXML private ComboBox<PopupManagement> popupComboBox;
    @FXML private DatePicker disposalDatePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label messageLabel;

    private int managerId;

    // 서비스 객체 의존성 주입 (DI) 방식으로 생성
    private final DisposalService disposalService = new DisposalService(
            new DispRecRepository(),
            new UserRepository(),
            new PopupRepository(),
            new WasteRepository()
    );

    public DisposalRegisterController() throws SQLException {
    }

    public void setManagerId(int id) {
        this.managerId = id;
    }

    @FXML
    public void initialize() {
        try {
            // 처리자 목록 및 팝업스토어 목록 조회
            List<User> processors = disposalService.getAllProcessors();
            List<PopupManagement> popups = disposalService.getAllPopupStores();

            processorComboBox.setItems(FXCollections.observableArrayList(processors));
            popupComboBox.setItems(FXCollections.observableArrayList(popups));
            statusComboBox.setItems(FXCollections.observableArrayList("완료", "대기", "보류"));
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("초기화 중 오류 발생: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        User selectedProcessor = processorComboBox.getValue();
        PopupManagement selectedPopup = popupComboBox.getValue();
        LocalDate date = disposalDatePicker.getValue();
        String status = statusComboBox.getValue();

        if (selectedProcessor == null || selectedPopup == null || date == null || status == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("모든 항목을 입력해주세요.");
            return;
        }

        try {
            // 기본 Waste 객체 생성
            Waste waste = new Waste();
            waste.setAmount(1); // 기본값
            waste.setType("기본폐기물"); // 기본값

            // DisposalRecord 생성
            DisposalRecord record = new DisposalRecord();
            record.setUserId(selectedProcessor.getId());
            record.setPopupId(selectedPopup.getPopupId());
            record.setDisposalDate(date.atStartOfDay());
            record.setStatus(status);
            record.setWasteId(0); // 일단 더미값 (validateDisposalInput에서 확인됨)

            disposalService.registerDisposalRecord(record, waste, managerId);

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("처리 이력 등록 성공!");

        } catch (BusinessException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("시스템 오류 발생");
        }
    }
}
