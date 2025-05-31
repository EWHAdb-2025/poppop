package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.CompanyInfo;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.*;
import db2025.DB2025Team05_poppop.DB2025Team05_service.DisposalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.*;

public class DisposalStatsController extends BaseController {

    @FXML private TableView<Map<String, Object>> statsTableView;
    @FXML private Button companyStatsBtn;
    @FXML private Button popupStatsBtn;
    @FXML private Button monthStatsBtn;
    @FXML
    private Button backButton;
    @FXML private Label messageLabel;
    @FXML private ComboBox<String> companyComboBox;
    @FXML private ComboBox<String> popupComboBox;
    @FXML private Button clearCompanyBtn;
    @FXML private Button clearPopupBtn;

    private DisposalService disposalService;

    // Maps for name → ID
    private final Map<String, Integer> processorCompanyNameToId = new HashMap<>();
    private final Map<String, Integer> popupNameToId = new HashMap<>();


    @FXML
    public void initialize() {
        super.initialize();
        try {
            disposalService = new DisposalService(
                    new DispRecRepository(),
                    new UserRepository(),
                    new PopupRepository(),
                    new WasteRepository(),
                    new CompanyRepository()
            );
            // ComboBox 선택 시 이벤트 연결
            companyComboBox.setOnAction(e -> {
                if (companyComboBox.getValue() != null) {
                    popupComboBox.setDisable(true);
                    clearCompanyBtn.setDisable(false);
                    clearPopupBtn.setDisable(true);
                }
            });

            popupComboBox.setOnAction(e -> {
                if (popupComboBox.getValue() != null) {
                    companyComboBox.setDisable(true);
                    clearPopupBtn.setDisable(false);
                    clearCompanyBtn.setDisable(true);
                }
            });

            // 초기에는 취소 버튼 비활성화
            clearCompanyBtn.setDisable(true);
            clearPopupBtn.setDisable(true);

            Optional<List<CompanyInfo>> processors = disposalService.getAllProcessCompanies();
            Optional<List<PopupManagement>> popups = disposalService.getAllPopupStores();

            List<String> companyNames = new ArrayList<>();
            if (processors.isPresent()) {
                for (CompanyInfo company : processors.get()) {
                    System.out.println("[Init] Loaded processor: " + company.getCompanyName() + " (userId: " + company.getId() + ")");
                    processorCompanyNameToId.put(company.getCompanyName(), company.getId());
                    companyNames.add(company.getCompanyName());
                }
            } else {
                System.out.println("[Init] No processor companies found");
            }
            companyComboBox.setItems(FXCollections.observableArrayList(companyNames));

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

            System.out.println("[Init] Complete");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("서비스 초기화 중 오류 발생");
        }
    }
    @FXML
    private void handleClearCompany() {
        companyComboBox.setValue(null);
        companyComboBox.setDisable(false);
        popupComboBox.setDisable(false);
        clearCompanyBtn.setDisable(true);
        clearPopupBtn.setDisable(true);
    }

    @FXML
    private void handleClearPopup() {
        popupComboBox.setValue(null);
        popupComboBox.setDisable(false);
        companyComboBox.setDisable(false);
        clearPopupBtn.setDisable(true);
        clearCompanyBtn.setDisable(true);
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
    private void handleCompanySearch() {
        try {
            String selectedCompany = companyComboBox.getValue();
            if (selectedCompany == null || selectedCompany.isBlank()) {
                showAlert("회사명을 선택해주세요.");
                return;
            }

            Optional<List<Map<String, Object>>> resultOpt =
                    disposalService.getDisposalStatisticsByCompanyname(currentUser.getId(), selectedCompany);
            resultOpt.ifPresent(result -> updateTable("회사명", "companyName", result));
        } catch (BusinessException e) {
            showAlert(e.getMessage());
        }
    }

    @FXML
    private void handlePopupSearch() {
        try {
            String selectedPopup = popupComboBox.getValue();
            if (selectedPopup == null || selectedPopup.isBlank()) {
                showAlert("팝업스토어를 선택해주세요.");
                return;
            }

            Optional<List<Map<String, Object>>> resultOpt =
                    disposalService.getDisposalStatisticsByPopup(currentUser.getId(), selectedPopup);
            resultOpt.ifPresent(result -> updateTable("팝업 ID", "popupId", result));
        } catch (BusinessException e) {
            showAlert(e.getMessage());
        }
    }


    private void updateTable(String column1Header, String column1Key, List<Map<String, Object>> data) {
        statsTableView.getItems().clear();
        statsTableView.getColumns().clear();

        TableColumn<Map<String, Object>, String> col1 = new TableColumn<>("팝업명");
        col1.setCellValueFactory(param -> {
            Object value = param.getValue().get("popupName");
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(value));
        });
        //type
        TableColumn<Map<String, Object>, String> col2 = new TableColumn<>("폐기물 종류");
        col2.setCellValueFactory(param -> {
            Object value = param.getValue().get("type");
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(value));
        });

        TableColumn<Map<String, Object>, String> col3 = new TableColumn<>("처리량 (kg)");
        col3.setCellValueFactory(param -> {
            Object value = param.getValue().get("amount"); // key must exist in Map
            return new SimpleStringProperty(String.valueOf(value));
        });

        statsTableView.getColumns().addAll(col1, col2, col3);

        ObservableList<Map<String, Object>> observableList = FXCollections.observableArrayList(data);
        statsTableView.setItems(observableList);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("오류");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

