package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.*;
import db2025.DB2025Team05_poppop.DB2025Team05_service.DisposalService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DisposalStatsController extends BaseController {

    @FXML private TableView<Map<String, Object>> statsTableView;
    @FXML private Button companyStatsBtn;
    @FXML private Button popupStatsBtn;
    @FXML private Button monthStatsBtn;
    @FXML
    private Button backButton;
    @FXML private Label messageLabel;

    private int managerId;

    private DisposalService service;

    public void setManagerId(int id) {
        this.managerId = id;
    }

    @FXML
    public void initialize() {
        super.initialize();
        try {
            service = new DisposalService(
                    new DispRecRepository(),
                    new UserRepository(),
                    new PopupRepository(),
                    new WasteRepository(),
                    new CompanyRepository()
            );
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("서비스 초기화 중 오류 발생");
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
    private void handleCompanyStats() {
        try {
            Optional<List<Map<String, Object>>> resultOpt = service.getDisposalStatisticsByCompanyname(managerId, "");
            resultOpt.ifPresent(result -> updateTable("회사명", "companyName", result));
        } catch (BusinessException e) {
            showAlert(e.getMessage());
        }
    }

    @FXML
    private void handlePopupStats() {
        try {
            Optional<List<Map<String, Object>>> resultOpt =
                    service.getDisposalStatisticsByPopup(managerId, "");  // 빈 문자열로 전체 조회
            resultOpt.ifPresent(result -> updateTable("팝업 ID", "popupId", result));
        } catch (BusinessException e) {
            showAlert(e.getMessage());
        }
    }

    @FXML
    private void handleMonthStats() {
        try {
            int year = LocalDate.now().getYear();
            int month = LocalDate.now().getMonthValue();
            Optional<List<Map<String, Object>>> resultOpt =
                    service.getDisposalStatisticsByMonth(managerId, year, month);  // 연도, 월 전달
            resultOpt.ifPresent(result -> updateTable("월", "month", result));
        } catch (BusinessException e) {
            showAlert(e.getMessage());
        }
    }

    private void updateTable(String column1Header, String column1Key, List<Map<String, Object>> data) {
        statsTableView.getItems().clear();
        statsTableView.getColumns().clear();

        TableColumn<Map<String, Object>, String> col1 = new TableColumn<>(column1Header);
        col1.setCellValueFactory(param -> {
            Object value = param.getValue().get(column1Key);
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(value));
        });

        TableColumn<Map<String, Object>, String> col2 = new TableColumn<>("총 처리 건수");
        col2.setCellValueFactory(param -> {
            Object value = param.getValue().get("totalDisposal");
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(value));
        });

        statsTableView.getColumns().addAll(col1, col2);

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

