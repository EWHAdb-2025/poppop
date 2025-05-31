package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupManagement;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
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

public class MyPopupListController extends BaseController {

    @FXML private TableView<PopupManagement> popupTable;
    @FXML private TableColumn<PopupManagement, String> colName;
    @FXML private TableColumn<PopupManagement, String> colAddress;
    @FXML private TableColumn<PopupManagement, String> colStartDate;
    @FXML private TableColumn<PopupManagement, String> colEndDate;
    @FXML private Button backButton;

    private final PopupRepository popupRepo = new PopupRepository();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MyPopupListController() throws SQLException {}

    @FXML
    public void initialize() {
        super.initialize();

        colName.setCellValueFactory(c -> c.getValue().nameProperty());
        colAddress.setCellValueFactory(c -> c.getValue().addressProperty());
        colStartDate.setCellValueFactory(c -> c.getValue().startDateProperty().asString(fmt));
        colEndDate.setCellValueFactory(c -> c.getValue().endDateProperty().asString(fmt));

        loadTable();  // 초기 데이터 로드
    }

    private void loadTable() {
        try {
            int producerId = currentUser.getId();  // BaseController에서 가져옴
            ObservableList<PopupManagement> data = FXCollections.observableArrayList(
                    popupRepo.findByProducerId(producerId)
            );
            popupTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("팝업 목록 조회 중 오류가 발생했습니다.");
        }
    }

    @FXML
    private void handleBack(ActionEvent e) {
        super.handleBack(backButton, "/producer_home.fxml");
    }

    @FXML
    private void handleEdit(ActionEvent e) {
        PopupManagement selected = popupTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/popup_edit.fxml"));
            Parent root = loader.load();

            PopupEditController ctrl = loader.getController();
            ctrl.setPopup(selected);

            Stage dialog = new Stage();
            dialog.initOwner(((Node) e.getSource()).getScene().getWindow());
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setScene(new Scene(root));
            dialog.setTitle("팝업 수정");
            dialog.showAndWait();

            loadTable();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("팝업 수정 화면 로드 중 오류가 발생했습니다.");
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
                    popupRepo.deletePopup(selected.getPopupId());
                    loadTable();
                } catch (BusinessException be) {
                    showAlert(be.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert("시스템 오류가 발생했습니다.");
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
