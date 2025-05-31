package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.*;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.*;
import db2025.DB2025Team05_poppop.DB2025Team05_service.DisposalService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DisposalRegisterController extends BaseController {

    @FXML
    private ComboBox<String> popupComboBox;
    @FXML
    private ComboBox<String> processorComboBox;
    @FXML
    private DatePicker disposalDate;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private Label messageLabel;
    @FXML
    private Button backButton;
    @FXML
    private TextField wasteTypeField;
    @FXML
    private TextField amountField;

    private final DisposalService disposalService = new DisposalService(
            new DispRecRepository(),
            new UserRepository(),
            new PopupRepository(),
            new WasteRepository(),
            new CompanyRepository()
    );
    private final CompanyRepository companyRepository = new CompanyRepository();
    private final DispRecRepository dispRecRepository = new DispRecRepository();

    // Maps for name → ID
    private final Map<String, Integer> processorCompanyNameToId = new HashMap<>();
    private final Map<String, Integer> popupNameToId = new HashMap<>();

    public DisposalRegisterController() throws SQLException {}


    @FXML
    public void initialize() {
        super.initialize();

        try {
            System.out.println("[Init] Start");

            Optional<List<CompanyInfo>> processors = disposalService.getAllProcessCompanies();
            Optional<List<PopupManagement>> popups = disposalService.getAllPopupStores();

            List<String> processorNames = new ArrayList<>();
            if (processors.isPresent()) {
                for (CompanyInfo company : processors.get()) {
                    System.out.println("[Init] Loaded processor: " + company.getCompanyName() + " (userId: " + company.getId() + ")");
                    processorCompanyNameToId.put(company.getCompanyName(), company.getId());
                    processorNames.add(company.getCompanyName());
                }
            } else {
                System.out.println("[Init] No processor companies found");
            }
            processorComboBox.setItems(FXCollections.observableArrayList(processorNames));

            List<String> popupNames = new ArrayList<>();
            if (popups.isPresent()) {
                for (PopupManagement popup : popups.get()) {
                    System.out.println("[Init] Loaded popup: " + popup.getName() + " (popupId: " + popup.getPopupId() + ")");
                    popupNameToId.put(popup.getName(), popup.getPopupId());
                    popupNames.add(popup.getName());
                }
            } else {
                System.out.println("[Init] No popup stores found");
            }
            popupComboBox.setItems(FXCollections.observableArrayList(popupNames));

            statusComboBox.setItems(FXCollections.observableArrayList("완료", "대기", "보류"));

            System.out.println("[Init] Complete");

        } catch (Exception e) {
            System.out.println("[Init Error] " + e.getMessage());
            e.printStackTrace();
            messageLabel.setText("Error during initialization: " + e.getMessage());
        }
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
        System.out.println("[Register Start]");
        String selectedPopupName = popupComboBox.getValue();
        String selectedProcessCompanyName = processorComboBox.getValue();
        LocalDate date = disposalDate.getValue();
        String status = statusComboBox.getValue();

        System.out.println("[Register] Selected popup: " + selectedPopupName);
        System.out.println("[Register] Selected company: " + selectedProcessCompanyName);
        System.out.println("[Register] Selected date: " + date);
        System.out.println("[Register] Selected status: " + status);

        if (selectedProcessCompanyName == null || selectedPopupName == null || date == null || status == null) {
            System.out.println("[Register Error] Missing required fields");
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            Integer companyId = processorCompanyNameToId.get(selectedProcessCompanyName);
            Integer popupId = popupNameToId.get(selectedPopupName);
            Integer userId = companyRepository.findUserIdByCompanyName(selectedProcessCompanyName);

            System.out.println("[Register] Retrieved companyId: " + companyId + ", popupId: " + popupId + ", userId: " + userId);

            Waste waste = new Waste();
            waste.setAmount(Integer.parseInt(amountField.getText())); // 숫자 변환 필요
            waste.setType(wasteTypeField.getText());


            DisposalRecord record = new DisposalRecord();
            record.setUserId(userId);
            record.setPopupId(popupId);
            record.setDisposalDate(date.atStartOfDay());
            record.setStatus(status);

            System.out.println("[Register] Record created, ready to register");

            disposalService.registerDisposalRecord(record, waste, currentUser.getId());

            System.out.println("[Register Complete] Disposal record successfully registered");
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Disposal record registered successfully!");

        } catch (BusinessException e) {
            System.out.println("[BusinessException] " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {
            System.out.println("[SystemException] " + e.getMessage());
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("System error occurred");
        }
    }
}

