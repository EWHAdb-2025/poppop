package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_service.PopupStoreService;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyPopupListController extends BaseController {

    @FXML private TableView<PopupManagement> popupTable;
    @FXML private TableColumn<PopupManagement, String> colName;
    @FXML private TableColumn<PopupManagement, String> colAddress;
    @FXML private TableColumn<PopupManagement, String> colStartDate;
    @FXML private TableColumn<PopupManagement, String> colEndDate;
    @FXML private Button backButton;
    @FXML private Label messageLabel;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private PopupStoreService popupStoreService;
    private PopupRepository popupRepository = new PopupRepository();

    public MyPopupListController() throws SQLException {}

    @FXML
    public void initialize() {
        super.initialize();

        try {
            popupStoreService = new PopupStoreService(
                    new PopupRepository(),
                    new UserRepository()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("서비스 초기화 중 오류가 발생했습니다.");
            return;
        }

        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colAddress.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAddress()));
        colStartDate.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStartDate().format(fmt)));
        colEndDate.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEndDate().format(fmt)));


        loadTable(); // 초기 데이터 로딩
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/producer_home.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Producer 홈");
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


    private void loadTable() {
        try {
            int producerId = currentUser.getId();
            List<PopupManagement> popups = popupRepository.findPopupByUserId(producerId);

            ObservableList<PopupManagement> data = FXCollections.observableArrayList(popups);
            popupTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("팝업 목록을 불러오는 데 실패했습니다.");
        }
    }

    @FXML
    private void handleEdit(ActionEvent e) {
        PopupManagement selectedPopup = popupTable.getSelectionModel().getSelectedItem();
        if (selectedPopup == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/popup_edit.fxml"));
            Parent root = loader.load();

            PopupEditController popupEditController = loader.getController();
            popupEditController.setPopup(selectedPopup);

            Stage dialog = new Stage();
            dialog.initOwner(((Node) e.getSource()).getScene().getWindow());
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setScene(new Scene(root));
            dialog.setTitle("팝업 수정");
            dialog.showAndWait();

            loadTable(); // 편집 후 갱신
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("팝업 수정 화면을 불러오는 데 실패했습니다.");
        }
    }

    @FXML
    private void handleDelete(ActionEvent e) {
        PopupManagement selected = popupTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "팝업 '" + selected.getName() + "'을(를) 삭제하시겠습니까?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    popupStoreService.deletePopupStore(
                            selected.getPopupId(),
                            currentUser.getId()
                    );
                    loadTable();
                } catch (BusinessException be) {
                    showAlert(be.getMessage());
                }
            }
        });
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("오류");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
