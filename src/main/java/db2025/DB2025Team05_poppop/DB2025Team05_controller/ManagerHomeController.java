package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ManagerHomeController extends BaseController {

    private int managerId; // 필요 시 로그인 시점에서 주입

    public void setManagerId(int id) {
        this.managerId = id;
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
