package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_repository.*;
import db2025.DB2025Team05_poppop.DB2025Team05_service.DisposalService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ManagerHomeController extends BaseController {
    @FXML
    private Button backButton;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        super.initialize();
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
    private void handleRegisterProcessor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/processor_register.fxml"));
            Parent root = loader.load();

            // 필요 시 Controller에 managerId 주입
            // ProcessorRegisterController controller = loader.getController();
            // controller.setManagerId(managerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("처리자 등록");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDisposalRecord(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/disposal_register.fxml"));
            Parent root = loader.load();

            // DisposalRegisterController controller = loader.getController();
            // controller.setManagerId(managerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("폐기물 처리 이력 입력");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStatistics(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/disposal_stats.fxml"));
            Parent root = loader.load();

            // DisposalStatsController controller = loader.getController();
            // controller.setManagerId(managerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("폐기물 통계 조회");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
