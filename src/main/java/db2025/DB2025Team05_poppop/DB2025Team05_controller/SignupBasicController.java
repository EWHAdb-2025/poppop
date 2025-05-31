package db2025.DB2025Team05_poppop.DB2025Team05_controller;

import db2025.DB2025Team05_poppop.DB2025Team05_common.Role;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.CompanyRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.UserRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.sql.SQLException;

public class SignupBasicController extends BaseController {

    @FXML private TextField emailField;
    @FXML private TextField nameField;
    @FXML private ToggleButton producerButton;
    @FXML private ToggleButton managerButton;
    @FXML private Button nextButton;
    @FXML private Label messageLabel;
    @FXML private Button backButton;

    private final UserService userService = new UserService(
            new UserRepository(),
            new CompanyRepository()
    );

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

    public SignupBasicController() throws SQLException {
    }

    @FXML
    private void handleNext(ActionEvent event) {
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        Role role = producerButton.isSelected() ? Role.PRODUCER :
                managerButton.isSelected() ? Role.MANAGER : null;

        if (email.isEmpty() || name.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("이메일과 이름을 입력해주세요.");
            return;
        }

        if (role == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("역할을 선택해주세요.");
            return;
        }

        if (role == Role.MANAGER) {
            try {
                User user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setRole(role);

                userService.registerUser(user, null);  // 회사 정보 없음

                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("회원가입 완료!");

            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("회원가입 중 오류 발생");
            }

        } else {
            // 생산자 → 회사 정보 입력 화면으로 이동
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup_producer.fxml"));
                Parent root = loader.load();

                SignupProducerController controller = loader.getController();
                controller.setBasicInfo(email, name, role.name());

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("회사 정보 입력");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("회사 정보 화면 전환 실패");
            }
        }
    }

}




