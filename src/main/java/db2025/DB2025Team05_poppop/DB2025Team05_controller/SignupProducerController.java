//package db2025.DB2025Team05_poppop.DB2025Team05_controller;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.event.ActionEvent;
//
//import db2025.DB2025Team05_poppop.DB2025Team05_domain.*;
//import db2025.DB2025Team05_poppop.DB2025Team05_service.UserService;
//import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
//
//import java.sql.Connection;
//
//public class SignupProducerController {
//
//    @FXML private TextField companyNameField;
//    @FXML private TextField bizNumberField;
//    @FXML private TextField ceoNameField;
//    @FXML private TextField ceoPhoneField;
//    @FXML private Label messageLabel;
//
//    private String email;
//    private String name;
//    private Role role;
//
//    // SignupBasicController���� ȣ���
//    public void setBasicInfo(String email, String name, String roleStr) {
//        this.email = email;
//        this.name = name;
//        this.role = Role.valueOf(roleStr); // ���ڿ� �� enum
//    }
//
//    @FXML
//    private void handleRegister(ActionEvent event) {
//        String companyName = companyNameField.getText();
//        String businessNumber = bizNumberField.getText();
//        String ceoName = ceoNameField.getText();
//        String ceoPhone = ceoPhoneField.getText();
//
//        try {
//            DB2025_USER user = new DB2025_USER();
//            user.setEmail(email);
//            user.setName(name);
//            user.setRole(role);
//
//            Connection conn = DBConnection.getConnection();
//            UserService userService = new UserService(conn);
//
//            boolean success = userService.registerUser(
//                user, companyName, businessNumber, ceoName, ceoPhone
//            );
//
//            if (success) {
//                messageLabel.setStyle("-fx-text-fill: green;");
//                messageLabel.setText("회원가입 완료!");
//            } else {
//                messageLabel.setStyle("-fx-text-fill: red;");
//                messageLabel.setText("회원가입 실패 (입력값 또는 중복 오류)");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            messageLabel.setStyle("-fx-text-fill: red;");
//            messageLabel.setText("회원가입 처리 중 오류 발생");
//        }
//    }
//
//}
