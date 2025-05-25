//package db2025.DB2025Team05_poppop.DB2025Team05_controller;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//
//import db2025.DB2025Team05_poppop.DB2025Team05_domain.*;
//import db2025.DB2025Team05_poppop.DB2025Team05_service.UserService;
//import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
//
//import java.sql.Connection;
//
//public class SignupBasicController {
//    @FXML private TextField emailField;
//    @FXML private TextField nameField;
//    @FXML private ToggleButton producerButton;
//    @FXML private ToggleButton managerButton;
//    @FXML private Button nextButton;
//    @FXML private Label messageLabel;
//
//    @FXML
//    private void handleNext(ActionEvent event) {
//        String email = emailField.getText();
//        String name = nameField.getText();
//        Role role = producerButton.isSelected() ? Role.PRODUCER :
//                    managerButton.isSelected() ? Role.MANAGER : null;
//
//        if (email.isEmpty() || name.isEmpty()) {
//            messageLabel.setText("이메일과 이름을 입력해주세요.");
//            return;
//        }
//
//        if (role == null) {
//            messageLabel.setText("역할을 선택해주세요.");
//            return;
//        }
//
//        if (role == Role.MANAGER) {
//            // manager는 여기서 바로 회원가입 처리
//            try {
//                DB2025_USER user = new DB2025_USER();
//                user.setEmail(email);
//                user.setName(name);
//                user.setRole(role);
//
//                Connection conn = DBConnection.getConnection();
//                UserService userService = new UserService(conn);
//
//                boolean result = userService.registerUser(user, null, null, null, null);
//
//                if (result) {
//                    messageLabel.setStyle("-fx-text-fill: green;");
//                    messageLabel.setText("회원가입 완료!");
//                } else {
//                    messageLabel.setStyle("-fx-text-fill: red;");
//                    messageLabel.setText("회원가입 실패 (중복 이메일 등)");
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                messageLabel.setText("회원가입 중 오류 발생");
//            }
//
//        } else {
//            // producer �� ȸ�� ���� �Է� ȭ������ �̵�
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup_company.fxml"));
//                Parent root = loader.load();
//
//                SignupProducerController controller = loader.getController();
//                controller.setBasicInfo(email, name, role.name()); // String���� �ѱ�
//
//                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//                stage.setScene(new Scene(root));
//                stage.setTitle("회사 정보 입력");
//                stage.show();
//            } catch (Exception e) {
//                e.printStackTrace();
//                messageLabel.setText("회사 정보 화면 전환 실패");
//            }
//        }
//    }
//}
