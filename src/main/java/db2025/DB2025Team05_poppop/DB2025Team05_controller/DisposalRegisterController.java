package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.DisposalRecord;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.Waste;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.DispRecRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.WasteRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_service.DisposalService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.List;

public class DisposalRegisterController {

    @FXML private ComboBox<User> processorComboBox;
    @FXML private ComboBox<PopupManagement> popupComboBox;
    @FXML private DatePicker disposalDatePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label messageLabel;

    private int managerId;

    public void setManagerId(int id) {
        this.managerId = id;
    }

    @FXML
    public void initialize() {
        try {
            UserRepository userRepo = new UserRepository();
            PopupRepository popupRepo = new PopupRepository();

            // 예시: 모든 사용자 중 role이 PROCESSOR인 경우
            List<User> processors = userRepo.findAllProcessors(); // 따로 만들어야 함
            List<PopupManagement> popups = popupRepo.findAllBasic(); // 따로 만들어야 함

            processorComboBox.setItems(FXCollections.observableArrayList(processors));
            popupComboBox.setItems(FXCollections.observableArrayList(popups));
            statusComboBox.setItems(FXCollections.observableArrayList("완료", "대기", "보류"));

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("초기화 중 오류 발생");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        User selectedProcessor = processorComboBox.getValue();
        PopupManagement selectedPopup = popupComboBox.getValue();
        LocalDate date = disposalDatePicker.getValue();
        String status = statusComboBox.getValue();

        if (selectedProcessor == null || selectedPopup == null || date == null) {
            messageLabel.setText("모든 항목을 입력해주세요.");
            return;
        }

        try {
            // Waste 객체 생성 (단순하게 처리)
            Waste waste = new Waste();
            waste.setAmount(1); // 기본값, 명세에 따라 자동 생성
            waste.setType("기본폐기물");

            // DisposalRecord 생성
            DisposalRecord record = new DisposalRecord();
            record.setUserId(selectedProcessor.getId());
            record.setPopupId(selectedPopup.getPopupId());
            record.setDisposalDate(date.atStartOfDay());
            record.setStatus(status);

            // 서비스 호출
            DisposalService service = new DisposalService(
                    new DispRecRepository(), new UserRepository(), new PopupRepository(), new WasteRepository()
            );

            service.registerDisposalRecord(record, waste, managerId);

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