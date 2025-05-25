//package db2025.DB2025Team05_poppop.DB2025Team05_controller;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.beans.property.ReadOnlyStringWrapper;
//
//import db2025.DB2025Team05_poppop.DB2025Team05_service.DisposalService;
//import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
//
//import java.sql.Connection;
//import java.util.*;
//
//public class DisposalStatsController {
//    @FXML private Button companyStatsBtn;
//    @FXML private Button popupStatsBtn;
//    @FXML private Button monthStatsBtn;
//
//    @FXML private TableView<Map<String, Object>> statsTableView;
//    @FXML private TableColumn<Map<String, Object>, String> columnX;
//    @FXML private TableColumn<Map<String, Object>, String> countColumn;
//
//    private DisposalService disposalService;
//
//    @FXML
//    public void initialize() {
//        try {
//            Connection conn = DBConnection.getConnection();
//            disposalService = new DisposalService(conn);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        countColumn.setCellValueFactory(data -> {
//            Object value = data.getValue().get("totalDisposal");
//            return new ReadOnlyStringWrapper(value == null ? "" : value.toString());
//        });
//    }
//
//    @FXML
//    private void handleCompanyStats() {
//        List<Map<String, Object>> data = disposalService.getDisposalStatisticsByCompany();
//        columnX.setText("회사명");
//        columnX.setCellValueFactory(row -> {
//            Object val = row.getValue().get("companyName");
//            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
//        });
//        updateTable(data);
//    }
//
//    @FXML
//    private void handlePopupStats() {
//        List<Map<String, Object>> data = disposalService.getDisposalStatisticsByPopup();
//        columnX.setText("팝업 ID");
//        columnX.setCellValueFactory(row -> {
//            Object val = row.getValue().get("popupId");
//            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
//        });
//        updateTable(data);
//    }
//
//    @FXML
//    private void handleMonthStats() {
//        List<Map<String, Object>> data = disposalService.getDisposalStatisticsByMonth();
//        columnX.setText("월");
//        columnX.setCellValueFactory(row -> {
//            Object val = row.getValue().get("month");
//            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
//        });
//        updateTable(data);
//    }
//
//    private void updateTable(List<Map<String, Object>> data) {
//        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList(data);
//        statsTableView.setItems(list);
//    }
//
//}
