package db2025.DB2025Team05_poppop.DB2025Team05_controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;

import db2025.DB2025Team05_poppop.DB2025Team05_common.DBConnection;
import db2025.DB2025Team05_poppop.DB2025Team05_repository.PopupRepository;
import db2025.DB2025Team05_poppop.DB2025Team05_domain.PopupCompanyView;

import java.sql.Connection;
import java.util.List;

public class PopupSelectController {
    @FXML private TableView<PopupCompanyView> popupTableView;
    @FXML private TableColumn<PopupCompanyView, String> popupNameColumn;
    @FXML private TableColumn<PopupCompanyView, String> popupEndDateColumn;
    @FXML private TableColumn<PopupCompanyView, String> companyNameColumn;
    @FXML private TableColumn<PopupCompanyView, String> popupIdColumn;
    @FXML private Label messageLabel;
    @FXML private Button nextButton;

    private ObservableList<PopupCompanyView> popupData;
    private int selectedPopupId = -1;

    @FXML
    public void initialize() {
        popupNameColumn.setCellValueFactory(cellData -> cellData.getValue().popupNameProperty());
        popupEndDateColumn.setCellValueFactory(cellData -> cellData.getValue().popupEndDateProperty());
        companyNameColumn.setCellValueFactory(cellData -> cellData.getValue().companyNameProperty());
        popupIdColumn.setCellValueFactory(cellData -> cellData.getValue().popupIdProperty());


        try {
            Connection conn = DBConnection.getConnection();
            PopupRepository popupRepo = new PopupRepository(conn);

            List<PopupCompanyView> list = popupRepo.getPopupCompanyView();
            popupData = FXCollections.observableArrayList(list);
            popupTableView.setItems(popupData);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("데이터 불러오기 실패");
        }

        // 테이블 클릭 이벤트
        popupTableView.setOnMouseClicked((MouseEvent event) -> {
            PopupCompanyView selected = popupTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedPopupId = selected.getPopupId();  // 필요 시 전달용 ID 저장
                messageLabel.setText("선택된 팝업 ID: " + selectedPopupId);
            }
        });
    }

    @FXML
    private void handleNext() {
        if (selectedPopupId == -1) {
            messageLabel.setText("팝업을 선택해주세요.");
            return;
        }

        // 다음 화면으로 popupId 넘기기 (예시)
        try {
            // FXMLLoader, setController, setPopupId 등 처리
            // 예: processor_select.fxml 로 전환
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("화면 전환 실패");
        }
    }

}
