package db2025.DB2025Team05_poppop.DB2025Team05_controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class ProcessorSelectController {
    @FXML private TableView<Processor> processorTableView;
    @FXML private TableColumn<Processor, String> processorIdColumn;
    @FXML private TableColumn<Processor, String> companyNameColumn;
    @FXML private TableColumn<Processor, String> ceoNameColumn;

    private ObservableList<Processor> processorData;
    private String selectedProcessorId;
    private String selectedPopupId; 

    public void setSelectedPopupId(String popupId) {
        this.selectedPopupId = popupId;
    }

    @FXML
    public void initialize() {
        processorIdColumn.setCellValueFactory(cellData -> cellData.getValue().processorIdProperty());
        companyNameColumn.setCellValueFactory(cellData -> cellData.getValue().companyNameProperty());
        ceoNameColumn.setCellValueFactory(cellData -> cellData.getValue().ceoNameProperty());

        processorData = FXCollections.observableArrayList(BackendService.getProcessorList());
        processorTableView.setItems(processorData);

        processorTableView.setOnMouseClicked(event -> {
            Processor selected = processorTableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedProcessorId = selected.getProcessorId();
            }
        });
    }

    @FXML
    private void handleNext(ActionEvent event) {
        if (selectedProcessorId == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/disposal_register.fxml"));
            Parent root = loader.load();

            DisposalRegisterController controller = loader.getController();
            controller.setSelectedInfo(selectedPopupId, selectedProcessorId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("��⹰ ó�� �Է�");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
