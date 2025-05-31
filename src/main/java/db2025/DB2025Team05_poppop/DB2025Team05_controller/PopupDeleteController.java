package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_common.AppSession;
import db2025.DB2025Team05_poppop.DB2025Team05_exception.BusinessException;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_service.PopupStoreService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.SQLException;

public class PopupDeleteController extends BaseController {

    private PopupStoreService popupStoreService;

    private int popupId; // 삭제 대상 팝업 ID

    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    public void setPopupId(int popupId) {
        this.popupId = popupId;
    }

    @FXML
    public void initialize() {
        super.initialize();

        try {
            popupStoreService = new PopupStoreService(
                    new PopupRepository(),
                    new UserRepository()
            );
        } catch (SQLException e) {
            showAlert("삭제 준비 중 오류가 발생했습니다.");
        }
    }

    @FXML
    private void handleDelete() {
        try {
            popupStoreService.deletePopupStore(popupId, currentUser.getId());  // OK
            showInfo("팝업스토어가 성공적으로 삭제되었습니다.");
            closeWindow();
        } catch (BusinessException e) {
            showAlert(e.getMessage());
        }
    }


    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) deleteButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("삭제 실패");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("삭제 성공");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
