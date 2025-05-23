package db2025.DB2025Team05_poppop.DB2025Team05_controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class PopupSelectController {
    @FXML private TableView<PopupCompanyView> popupTableView;
    @FXML private TableColumn<PopupCompanyView, String> popupIdColumn;
    @FXML private TableColumn<PopupCompanyView, String> popupNameColumn;
    @FXML private TableColumn<PopupCompanyView, String> companyNameColumn;


    private ObservableList<PopupCompanyView> popupData;
    private String selectedPopupId;

    @FXML
    public void initialize() {
        popupIdColumn.setCellValueFactory(cellData -> cellData.getValue().popupIdProperty());
        popupNameColumn.setCellValueFactory(cellData -> cellData.getValue().popupNameProperty());
        companyNameColumn.setCellValueFactory(cellData -> cellData.getValue().companyNameProperty());

        popupData = FXCollections.observableArrayList(BackendService.getPopupCompanyView());
        popupTableView.setItems(popupData);

        popupTableView.setOnMouseClicked(event -> {
            PopupCompanyView selected = popupTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedPopupId = selected.getPopupId();
            }
        });
    }

    @FXML
    private void handleNext(ActionEvent event) {
        if (selectedPopupId == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/processor_select.fxml"));
            Parent root = loader.load();

            ProcessorSelectController controller = loader.getController();
            controller.setSelectedPopupId(selectedPopupId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("ó����ü ����");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
