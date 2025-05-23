package db2025.DB2025Team05_poppop.DB2025Team05_controller;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.event.ActionEvent;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.*;
import db2025.DB2025Team05_poppop.DB2025Team05_service.UserService;
import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;

import java.sql.Connection;

public class ProcessorRegisterController {
    @FXML private TextField emailField;
    @FXML private TextField nameField;
    @FXML private TextField companyNameField;
    @FXML private TextField bizNumberField;
    @FXML private TextField ceoNameField;
    @FXML private TextField ceoPhoneField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister(ActionEvent event) {
        String email = emailField.getText();
        String name = nameField.getText();
        String companyName = companyNameField.getText();
        String businessNumber = bizNumberField.getText();
        String ceoName = ceoNameField.getText();
        String ceoPhone = ceoPhoneField.getText();

        try {
            DB2025_USER user = new DB2025_USER();
            user.setEmail(email);
            user.setName(name);
            user.setRole(Role.PROCESSOR);

            Connection conn = DBConnection.getConnection();
            UserService userService = new UserService(conn);

            boolean result = userService.registerUser(
                user, companyName, businessNumber, ceoName, ceoPhone
            );

            if (result) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("처리업체 등록 완료!");
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("등록 실패 (입력 오류 또는 중복 이메일)");
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("등록 중 오류 발생");
        }
    }


}
