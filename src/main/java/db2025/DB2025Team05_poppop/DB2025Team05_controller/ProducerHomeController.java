package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Producer(생산자) 홈 화면 컨트롤러
 * 1) 팝업 등록 화면
 * 2) 내 팝업 목록 화면
 * 3) 이전(로그인) 화면
 *
 * ManagerHomeController 와 같은 스타일(핸들러마다 직접 Scene 전환)
 */
public class ProducerHomeController {

    /* 필요한 경우 로그인 시점에서 주입 */
    private int producerId;

    public void setProducerId(int id) {
        this.producerId = id;
    }

    /** ① 팝업스토어 등록 화면으로 */
    @FXML
    private void handleRegisterPopup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/popup_register.fxml"));
            Parent root = loader.load();

            // PopupRegisterController controller = loader.getController();
            // controller.setProducerId(producerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("팝업스토어 등록");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** ② 내 팝업 보기 화면으로 */
    @FXML
    private void handleViewMyPopup(ActionEvent event) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/popup_list.fxml"));
            Parent root = loader.load();

            // MyPopupListController controller = loader.getController();
            // controller.setProducerId(producerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("내 팝업 목록");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** ③ 이전(로그인) 화면으로 */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root =
                    FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("로그인");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
