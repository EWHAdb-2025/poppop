package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.*;
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
import java.util.*;
import java.util.stream.Collectors;

public class DisposalRegisterController extends BaseController {

    @FXML private ComboBox<String> popupComboBox;
    @FXML private ComboBox<String> companyComboBox;
    @FXML private DatePicker disposalDate;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label messageLabel;

    private int managerId;

    private final DisposalService disposalService = new DisposalService(
            new DispRecRepository(),
            new UserRepository(),
            new PopupRepository(),
            new WasteRepository(),
            new CompanyRepository()
    );

    // 이름 → ID 매핑
    private final Map<String, Integer> processorCompanyNameToId = new HashMap<>();
    private final Map<String, Integer> popupNameToId = new HashMap<>();

    public DisposalRegisterController() throws SQLException {
    }

    public void setManagerId(int id) {
        this.managerId = id;
    }

    @FXML
    public void initialize() {
        try {
            Optional<List<CompanyInfo>> processors = disposalService.getAllProcessCompanies();
            Optional<List<PopupManagement>> popups = disposalService.getAllPopupStores();

            List<String> processorNames = new ArrayList<>();
            if (processors.isPresent()) {
                for (CompanyInfo company : processors.get()) {
                    processorCompanyNameToId.put(company.getCompanyName(), company.getId());
                    processorNames.add(company.getCompanyName());
                }
            }
            companyComboBox.setItems(FXCollections.observableArrayList(processorNames));

            List<String> popupNames = new ArrayList<>();
            if (popups.isPresent()) {
                for (PopupManagement popup : popups.get()) {
                    popupNameToId.put(popup.getName(), popup.getPopupId());
                    popupNames.add(popup.getName());
                }
            }
            popupComboBox.setItems(FXCollections.observableArrayList(popupNames));

            statusComboBox.setItems(FXCollections.observableArrayList("완료", "대기", "보류"));

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("초기화 중 오류 발생: " + e.getMessage());
        }

    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String selectedPopupName = popupComboBox.getValue();
        String selectedProcessCompanyName = companyComboBox.getValue();
        LocalDate date = disposalDate.getValue();
        String status = statusComboBox.getValue();

        if (selectedProcessCompanyName == null || selectedPopupName == null || date == null || status == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("모든 항목을 입력해주세요.");
            return;
        }

        try {
            Integer userId = processorCompanyNameToId.get(selectedProcessCompanyName);
            Integer popupId = popupNameToId.get(selectedPopupName);

            Waste waste = new Waste();
            waste.setAmount(1);
            waste.setType("기본폐기물");

            DisposalRecord record = new DisposalRecord();
            record.setUserId(userId);
            record.setPopupId(popupId);
            record.setDisposalDate(date.atStartOfDay());
            record.setStatus(status);
            record.setWasteId(0); // validateDisposalInput에서 처리

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
