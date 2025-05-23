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
            messageLabel.setText("������ �ҷ����� ����");
        }

        // ���̺� Ŭ�� �̺�Ʈ
        popupTableView.setOnMouseClicked((MouseEvent event) -> {
            PopupCompanyView selected = popupTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedPopupId = selected.getPopupId();  // �ʿ� �� ���޿� ID ����
                messageLabel.setText("���õ� �˾� ID: " + selectedPopupId);
            }
        });
    }

    @FXML
    private void handleNext() {
        if (selectedPopupId == -1) {
            messageLabel.setText("�˾��� �������ּ���.");
            return;
        }

        // ���� ȭ������ popupId �ѱ�� (����)
        try {
            // FXMLLoader, setController, setPopupId �� ó��
            // ��: processor_select.fxml �� ��ȯ
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("ȭ�� ��ȯ ����");
        }
    }

}
